package com.tercergrupo.controller;

import com.tercergrupo.model.Cita;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CitasController {
    private final CitaRepository repositorio;

    public CitasController() {
        this(new JdbcCitaRepository());
    }

    public CitasController(CitaRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio de citas es obligatorio.");
    }

    public long crearCita(User usuario, Cita cita) throws SQLException {
        validarUsuario(usuario);
        validarCita(cita);
        exigirPermisoDeEdicion(usuario);
        validarDoctorAsignado(usuario, cita);
        validarHorarioDisponible(cita, 0);
        return repositorio.crear(cita);
    }

    public void actualizarCita(User usuario, Cita cita) throws SQLException {
        validarUsuario(usuario);
        validarCita(cita);
        exigirPermisoDeEdicion(usuario);

        Cita existente = repositorio.buscarPorId(cita.id())
                .orElseThrow(() -> new IllegalArgumentException("La cita que se intenta modificar no existe."));
        if (usuario.getRol() == RolUsuario.DOCTOR
                && !existente.doctorCorreo().equalsIgnoreCase(usuario.getCorreoElectronico())) {
            throw new SecurityException("Un doctor solo puede modificar sus propias citas.");
        }
        validarDoctorAsignado(usuario, cita);
        validarHorarioDisponible(cita, cita.id());
        repositorio.actualizar(cita);
    }

    public List<Cita> listarCitas(User usuario) throws SQLException {
        validarUsuario(usuario);
        return switch (usuario.getRol()) {
            case ADMINISTRADOR, FACTURACION -> repositorio.listarTodas();
            case DOCTOR -> repositorio.listarPorDoctor(usuario.getCorreoElectronico());
            case SIN_ACCESO -> throw new SecurityException("El usuario no tiene permiso para ver citas.");
        };
    }

    private void exigirPermisoDeEdicion(User usuario) {
        if (usuario.getRol() != RolUsuario.ADMINISTRADOR && usuario.getRol() != RolUsuario.DOCTOR) {
            throw new SecurityException("Solo un doctor o administrador puede crear o modificar citas.");
        }
    }

    private void validarDoctorAsignado(User usuario, Cita cita) {
        if (usuario.getRol() == RolUsuario.DOCTOR
                && !cita.doctorCorreo().equalsIgnoreCase(usuario.getCorreoElectronico())) {
            throw new SecurityException("Un doctor solo puede agendar citas a su propio nombre.");
        }
    }

    private void validarHorarioDisponible(Cita cita, long excluirId) throws SQLException {
        if (repositorio.horarioOcupado(cita.doctorCorreo(), cita, excluirId)) {
            throw new IllegalArgumentException("El doctor ya tiene una cita en esa fecha y hora.");
        }
    }

    private void validarUsuario(User usuario) {
        if (usuario == null || usuario.getRol() == null || usuario.getCorreoElectronico() == null
                || usuario.getCorreoElectronico().isBlank()) {
            throw new SecurityException("Se requiere un usuario autenticado para acceder a las citas.");
        }
    }

    private void validarCita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("Los datos de la cita son obligatorios.");
        }
    }
}
