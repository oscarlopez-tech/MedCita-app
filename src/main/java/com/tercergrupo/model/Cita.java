package com.tercergrupo.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Cita(long id, String paciente, String doctorCorreo, LocalDate fecha, LocalTime hora) {
    public Cita {
        if (id < 0) {
            throw new IllegalArgumentException("El identificador de la cita no puede ser negativo.");
        }
        if (paciente == null || paciente.isBlank()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio.");
        }
        paciente = paciente.trim();
        if (doctorCorreo == null || doctorCorreo.isBlank() || !doctorCorreo.contains("@")) {
            throw new IllegalArgumentException("El correo del doctor es obligatorio y debe ser válido.");
        }
        doctorCorreo = doctorCorreo.trim();
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la cita es obligatoria.");
        }
        if (hora == null) {
            throw new IllegalArgumentException("La hora de la cita es obligatoria.");
        }
    }

    public static Cita nueva(String paciente, String doctorCorreo, LocalDate fecha, LocalTime hora) {
        return new Cita(0, paciente, doctorCorreo, fecha, hora);
    }
}
