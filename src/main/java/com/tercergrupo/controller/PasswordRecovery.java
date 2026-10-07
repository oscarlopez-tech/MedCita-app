package com.tercergrupo.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordRecovery {

    public String obtenerPreguntaSeguridad(String correoElectronico) {
        String sql = "SELECT pregunta_Seguridad FROM usuarios WHERE correo = ?";

        try (Connection conexion = ConexionController.conectar();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, correoElectronico);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("pregunta_Seguridad");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar la pregunta de seguridad " + e.getMessage());  
        }
        
        return null; 
    }

    public boolean validarYCambiarContraseña(String correoElectronico, String respuestaIngresada, String nuevaContraseña) {
        String sqlSelect = "SELECT respuesta_Seguridad FROM usuarios WHERE correo = ? ";
        String sqlUpdate = "UPDATE usuarios SET contrasena = ? WHERE correo = ?";

        try (Connection conexion = ConexionController.conectar();
             PreparedStatement pstmtSelect = conexion.prepareStatement(sqlSelect)) {
            
            pstmtSelect.setString(1, correoElectronico);

            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    String respuestaGuardada = rs.getString("respuesta_Seguridad");

                    if (respuestaGuardada != null && respuestaGuardada.equalsIgnoreCase(respuestaIngresada.trim())) {
                        
                        try (PreparedStatement pstmtUpdate = conexion.prepareStatement(sqlUpdate)) {
                            pstmtUpdate.setString(1, nuevaContraseña);
                            pstmtUpdate.setString(2, correoElectronico);

                            int filasAfectadas = pstmtUpdate.executeUpdate();
                            return filasAfectadas > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar la contraseña: " + e.getMessage());
        }

        return false;
    }
}