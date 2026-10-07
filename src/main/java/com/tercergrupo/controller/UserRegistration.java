package com.tercergrupo.controller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tercergrupo.model.User;

public class UserRegistration {

    public boolean registrarUsuario(User nuevoUsuario){
        String sql = "INSERT INTO usuarios (nombre_completo, telefono, correo, contrasena, "
                + "pregunta_Seguridad, respuesta_Seguridad, id_rol) "
                + "SELECT ?, ?, ?, ?, ?, ?, id_rol FROM roles WHERE nombre_rol = ?";
        
        try(Connection conexion = ConexionController.conectar();
        PreparedStatement pstmt = conexion.prepareStatement(sql)){
        
        pstmt.setString(1, nuevoUsuario.getNombre());
        pstmt.setString(2, nuevoUsuario.getTelefono());
        pstmt.setString(3, nuevoUsuario.getCorreoElectronico());
        pstmt.setString(4, nuevoUsuario.getContraseña());
        pstmt.setString(5, nuevoUsuario.getPreguntaSeguridad());
        pstmt.setString(6, nuevoUsuario.getRespuestaSeguridad());
        pstmt.setString(7, "Recepcionista");

        int filasAfectadas = pstmt.executeUpdate();

        return filasAfectadas > 0;


        }catch (SQLException e){
            System.err.println("Error al registrar el usuario en la base de datos " + e.getMessage());
            return false;
        }


    }
    public boolean actualizarUsuario(User usuarioActualizado){
        String sql = "UPDATE usuarios SET nombre_completo = ?, telefono = ?, correo = ?, "
                + "contrasena = ? WHERE correo = ? AND pregunta_Seguridad = ?";

        try (Connection conexion = ConexionController.conectar();
            PreparedStatement pstmt = conexion.prepareStatement(sql)){
            
                String nombreCompleto = usuarioActualizado.getNombre();
                if (usuarioActualizado.getApellido() != null && !usuarioActualizado.getApellido().isBlank()) {
                    nombreCompleto += " " + usuarioActualizado.getApellido();
                }
                pstmt.setString(1, nombreCompleto);
                pstmt.setString(2, usuarioActualizado.getTelefono());
                pstmt.setString(3, usuarioActualizado.getCorreoElectronico());
                pstmt.setString(4, usuarioActualizado.getContraseña());
                pstmt.setString(5, usuarioActualizado.getCorreoElectronico());
                pstmt.setString(6, usuarioActualizado.getPreguntaSeguridad());

                int filasAfectadas = pstmt.executeUpdate();
                return filasAfectadas > 0;



            
        } catch (SQLException e) {
            System.err.println("Error al actualizar el usuario en la base de datos: " + e.getMessage());
            return false;
        }
        
    }

    
}
