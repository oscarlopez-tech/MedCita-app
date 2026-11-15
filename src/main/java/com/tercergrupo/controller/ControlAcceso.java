package com.tercergrupo.controller;

import com.tercergrupo.model.Permiso;
import com.tercergrupo.model.RolUsuario;

public final class ControlAcceso {

    private ControlAcceso(){}

    public static boolean puede(RolUsuario rol, Permiso permiso){
        if(rol == null || permiso == null){
            return false; 
        }
        return switch(rol){
            case ADMINISTRADOR -> true; 
            case DOCTOR -> permiso == Permiso.VER_CITAS || permiso == Permiso.CREAR_CITA || permiso ==  Permiso.MODIFICAR_CITA; 
            case FACTURACION -> permiso ==  Permiso.VER_FACTURAS || permiso == Permiso.CREAR_FACTURA || permiso == Permiso.VER_CITAS; 
            case SIN_ACCESO -> false; 

        };
    }
    
}
