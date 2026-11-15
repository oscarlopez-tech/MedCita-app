package com.tercergrupo.view;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tercergrupo.controller.CitasController;
import com.tercergrupo.controller.UsuariosController;
import com.tercergrupo.model.Cita;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;

public class FormularioCita extends JDialog {
    private final User usuario;
    private final Cita existente;
    private final CitasController citasController = new CitasController();
    private final UsuariosController usuariosController = new UsuariosController();
    private final JTextField paciente = new JTextField();
    private final JComboBox<String> doctor = new JComboBox<>();
    private final JTextField fecha = new JTextField();
    private final JTextField hora = new JTextField();
    private final JButton guardar = EstiloMedCita.boton("Guardar", true);
    private final JButton cancelar = EstiloMedCita.boton("Cancelar", false);

    public FormularioCita(Window propietario, User usuario) {
        this(propietario, usuario, null);
    }

    public FormularioCita(Window propietario, User usuario, Cita existente) {
        super(propietario, existente == null ? "Nueva cita" : "Editar cita", ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.existente = existente;

        JPanel panel = new JPanel();
        JPanel tarjeta = EstiloMedCita.formulario(panel,
                existente == null ? "Nueva cita" : "Editar cita",
                "Completa los datos de la cita.");
        EstiloMedCita.campo(tarjeta, "Paciente", paciente);
        EstiloMedCita.campo(tarjeta, "Doctor", doctor);
        EstiloMedCita.campo(tarjeta, "Fecha (yyyy-MM-dd)", fecha);
        EstiloMedCita.campo(tarjeta, "Hora (HH:mm)", hora);
        EstiloMedCita.acciones(tarjeta, guardar, cancelar);
        add(panel);

        if (existente != null) {
            paciente.setText(existente.paciente());
            fecha.setText(existente.fecha().toString());
            hora.setText(existente.hora().toString());
        }
        cargarDoctores();
        guardar.addActionListener(event -> guardarCita());
        cancelar.addActionListener(event -> dispose());
        pack();
        setLocationRelativeTo(propietario);
    }

    private void cargarDoctores() {
        if (usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            doctor.addItem(usuario.getCorreoElectronico());
            doctor.setEnabled(false);
            return;
        }
        try {
            List<String> correos = new ArrayList<>();
            for (User candidato : usuariosController.listarUsuarios(usuario)) {
                if (candidato.getRol() == RolUsuario.DOCTOR && candidato.isActivo()) {
                    correos.add(candidato.getCorreoElectronico());
                }
            }
            if (existente != null && !correos.contains(existente.doctorCorreo())) {
                correos.add(existente.doctorCorreo());
            }
            correos.forEach(doctor::addItem);
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
        if (existente != null) {
            doctor.setSelectedItem(existente.doctorCorreo());
        }
    }

    private void guardarCita() {
        try {
            String pacienteIngresado = paciente.getText();
            String doctorElegido = (String) doctor.getSelectedItem();
            LocalDate fechaElegida = LocalDate.parse(fecha.getText().trim());
            LocalTime horaElegida = LocalTime.parse(hora.getText().trim());
            if (existente == null) {
                citasController.crearCita(usuario,
                        Cita.nueva(pacienteIngresado, doctorElegido, fechaElegida, horaElegida));
            } else {
                citasController.actualizarCita(usuario,
                        new Cita(existente.id(), pacienteIngresado, doctorElegido, fechaElegida, horaElegida));
            }
            dispose();
        } catch (DateTimeParseException error) {
            mostrarError(new IllegalArgumentException(
                    "La fecha debe tener formato yyyy-MM-dd y la hora formato HH:mm."));
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void mostrarError(Exception error) {
        JOptionPane.showMessageDialog(this, error.getMessage(), "MedCita", JOptionPane.ERROR_MESSAGE);
    }
}
