package com.tercergrupo.controller;

import com.tercergrupo.config.ConexionBaseDatos;
import com.tercergrupo.model.Cita;
import com.tercergrupo.model.Factura;
import com.tercergrupo.model.Permiso;
import com.tercergrupo.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FacturacionController {
    private final String urlPruebas;

    public FacturacionController() {
        this(null);
    }

    // Las pruebas utilizan un archivo temporal, sin tocar data/medcita.db.
    FacturacionController(String urlPruebas) {
        this.urlPruebas = urlPruebas;
    }

    private Connection abrirConexion() throws SQLException {
        return urlPruebas == null ? ConexionBaseDatos.abrir() : DriverManager.getConnection(urlPruebas);
    }

    public List<Cita> listarCitasSinFactura(User usuario) throws SQLException {
        exigirPermiso(usuario, Permiso.VER_FACTURAS);
        String sql = "SELECT c.* FROM citas c WHERE NOT EXISTS "
                + "(SELECT 1 FROM facturas f WHERE f.cita_id = c.id_cita) ORDER BY c.fecha, c.hora";
        List<Cita> citas = new ArrayList<>();
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                citas.add(new Cita(resultado.getLong("id_cita"), resultado.getString("paciente"),
                        resultado.getString("doctor_correo"), LocalDate.parse(resultado.getString("fecha")),
                        LocalTime.parse(resultado.getString("hora"))));
            }
        }
        return citas;
    }

    public boolean estaAtendida(User usuario, long citaId) throws SQLException {
        exigirPermiso(usuario, Permiso.VER_FACTURAS);
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "SELECT 1 FROM citas_atendidas WHERE cita_id = ?")) {
            sentencia.setLong(1, citaId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public void marcarAtendida(User usuario, long citaId) throws SQLException {
        exigirPermiso(usuario, Permiso.CREAR_FACTURA);
        try (Connection conexion = abrirConexion()) {
            try (PreparedStatement buscar = conexion.prepareStatement("SELECT fecha, hora FROM citas WHERE id_cita = ?")) {
                buscar.setLong(1, citaId);
                try (ResultSet resultado = buscar.executeQuery()) {
                    if (!resultado.next()) {
                        throw new IllegalArgumentException("La cita no existe.");
                    }
                    LocalDateTime horario = LocalDateTime.of(LocalDate.parse(resultado.getString("fecha")),
                            LocalTime.parse(resultado.getString("hora")));
                    if (horario.isAfter(LocalDateTime.now())) {
                        throw new IllegalArgumentException("No puedes confirmar una cita que todavía no ha ocurrido.");
                    }
                }
            }
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    "INSERT INTO citas_atendidas (cita_id, confirmado_por) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
                sentencia.setLong(1, citaId);
                sentencia.setString(2, usuario.getCorreoElectronico());
                sentencia.executeUpdate();
            }
        }
    }

    public void generarFactura(User usuario, long citaId, String concepto, BigDecimal total) throws SQLException {
        exigirPermiso(usuario, Permiso.CREAR_FACTURA);
        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException("El concepto es obligatorio.");
        }
        long centavos = convertirCentavos(total);
        // Se comprueba la atención y se copian los datos en una sola sentencia.
        String sql = """
            INSERT INTO facturas (cita_id, paciente, doctor_correo, fecha_cita, fecha_emision,
                concepto, total_centavos)
            SELECT c.id_cita, c.paciente, c.doctor_correo, c.fecha, ?, ?, ? FROM citas c
            JOIN citas_atendidas a ON a.cita_id = c.id_cita
            WHERE c.id_cita = ? AND NOT EXISTS (SELECT 1 FROM facturas f WHERE f.cita_id = c.id_cita)
            """;
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, LocalDate.now().toString());
            sentencia.setString(2, concepto.trim());
            sentencia.setLong(3, centavos);
            sentencia.setLong(4, citaId);
            if (sentencia.executeUpdate() != 1) {
                throw new IllegalArgumentException("La cita debe estar atendida y no tener una factura previa.");
            }
        }
    }

    public List<Factura> listarFacturas(User usuario) throws SQLException {
        exigirPermiso(usuario, Permiso.VER_FACTURAS);
        List<Factura> facturas = new ArrayList<>();
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement("SELECT * FROM facturas ORDER BY id DESC");
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                facturas.add(new Factura(resultado.getLong("id"), resultado.getLong("cita_id"),
                        resultado.getString("paciente"), resultado.getString("doctor_correo"),
                        LocalDate.parse(resultado.getString("fecha_cita")),
                        LocalDate.parse(resultado.getString("fecha_emision")), resultado.getString("concepto"),
                        resultado.getLong("total_centavos"), resultado.getLong("pagado_centavos")));
            }
        }
        return facturas;
    }

    public void registrarPago(User usuario, long facturaId, BigDecimal monto) throws SQLException {
        exigirPermiso(usuario, Permiso.CREAR_FACTURA);
        long centavos = convertirCentavos(monto);
        // El límite se verifica en el UPDATE para impedir que se cobre más que el saldo.
        String sql = "UPDATE facturas SET pagado_centavos = pagado_centavos + ? "
                + "WHERE id = ? AND total_centavos - pagado_centavos >= ?";
        try (Connection conexion = abrirConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, centavos);
            sentencia.setLong(2, facturaId);
            sentencia.setLong(3, centavos);
            if (sentencia.executeUpdate() != 1) {
                throw new IllegalArgumentException("La factura no existe o el pago supera el saldo pendiente.");
            }
        }
    }

    private long convertirCentavos(BigDecimal monto) {
        try {
            if (monto == null || monto.signum() <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor que cero.");
            }
            return monto.movePointRight(2).longValueExact();
        } catch (ArithmeticException error) {
            throw new IllegalArgumentException("El monto debe tener como máximo dos decimales y estar dentro del rango permitido.");
        }
    }

    private void exigirPermiso(User usuario, Permiso permiso) {
        if (usuario == null || !usuario.isActivo() || !ControlAcceso.puede(usuario.getRol(), permiso)
                || usuario.getCorreoElectronico() == null || usuario.getCorreoElectronico().isBlank()) {
            throw new SecurityException("Solo un administrador o usuario de Facturación puede acceder a este módulo.");
        }
    }
}
