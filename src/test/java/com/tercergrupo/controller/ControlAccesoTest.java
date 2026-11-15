package com.tercergrupo.controller;

import com.tercergrupo.model.Permiso;
import com.tercergrupo.model.RolUsuario;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControlAccesoTest {

    @Test
    public void administradorTienePermisosDeTodosLosModulos() {
        for (Permiso permiso : Permiso.values()) {
            assertTrue(ControlAcceso.puede(RolUsuario.ADMINISTRADOR, permiso));
        }
    }

    @Test
    public void doctorPuedeAgendarYVerCitasPeroNoFacturasNiUsuarios() {
        assertTrue(ControlAcceso.puede(RolUsuario.DOCTOR, Permiso.VER_CITAS));
        assertTrue(ControlAcceso.puede(RolUsuario.DOCTOR, Permiso.CREAR_CITA));
        assertTrue(ControlAcceso.puede(RolUsuario.DOCTOR, Permiso.MODIFICAR_CITA));
        assertFalse(ControlAcceso.puede(RolUsuario.DOCTOR, Permiso.VER_FACTURAS));
        assertFalse(ControlAcceso.puede(RolUsuario.DOCTOR, Permiso.CREAR_FACTURA));
        assertFalse(ControlAcceso.puede(RolUsuario.DOCTOR, Permiso.GESTIONAR_USUARIOS));
    }

    @Test
    public void facturacionPuedeVerCitasYFacturasPeroNoAgendarNiGestionarUsuarios() {
        assertTrue(ControlAcceso.puede(RolUsuario.FACTURACION, Permiso.VER_CITAS));
        assertTrue(ControlAcceso.puede(RolUsuario.FACTURACION, Permiso.VER_FACTURAS));
        assertTrue(ControlAcceso.puede(RolUsuario.FACTURACION, Permiso.CREAR_FACTURA));
        assertFalse(ControlAcceso.puede(RolUsuario.FACTURACION, Permiso.CREAR_CITA));
        assertFalse(ControlAcceso.puede(RolUsuario.FACTURACION, Permiso.MODIFICAR_CITA));
        assertFalse(ControlAcceso.puede(RolUsuario.FACTURACION, Permiso.GESTIONAR_USUARIOS));
    }

    @Test
    public void sinAccesoNoTieneNingunPermiso() {
        for (Permiso permiso : Permiso.values()) {
            assertFalse(ControlAcceso.puede(RolUsuario.SIN_ACCESO, permiso));
        }
    }

    @Test
    public void rolOPermisoNuloNuncaDaAcceso() {
        assertFalse(ControlAcceso.puede(null, Permiso.VER_CITAS));
        assertFalse(ControlAcceso.puede(RolUsuario.ADMINISTRADOR, null));
    }
}
