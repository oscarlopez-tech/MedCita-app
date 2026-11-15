package com.tercergrupo.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class InicializadorBaseDatos {
    private InicializadorBaseDatos() { }

    public static void inicializar() throws SQLException {
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
        try (Connection conexion = ConexionBaseDatos.abrir();
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute(usuarios);
            sentencia.execute(citas);
            sentencia.execute(adminPrueba);
        }
    }
}
