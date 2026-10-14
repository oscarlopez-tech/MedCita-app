package com.tercergrupo.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Formulario de registro 
public class Registro extends JPanel {
    private final JTextField nombreCompleto = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JTextField correo = new JTextField();
    private final JPasswordField contrasena = new JPasswordField();
    private final JComboBox<String> pregunta = new JComboBox<>(new String[]{
            "¿Cuál es el nombre de tu primera mascota?",
            "¿En qué ciudad naciste?",
            "¿Cuál era el nombre de tu escuela?"
    });

    private final JTextField respuesta = new JTextField();
    
    private final JButton registrar = EstiloMedCita.boton(
        "Crear cuenta", 
        true);

    private final JButton volver = EstiloMedCita.boton(
        "Volver al inicio", 
        false);

    public Registro() {
        JPanel tarjeta = EstiloMedCita.formulario(
            this, "Crear cuenta",
            "Completa tus datos para registrarte en MedCita.");

        EstiloMedCita.campo(
            tarjeta, 
            "Nombre completo", 
            nombreCompleto);

        EstiloMedCita.campo(
            tarjeta, 
            "Teléfono", 
            telefono);

        EstiloMedCita.campo(
            tarjeta, 
            "Correo electrónico", 
            correo);

        EstiloMedCita.campo(
            tarjeta, 
            "Contraseña (mínimo 8 caracteres)", 
            contrasena);

        EstiloMedCita.campo(
            tarjeta, 
            "Pregunta de seguridad", 
            pregunta);

        EstiloMedCita.campo(
            tarjeta, 
            "Respuesta", 
            respuesta);

        EstiloMedCita.acciones(
            tarjeta, 
            registrar, 
            volver);
    }

    // Datos y botones
    public String getNombreCompleto() {
        return nombreCompleto.getText().trim();
    }

    public String getTelefono() {
        return telefono.getText().trim();
    }

    public String getCorreo() {
        return correo.getText().trim();
    }

    public char[] getContrasena() {
        return contrasena.getPassword();
    }

    public String getPregunta() {
        return (String) pregunta.getSelectedItem();
    }

    public String getRespuesta() {
        return respuesta.getText().trim();
    }

    public JButton getBotonRegistrar() {
        return registrar;
    }

    public JButton getBotonVolver() {
        return volver;
    }

    public void limpiar() {
        nombreCompleto.setText("");
        telefono.setText("");
        correo.setText("");
        contrasena.setText("");
        respuesta.setText("");
    }
}
