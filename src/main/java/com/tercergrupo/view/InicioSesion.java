package com.tercergrupo.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

// Ventana de inicio de sesion
public class InicioSesion extends JPanel {
    private final JTextField correo = new JTextField();
    private final JPasswordField contrasena = new JPasswordField();
    private final JButton iniciarSesion = new JButton("Iniciar sesión");
    private final JButton registrar = new JButton("Crear cuenta");
    private final JButton recuperar = new JButton("Olvidé mi contraseña");

    public InicioSesion() {
        super(new GridLayout(0, 2, 8, 8));
        add(new JLabel("MedCita - Iniciar sesión"));
        add(new JLabel(""));
        add(new JLabel("Correo electrónico:"));
        add(correo);
        add(new JLabel("Contraseña:"));
        add(contrasena);
        add(iniciarSesion);
        add(registrar);
        add(recuperar);
        add(new JLabel(""));
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
