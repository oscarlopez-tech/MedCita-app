package com.tercergrupo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBaseDatos {
    private static final String URL = "jdbc:sqlite:data/medcita.db";

    private ConexionBaseDatos() { }

    public static Connection abrir() throws SQLException {
        try {
            Files.createDirectories(Path.of("data"));
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo crear la carpeta data.", error);
        }
        Connection conexion = DriverManager.getConnection(URL);
        try (var sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
        }
        return conexion;
    }
}
