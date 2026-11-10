package com.tercergrupo.controller;

import com.tercergrupo.model.Cita;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCitaRepository implements CitaRepository {
    @Override
    public long crear(Cita cita) throws SQLException {
        String sql = "INSERT INTO citas (paciente, doctor_correo, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection conexion = obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(sentencia, cita);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("No se pudo obtener el identificador de la cita creada.");
                }
                return claves.getLong(1);
            }
        }
    }

    @Override
    public void actualizar(Cita cita) throws SQLException {
        String sql = "UPDATE citas SET paciente = ?, doctor_correo = ?, fecha = ?, hora = ? WHERE id_cita = ?";
        try (Connection conexion = obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarDatos(sentencia, cita);
            sentencia.setLong(5, cita.id());
            if (sentencia.executeUpdate() != 1) {
                throw new SQLException("No se encontró la cita que se intenta actualizar.");
            }
        }
    }

    @Override
    public Optional<Cita> buscarPorId(long id) throws SQLException {
        String sql = "SELECT id_cita, paciente, doctor_correo, fecha, hora FROM citas WHERE id_cita = ?";
        try (Connection conexion = obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, id);
            try (ResultSet resultados = sentencia.executeQuery()) {
                return resultados.next() ? Optional.of(mapear(resultados)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Cita> listarTodas() throws SQLException {
        return listar("SELECT id_cita, paciente, doctor_correo, fecha, hora FROM citas "
                + "ORDER BY fecha, hora", null);
    }

    @Override
    public List<Cita> listarPorDoctor(String doctorCorreo) throws SQLException {
        return listar("SELECT id_cita, paciente, doctor_correo, fecha, hora FROM citas "
                + "WHERE doctor_correo = ? ORDER BY fecha, hora", doctorCorreo);
    }

    @Override
    public boolean horarioOcupado(String doctorCorreo, Cita cita, long excluirId) throws SQLException {
        String sql = "SELECT 1 FROM citas WHERE doctor_correo = ? AND fecha = ? AND hora = ? "
                + "AND id_cita <> ? LIMIT 1";
        try (Connection conexion = obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, doctorCorreo);
            sentencia.setDate(2, Date.valueOf(cita.fecha()));
            sentencia.setTime(3, Time.valueOf(cita.hora()));
            sentencia.setLong(4, excluirId);
            try (ResultSet resultados = sentencia.executeQuery()) {
                return resultados.next();
            }
        }
    }

    private List<Cita> listar(String sql, String doctorCorreo) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        try (Connection conexion = obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            if (doctorCorreo != null) {
                sentencia.setString(1, doctorCorreo);
            }
            try (ResultSet resultados = sentencia.executeQuery()) {
                while (resultados.next()) {
                    citas.add(mapear(resultados));
                }
            }
        }
        return citas;
    }

    private Cita mapear(ResultSet resultado) throws SQLException {
        return new Cita(
                resultado.getLong("id_cita"),
                resultado.getString("paciente"),
                resultado.getString("doctor_correo"),
                resultado.getDate("fecha").toLocalDate(),
                resultado.getTime("hora").toLocalTime());
    }

    private void asignarDatos(PreparedStatement sentencia, Cita cita) throws SQLException {
        sentencia.setString(1, cita.paciente());
        sentencia.setString(2, cita.doctorCorreo());
        sentencia.setDate(3, Date.valueOf(cita.fecha()));
        sentencia.setTime(4, Time.valueOf(cita.hora()));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection conexion = ConexionController.conectar();
        if (conexion == null) {
            throw new SQLException("No se pudo establecer conexión con la base de datos.");
        }
        return conexion;
    }
}
