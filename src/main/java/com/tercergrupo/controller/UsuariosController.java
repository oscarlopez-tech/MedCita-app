package com.tercergrupo.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tercergrupo.config.ConexionBaseDatos;
import com.tercergrupo.model.Permiso;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;

public class UsuariosController {
    public List<User> listarUsuarios(User admin) throws SQLException {
        exigirAdmin(admin);
        return listar("SELECT nombre_completo, telefono, correo, rol, activo FROM usuarios ORDER BY nombre_completo");
    }

    public List<User> listarSolicitudes(User admin) throws SQLException {
        exigirAdmin(admin);
        return listar("SELECT nombre_completo, telefono, correo, rol, activo FROM usuarios "
                + "WHERE rol = 'SIN_ACCESO' AND activo = 1 ORDER BY nombre_completo");
    }

    public void asignarRol(User admin, String correo, RolUsuario nuevoRol) throws SQLException {
        exigirAdmin(admin);
        exigirOtroUsuario(admin, correo);
        if (nuevoRol == null) {
            throw new IllegalArgumentException("Debes elegir un rol.");
        }
        String sql = "UPDATE usuarios SET rol = ? WHERE correo = ?";
        try (Connection conexion = ConexionBaseDatos.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nuevoRol.name());
            sentencia.setString(2, correo);
            exigirFilaAfectada(sentencia.executeUpdate());
        }
    }

    public void cambiarEstado(User admin, String correo, boolean activo) throws SQLException {
        exigirAdmin(admin);
        exigirOtroUsuario(admin, correo);
        String sql = "UPDATE usuarios SET activo = ? WHERE correo = ?";
        try (Connection conexion = ConexionBaseDatos.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, activo ? 1 : 0);
            sentencia.setString(2, correo);
            exigirFilaAfectada(sentencia.executeUpdate());
        }
    }

    public void eliminarUsuario(User admin, String correo) throws SQLException {
        exigirAdmin(admin);
        exigirOtroUsuario(admin, correo);
        try (Connection conexion = ConexionBaseDatos.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement borrarCitas = conexion.prepareStatement("DELETE FROM citas WHERE doctor_correo = ?");
                    PreparedStatement borrarUsuario = conexion.prepareStatement("DELETE FROM usuarios WHERE correo = ?")) {
                borrarCitas.setString(1, correo);
                borrarCitas.executeUpdate();
                borrarUsuario.setString(1, correo);
                exigirFilaAfectada(borrarUsuario.executeUpdate());
                conexion.commit();
            } catch (SQLException | RuntimeException excepcion) {
                conexion.rollback();
                throw excepcion;
            }
        }
    }

    private List<User> listar(String sql) throws SQLException {
        List<User> usuarios = new ArrayList<>();
        try (Connection conexion = ConexionBaseDatos.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultados = sentencia.executeQuery()) {
            while (resultados.next()) {
                usuarios.add(mapear(resultados));
            }
        }
        return usuarios;
    }

    private User mapear(ResultSet resultado) throws SQLException {
        User usuario = new User();
        usuario.setNombre(resultado.getString("nombre_completo"));
        usuario.setTelefono(resultado.getString("telefono"));
        usuario.setCorreoElectronico(resultado.getString("correo"));
        usuario.setRol(RolUsuario.desdeNombre(resultado.getString("rol")));
        usuario.setActivo(resultado.getInt("activo") == 1);
        return usuario;
    }

    private void exigirAdmin(User usuario) {
        if (usuario == null || !ControlAcceso.puede(usuario.getRol(), Permiso.GESTIONAR_USUARIOS)) {
            throw new SecurityException("Solo un administrador puede gestionar usuarios.");
        }
    }

    private void exigirOtroUsuario(User admin, String correo) {
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("Debes indicar el correo del usuario.");
        }
        if (correo.equalsIgnoreCase(admin.getCorreoElectronico())) {
            throw new SecurityException("No puedes modificarte a ti mismo desde Configuración.");
        }
    }

    private void exigirFilaAfectada(int filas) {
        if (filas != 1) {
            throw new IllegalArgumentException("No existe un usuario con ese correo.");
        }
    }
}
