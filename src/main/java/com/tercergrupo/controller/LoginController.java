package com.tercergrupo.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tercergrupo.model.User;

public class LoginController {

    public User autenticarUsuario(String usuarioIngresado, String contraseñaIngresada) {
        String sql = "SELECT * FROM usuarios WHERE (usuario = ? OR correo_electronico = ?) AND contraseña = ?";
        
        try (Connection conexion = ConexionController.conectar();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            
            pstmt.setString(1, usuarioIngresado);
            pstmt.setString(2, usuarioIngresado);
            pstmt.setString(3, contraseñaIngresada);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User usuarioValido = new User();
                    usuarioValido.setNombre(rs.getString("nombre"));
                    usuarioValido.setApellido(rs.getString("apellido"));
                    usuarioValido.setTelefono(rs.getString("telefono"));
                    usuarioValido.setCorreoElectronico(rs.getString("correo_electronico"));
                    usuarioValido.setUsuario(rs.getString("usuario"));
                    usuarioValido.setContraseña(""); 
                    usuarioValido.setPreguntaSeguridad(rs.getString("pregunta_Seguridad"));
                    
                    System.out.println("¡Autenticación exitosa para el usuario: " + usuarioValido.getUsuario() + "!");
                    return usuarioValido;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error durante el proceso de autenticación: " + e.getMessage());
        }
        
        return null;
    }
}