package com.tercergrupo.controller;

import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class UsuariosControllerTest {
    private static final String ADMIN = "admin@medcita.test";
    private static final String OTRO = "doctora@medcita.test";

    private final UsuariosController controller = new UsuariosController();

    @Test
    public void noAdminNoPuedeGestionarUsuarios() {
        User doctor = usuario(RolUsuario.DOCTOR, OTRO);

        assertThrows(SecurityException.class, () -> controller.listarUsuarios(doctor));
        assertThrows(SecurityException.class, () -> controller.listarSolicitudes(doctor));
        assertThrows(SecurityException.class, () -> controller.cambiarEstado(doctor, ADMIN, false));
        assertThrows(SecurityException.class, () -> controller.asignarRol(doctor, ADMIN, RolUsuario.DOCTOR));
        assertThrows(SecurityException.class, () -> controller.eliminarUsuario(doctor, ADMIN));
    }

    @Test
    public void adminNoPuedeBloquearDegradarNiEliminarseASiMismo() {
        User admin = usuario(RolUsuario.ADMINISTRADOR, ADMIN);

        assertThrows(SecurityException.class, () -> controller.cambiarEstado(admin, ADMIN, false));
        assertThrows(SecurityException.class, () -> controller.asignarRol(admin, ADMIN, RolUsuario.DOCTOR));
        assertThrows(SecurityException.class, () -> controller.eliminarUsuario(admin, ADMIN));
    }

    @Test
    public void adminNoPuedeEvadirLaRestriccionCambiandoLasMayusculas() {
        User admin = usuario(RolUsuario.ADMINISTRADOR, ADMIN);

        assertThrows(SecurityException.class,
                () -> controller.eliminarUsuario(admin, "ADMIN@medcita.test"));
    }

    @Test
    public void rechazaCorreoVacioYRolNulo() {
        User admin = usuario(RolUsuario.ADMINISTRADOR, ADMIN);

        assertThrows(IllegalArgumentException.class, () -> controller.eliminarUsuario(admin, " "));
        assertThrows(IllegalArgumentException.class, () -> controller.asignarRol(admin, OTRO, null));
    }

    private User usuario(RolUsuario rol, String correo) {
        User usuario = new User();
        usuario.setCorreoElectronico(correo);
        usuario.setRol(rol);
        return usuario;
    }
}
