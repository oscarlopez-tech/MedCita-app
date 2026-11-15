package com.tercergrupo.controller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tercergrupo.config.ConexionBaseDatos;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;

public class UserRegistration {

    public boolean registrarUsuario(User nuevoUsuario){
        String sql = "INSERT INTO usuarios (nombre_completo, telefono, correo, contrasena, "
                + "pregunta_Seguridad, respuesta_Seguridad, rol) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection conexion = ConexionBaseDatos.abrir();
        PreparedStatement pstmt = conexion.prepareStatement(sql)){

        pstmt.setString(1, nuevoUsuario.getNombre());
        pstmt.setString(2, nuevoUsuario.getTelefono());
        pstmt.setString(3, nuevoUsuario.getCorreoElectronico());
        pstmt.setString(4, nuevoUsuario.getContraseña());
        pstmt.setString(5, nuevoUsuario.getPreguntaSeguridad());
        pstmt.setString(6, nuevoUsuario.getRespuestaSeguridad());
        pstmt.setString(7, RolUsuario.SIN_ACCESO.name());

        int filasAfectadas = pstmt.executeUpdate();

        return filasAfectadas > 0;

        }catch (SQLException e){
            System.err.println("Error al registrar el usuario en la base de datos " + e.getMessage());
            return false;
        }
    }
}
