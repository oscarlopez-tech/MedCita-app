package com.tercergrupo.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

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
    private final JButton registrar = new JButton("Registrar");
    private final JButton volver = new JButton("Volver");

    public Registro() {
        super(new GridLayout(0, 2, 8, 8));
        add(new JLabel("MedCita - Crear cuenta"));
        add(new JLabel(""));
        add(new JLabel("Nombre completo:"));
        add(nombreCompleto);
        add(new JLabel("Teléfono:"));
        add(telefono);
        add(new JLabel("Correo electrónico:"));
        add(correo);
        add(new JLabel("Contraseña:"));
        add(contrasena);
        add(new JLabel("Pregunta de seguridad:"));
        add(pregunta);
        add(new JLabel("Respuesta:"));
        add(respuesta);
        add(registrar);
        add(volver);
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
