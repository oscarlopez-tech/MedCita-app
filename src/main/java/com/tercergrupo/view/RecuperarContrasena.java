package com.tercergrupo.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Ventana de recuperacion de contraseña
public class RecuperarContrasena extends JPanel {
    private final JTextField correo = new JTextField();
    private final JLabel pregunta = new JLabel("Primero elija su pregunta de seguridad.");
    private final JTextField respuesta = new JTextField();
    private final JPasswordField nuevaContrasena = new JPasswordField();
    private final JPasswordField confirmarContrasena = new JPasswordField();
    private final JButton buscarPregunta = EstiloMedCita.boton("Buscar pregunta", false);
    private final JButton cambiarContrasena = EstiloMedCita.boton("Cambiar contraseña", true);
    private final JButton volver = EstiloMedCita.boton("Volver al inicio", false);

    public RecuperarContrasena() {
        JPanel tarjeta = EstiloMedCita.formulario(
            this, 
            "Recuperar contraseña",
            "Verifica tu cuenta con la pregunta de seguridad.");

        EstiloMedCita.campo(
            tarjeta, 
            "Correo electrónico", 
            correo);

        EstiloMedCita.acciones(
            tarjeta, 
            buscarPregunta);

        EstiloMedCita.aviso(
            tarjeta, 
            pregunta);

        EstiloMedCita.campo(
            tarjeta, 
            "Respuesta", 
            respuesta);

        EstiloMedCita.campo(
            tarjeta, 
            "Nueva contraseña (mínimo 8 caracteres)", 
            nuevaContrasena);

        EstiloMedCita.campo(
            tarjeta, "Confirmar contraseña", 
            confirmarContrasena);
        cambiarContrasena.setEnabled(false);

        EstiloMedCita.acciones(
            tarjeta, 
            cambiarContrasena, 
            volver);

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
        pregunta.setText("Primero busque su pregunta de seguridad.");
        cambiarContrasena.setEnabled(false);
    }
}
