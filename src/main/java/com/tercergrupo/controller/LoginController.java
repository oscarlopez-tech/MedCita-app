package com.tercergrupo.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tercergrupo.model.User;
import com.tercergrupo.model.RolUsuario;

public class LoginController {

    public User autenticarUsuario(String usuarioIngresado, String contraseñaIngresada) {
        String sql = "SELECT u.nombre_completo, u.telefono, u.correo, u.pregunta_Seguridad, "
                + "r.nombre_rol FROM usuarios u JOIN roles r ON u.id_rol = r.id_rol "
                + "WHERE u.correo = ? AND u.contrasena = ?";
        
        try (Connection conexion = ConexionController.conectar();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            
            pstmt.setString(1, usuarioIngresado);
            pstmt.setString(2, contraseñaIngresada);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User usuarioValido = new User();
                    usuarioValido.setNombre(rs.getString("nombre_completo"));
                    usuarioValido.setTelefono(rs.getString("telefono"));
                    usuarioValido.setCorreoElectronico(rs.getString("correo"));
                    usuarioValido.setUsuario(rs.getString("correo"));
                    usuarioValido.setRol(RolUsuario.desdeNombre(rs.getString("nombre_rol")));
                    String preguntaSeguridad = rs.getString("pregunta_Seguridad");
                    if (preguntaSeguridad != null) {
                        usuarioValido.setPreguntaSeguridad(preguntaSeguridad);
                    }
                    
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