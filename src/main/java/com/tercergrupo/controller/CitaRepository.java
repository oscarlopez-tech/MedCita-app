package com.tercergrupo.controller;

import com.tercergrupo.model.Cita;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CitaRepository {
    long crear(Cita cita) throws SQLException;

    void actualizar(Cita cita) throws SQLException;

    Optional<Cita> buscarPorId(long id) throws SQLException;

    List<Cita> listarTodas() throws SQLException;

    List<Cita> listarPorDoctor(String doctorCorreo) throws SQLException;

    boolean horarioOcupado(String doctorCorreo, Cita cita, long excluirId) throws SQLException;
}
