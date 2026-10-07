package com.tercergrupo.controller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tercergrupo.model.User;

public class UserRegistration {

    public boolean registrarUsuario(User nuevoUsuario){
        String sql = "INSERT INTO usuarios (nombre, apellido, telefono, correo_electronico, usuario, contraseña, pregunta_Seguridad) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try(Connection conexion = ConexionController.conectar();
        PreparedStatement pstmt = conexion.prepareStatement(sql)){
        
        pstmt.setString(1, nuevoUsuario.getNombre());
        pstmt.setString(2, nuevoUsuario.getApellido());
        pstmt.setString(3, nuevoUsuario.getTelefono());
        pstmt.setString(4, nuevoUsuario.getCorreoElectronico());
        pstmt.setString(5, nuevoUsuario.getUsuario());
        pstmt.setString(6, nuevoUsuario.getContraseña());
        pstmt.setString(7, nuevoUsuario.getPreguntaSeguridad());

        int filasAfectadas = pstmt.executeUpdate();

        return filasAfectadas > 0;


        }catch (SQLException e){
            System.err.println("Error al registrar el usuario en la base de datos " + e.getMessage());
            return false;
        }


    }
    public boolean actualizarUsuario(User usuarioActualizado){
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ?, correo_electronico = ?, contraseña = ? WHERE usuario = ? AND pregunta_Seguridad = ?";

        try (Connection conexion = ConexionController.conectar();
            PreparedStatement pstmt = conexion.prepareStatement(sql)){
            
                pstmt.setString(1, usuarioActualizado.getNombre());
                pstmt.setString(2, usuarioActualizado.getApellido());
                pstmt.setString(3, usuarioActualizado.getTelefono());
                pstmt.setString(4, usuarioActualizado.getCorreoElectronico());
                pstmt.setString(5, usuarioActualizado.getContraseña());
                pstmt.setString(6, usuarioActualizado.getUsuario());
                pstmt.setString(7, usuarioActualizado.getPreguntaSeguridad());

                int filasAfectadas = pstmt.executeUpdate();
                return filasAfectadas > 0;



            
        } catch (SQLException e) {
            System.err.println("Error al actualizar el usuario en la base de datos: " + e.getMessage());
            return false;
        }
        
    }

    
}
