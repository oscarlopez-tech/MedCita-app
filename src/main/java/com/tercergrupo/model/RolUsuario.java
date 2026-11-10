package com.tercergrupo.model;

import java.text.Normalizer;
import java.util.Locale;

public enum RolUsuario {
    ADMINISTRADOR,
    DOCTOR,
    FACTURACION,
    SIN_ACCESO;

    public static RolUsuario desdeNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return SIN_ACCESO;
        }

        String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT);

        return switch (normalizado) {
            case "admin", "administrador" -> ADMINISTRADOR;
            case "doctor" -> DOCTOR;
            case "facturacion", "facturacion/cxc", "facturacion y cxc", "cxc" -> FACTURACION;
            default -> SIN_ACCESO;
        };
    }
}
