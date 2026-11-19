package com.tercergrupo.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class InicializadorBaseDatos {
    private InicializadorBaseDatos() { }

    public static void inicializar() throws SQLException {
        try (Connection conexion = ConexionBaseDatos.abrir()) {
            inicializar(conexion);
        }
    }

    // También permite preparar una base de datos independiente en las pruebas.
    public static void inicializar(Connection conexion) throws SQLException {
        String usuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_completo TEXT NOT NULL,
                telefono TEXT,
                correo TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL,
                pregunta_Seguridad TEXT,
                respuesta_Seguridad TEXT,
                rol TEXT NOT NULL DEFAULT 'SIN_ACCESO'
                    CHECK (rol IN ('ADMINISTRADOR', 'DOCTOR', 'FACTURACION', 'SIN_ACCESO')),
                activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0, 1))
            )
            """;
        String citas = """
            CREATE TABLE IF NOT EXISTS citas (
                id_cita INTEGER PRIMARY KEY AUTOINCREMENT,
                paciente TEXT NOT NULL,
                doctor_correo TEXT NOT NULL,
                fecha TEXT NOT NULL,
                hora TEXT NOT NULL,
                UNIQUE (doctor_correo, fecha, hora)
            )
            """;
        String adminPrueba = """
            INSERT INTO usuarios (nombre_completo, correo, contrasena, rol, activo)
            VALUES ('Administrador de prueba', 'admin@medcita.test', '12345678', 'ADMINISTRADOR', 1)
            ON CONFLICT (correo) DO UPDATE SET contrasena = '12345678', rol = 'ADMINISTRADOR', activo = 1
            """;
        String atenciones = """
            CREATE TABLE IF NOT EXISTS citas_atendidas (
                cita_id INTEGER PRIMARY KEY REFERENCES citas(id_cita) ON DELETE CASCADE,
                confirmado_por TEXT NOT NULL
            )
            """;
        // La factura conserva los datos de la atención aunque se elimine el doctor.
        // Los importes se guardan en centavos para evitar errores de decimales.
        String facturas = """
            CREATE TABLE IF NOT EXISTS facturas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cita_id INTEGER NOT NULL UNIQUE,
                paciente TEXT NOT NULL,
                doctor_correo TEXT NOT NULL,
                fecha_cita TEXT NOT NULL,
                fecha_emision TEXT NOT NULL,
                concepto TEXT NOT NULL,
                total_centavos INTEGER NOT NULL CHECK (total_centavos > 0),
                pagado_centavos INTEGER NOT NULL DEFAULT 0
                    CHECK (pagado_centavos >= 0 AND pagado_centavos <= total_centavos)
            )
            """;
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute(usuarios);
            sentencia.execute(citas);
            sentencia.execute(atenciones);
            sentencia.execute(facturas);
            sentencia.execute(adminPrueba);
        }
    }
}
