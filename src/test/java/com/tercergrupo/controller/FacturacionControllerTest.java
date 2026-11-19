package com.tercergrupo.controller;

import com.tercergrupo.config.InicializadorBaseDatos;
import com.tercergrupo.model.EstadoCuenta;
import com.tercergrupo.model.Factura;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class FacturacionControllerTest {
    private Path archivo;
    private String url;
    private FacturacionController controller;
    private User facturador;

    @Before
    public void preparar() throws Exception {
        archivo = Files.createTempFile("medcita-facturacion-", ".db");
        url = "jdbc:sqlite:" + archivo;
        try (Connection conexion = DriverManager.getConnection(url)) {
            InicializadorBaseDatos.inicializar(conexion);
        }
        controller = new FacturacionController(url);
        facturador = usuario(RolUsuario.FACTURACION);
        ejecutar("INSERT INTO citas (id_cita, paciente, doctor_correo, fecha, hora) "
                + "VALUES (1, 'Paciente de prueba', 'doctor@medcita.test', '2020-01-01', '09:00')");
    }

    @After
    public void limpiar() throws Exception {
        Files.deleteIfExists(archivo);
    }

    @Test
    public void citaAtendidaGeneraFacturaPendienteConDatosDeLaAtencion() throws Exception {
        controller.marcarAtendida(facturador, 1);
        controller.generarFactura(facturador, 1, "Consulta general", new BigDecimal("25000.50"));
        Factura factura = factura();
        assertEquals("Paciente de prueba", factura.paciente());
        assertEquals("doctor@medcita.test", factura.doctorCorreo());
        assertEquals("Consulta general", factura.concepto());
        assertEquals(new BigDecimal("25000.50"), factura.saldo());
        assertEquals(EstadoCuenta.PENDIENTE, factura.estado());
        assertTrue(controller.listarCitasSinFactura(facturador).isEmpty());
    }

    @Test
    public void noFacturaCitaSinAtenderNiCitaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.generarFactura(facturador, 1, "Consulta", BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class,
                () -> controller.generarFactura(facturador, 999, "Consulta", BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> controller.marcarAtendida(facturador, 999));
    }

    @Test
    public void noConfirmaAtencionFutura() throws Exception {
        ejecutar("UPDATE citas SET fecha = '2999-01-01' WHERE id_cita = 1");
        assertThrows(IllegalArgumentException.class, () -> controller.marcarAtendida(facturador, 1));
        assertFalse(controller.estaAtendida(facturador, 1));
    }

    @Test
    public void noDuplicaFacturaAunqueSeConfirmeAtencionDosVeces() throws Exception {
        controller.marcarAtendida(facturador, 1);
        controller.marcarAtendida(facturador, 1);
        controller.generarFactura(facturador, 1, "Consulta", BigDecimal.TEN);
        assertThrows(IllegalArgumentException.class,
                () -> controller.generarFactura(facturador, 1, "Otra factura", BigDecimal.TEN));
        assertEquals(1, controller.listarFacturas(facturador).size());
    }

    @Test
    public void abonoMantienePendienteYLiquidacionCambiaAPagado() throws Exception {
        crearFactura();
        long id = factura().id();
        controller.registrarPago(facturador, id, new BigDecimal("4.25"));
        assertEquals(new BigDecimal("5.75"), factura().saldo());
        assertEquals(EstadoCuenta.PENDIENTE, factura().estado());
        controller.registrarPago(facturador, id, new BigDecimal("5.75"));
        assertEquals(new BigDecimal("0.00"), factura().saldo());
        assertEquals(new BigDecimal("10.00"), factura().pagado());
        assertEquals(EstadoCuenta.PAGADO, factura().estado());
        assertThrows(IllegalArgumentException.class, () -> controller.registrarPago(facturador, id, BigDecimal.ONE));
    }

    @Test
    public void rechazaPagosInvalidosSinAlterarSaldo() throws Exception {
        crearFactura();
        long id = factura().id();
        for (BigDecimal monto : new BigDecimal[]{BigDecimal.ZERO, new BigDecimal("-1"),
                new BigDecimal("10.01"), new BigDecimal("0.001"), new BigDecimal("1E30")}) {
            assertThrows(IllegalArgumentException.class, () -> controller.registrarPago(facturador, id, monto));
        }
        assertThrows(IllegalArgumentException.class, () -> controller.registrarPago(facturador, id, null));
        assertThrows(IllegalArgumentException.class, () -> controller.registrarPago(facturador, 999, BigDecimal.ONE));
        assertEquals(new BigDecimal("10.00"), factura().saldo());
    }

    @Test
    public void rechazaConceptoVacioYTotalInvalido() throws Exception {
        controller.marcarAtendida(facturador, 1);
        assertThrows(IllegalArgumentException.class,
                () -> controller.generarFactura(facturador, 1, " ", BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class,
                () -> controller.generarFactura(facturador, 1, "Consulta", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> controller.generarFactura(facturador, 1, "Consulta", new BigDecimal("0.001")));
        assertTrue(controller.listarFacturas(facturador).isEmpty());
    }

    @Test
    public void doctorYSinAccesoNoPuedenRealizarNingunaOperacion() {
        for (User usuario : new User[]{usuario(RolUsuario.DOCTOR), usuario(RolUsuario.SIN_ACCESO), null}) {
            assertThrows(SecurityException.class, () -> controller.listarCitasSinFactura(usuario));
            assertThrows(SecurityException.class, () -> controller.estaAtendida(usuario, 1));
            assertThrows(SecurityException.class, () -> controller.marcarAtendida(usuario, 1));
            assertThrows(SecurityException.class, () -> controller.generarFactura(usuario, 1, "Consulta", BigDecimal.TEN));
            assertThrows(SecurityException.class, () -> controller.listarFacturas(usuario));
            assertThrows(SecurityException.class, () -> controller.registrarPago(usuario, 1, BigDecimal.ONE));
        }
    }

    @Test
    public void administradorPuedeFacturarYUsuarioBloqueadoNoPuedeEntrar() throws Exception {
        User admin = usuario(RolUsuario.ADMINISTRADOR);
        controller.marcarAtendida(admin, 1);
        controller.generarFactura(admin, 1, "Consulta", BigDecimal.TEN);
        controller.registrarPago(admin, factura().id(), BigDecimal.TEN);
        assertEquals(EstadoCuenta.PAGADO, controller.listarFacturas(admin).get(0).estado());
        facturador.setActivo(false);
        assertThrows(SecurityException.class, () -> controller.listarFacturas(facturador));
    }

    @Test
    public void facturaYPagosPersistenYNoSePierdenAlEliminarDoctor() throws Exception {
        crearFactura();
        controller.registrarPago(facturador, factura().id(), new BigDecimal("3.10"));
        ejecutar("INSERT INTO usuarios (nombre_completo, correo, contrasena, rol) "
                + "VALUES ('Doctor', 'doctor@medcita.test', '12345678', 'DOCTOR')");
        try (Connection conexion = DriverManager.getConnection(url)) {
            // Reproduce la eliminación de citas y cuenta de UsuariosController.
            try (var sentencia = conexion.createStatement()) {
                sentencia.execute("PRAGMA foreign_keys = ON");
                conexion.setAutoCommit(false);
                sentencia.executeUpdate("DELETE FROM citas WHERE doctor_correo = 'doctor@medcita.test'");
                sentencia.executeUpdate("DELETE FROM usuarios WHERE correo = 'doctor@medcita.test'");
                conexion.commit();
            }
            InicializadorBaseDatos.inicializar(conexion);
        }
        FacturacionController nuevaInstancia = new FacturacionController(url);
        Factura conservada = nuevaInstancia.listarFacturas(facturador).get(0);
        assertEquals("Paciente de prueba", conservada.paciente());
        assertEquals(new BigDecimal("6.90"), conservada.saldo());
    }

    private User usuario(RolUsuario rol) {
        User usuario = new User();
        usuario.setCorreoElectronico("operador@medcita.test");
        usuario.setRol(rol);
        return usuario;
    }

    private void crearFactura() throws SQLException {
        controller.marcarAtendida(facturador, 1);
        controller.generarFactura(facturador, 1, "Consulta", BigDecimal.TEN);
    }

    private Factura factura() throws SQLException {
        return controller.listarFacturas(facturador).get(0);
    }

    private void ejecutar(String sql) throws SQLException {
        try (Connection conexion = DriverManager.getConnection(url);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.executeUpdate();
        }
    }
}
