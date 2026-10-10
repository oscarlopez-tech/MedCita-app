package com.tercergrupo.view;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Ventana de inicio de sesion
public class InicioSesion extends JPanel {
    private final JTextField correo = new JTextField();

    private final JPasswordField contrasena = new JPasswordField();

    private final JButton iniciarSesion = EstiloMedCita.boton(
        "Iniciar sesión", 
        true);
    private final JButton registrar = EstiloMedCita.boton(
        "Registrarse", 
        false);
    private final JButton recuperar = EstiloMedCita.boton(
        "Olvidé mi contraseña", 
        false);

    public InicioSesion() {
        JPanel tarjeta = EstiloMedCita.formulario(
            this, 
            "Iniciar sesión",
            "Bienvenido. Ingresa los datos de tu cuenta.");
        EstiloMedCita.campo(
            tarjeta, 
            "Correo electrónico", 
            correo);
        EstiloMedCita.campo(
            tarjeta, 
            "Contraseña", 
            contrasena);
        EstiloMedCita.acciones(
            tarjeta, 
            iniciarSesion, 
            registrar, 
            recuperar);
    }

    // Datos y botones
    public String getCorreo() {
        return correo.getText().trim();
    }

    public char[] getContrasena() {
        return contrasena.getPassword();
    }

    public JButton getIniciarSesion() {
        return iniciarSesion;
    }

    public JButton getRegistrar() {
        return registrar;
    }

    public JButton getRecuperar() {
        return recuperar;
    }

    public void limpiar() {
        correo.setText("");
        contrasena.setText("");
    }
}
