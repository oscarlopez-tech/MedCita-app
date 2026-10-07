package com.tercergrupo.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

// Ventana de recuperacion de contraseña
public class RecuperarContrasena extends JPanel {
    private final JTextField correo = new JTextField();
    private final JLabel pregunta = new JLabel("Primero busca tu pregunta de seguridad.");
    private final JTextField respuesta = new JTextField();
    private final JPasswordField nuevaContrasena = new JPasswordField();
    private final JPasswordField confirmarContrasena = new JPasswordField();
    private final JButton buscarPregunta = new JButton("Buscar pregunta");
    private final JButton cambiarContrasena = new JButton("Cambiar contraseña");
    private final JButton volver = new JButton("Volver");

    public RecuperarContrasena() {
        super(new GridLayout(0, 2, 8, 8));
        add(new JLabel("MedCita - Recuperar contraseña"));
        add(new JLabel(""));
        add(new JLabel("Correo electrónico:"));
        add(correo);
        add(buscarPregunta);
        add(pregunta);
        add(new JLabel("Respuesta:"));
        add(respuesta);
        add(new JLabel("Nueva contraseña:"));
        add(nuevaContrasena);
        add(new JLabel("Confirmar contraseña:"));
        add(confirmarContrasena);
        cambiarContrasena.setEnabled(false);
        add(cambiarContrasena);
        add(volver);
    }

    // Datos y botones
    public String getCorreo() {
        return correo.getText().trim();
    }

    public String getRespuesta() {
        return respuesta.getText().trim();
    }

    public char[] getNuevaContrasena() {
        return nuevaContrasena.getPassword();
    }

    public char[] getConfirmarContrasena() {
        return confirmarContrasena.getPassword();
    }

    public JButton getBotonBuscarPregunta() {
        return buscarPregunta;
    }

    public JButton getBotonCambiarContrasena() {
        return cambiarContrasena;
    }

    public JButton getBotonVolver() {
        return volver;
    }

    public void setPregunta(String texto) {
        pregunta.setText(texto);
    }

    public void habilitarCambio(boolean habilitado) {
        cambiarContrasena.setEnabled(habilitado);
    }

    public void limpiar() {
        correo.setText("");
        respuesta.setText("");
        nuevaContrasena.setText("");
        confirmarContrasena.setText("");
        pregunta.setText("Primero busca tu pregunta de seguridad.");
        cambiarContrasena.setEnabled(false);
    }
}
