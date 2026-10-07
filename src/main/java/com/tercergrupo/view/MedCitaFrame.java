package com.tercergrupo.view;

import com.tercergrupo.controller.LoginController;
import com.tercergrupo.controller.PasswordRecovery;
import com.tercergrupo.controller.UserRegistration;
import com.tercergrupo.model.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

// Ventana principal
public class MedCitaFrame extends JFrame {
    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String RECOVERY = "recovery";
    private static final String HOME = "home";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel screens = new JPanel(cardLayout);
    private final InicioSesion loginView = new InicioSesion();
    private final Registro registerView = new Registro();
    private final RecuperarContrasena recoveryView = new RecuperarContrasena();

    private final LoginController loginController = new LoginController();
    private final UserRegistration userRegistration = new UserRegistration();
    private final PasswordRecovery passwordRecovery = new PasswordRecovery();

    // Configura la ventana y conecta los botones de navegacion
    public MedCitaFrame() {
        super("MedCita");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 420);
        setLocationRelativeTo(null);

        screens.add(loginView, LOGIN);
        screens.add(registerView, REGISTER);
        screens.add(recoveryView, RECOVERY);
        screens.add(buildHomeView(), HOME);
        add(screens);

        loginView.getIniciarSesion().addActionListener(event ->
                iniciarSesion(loginView.getIniciarSesion()));
        loginView.getRegistrar().addActionListener(event -> mostrarPantalla(REGISTER));
        loginView.getRecuperar().addActionListener(event -> {
            recoveryView.limpiar();
            mostrarPantalla(RECOVERY);
        });
        registerView.getBotonRegistrar().addActionListener(event ->
                registrarUsuario(registerView.getBotonRegistrar()));
        registerView.getBotonVolver().addActionListener(event -> mostrarPantalla(LOGIN));
        recoveryView.getBotonBuscarPregunta().addActionListener(event ->
                buscarPregunta(recoveryView.getBotonBuscarPregunta()));
        recoveryView.getBotonCambiarContrasena().addActionListener(event ->
                cambiarContrasena(recoveryView.getBotonCambiarContrasena()));
        recoveryView.getBotonVolver().addActionListener(event -> mostrarPantalla(LOGIN));
    }

    // Ventana que se muestra despues de iniciar sesion
    private JPanel buildHomeView() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("MedCita"));
        panel.add(new JLabel(""));
        panel.add(new JLabel("Sesión iniciada correctamente."));
        panel.add(new JLabel(""));
        JButton cerrarSesion = new JButton("Cerrar sesión");
        cerrarSesion.addActionListener(event -> {
            loginView.limpiar();
            mostrarPantalla(LOGIN);
        });
        panel.add(cerrarSesion);
        panel.add(new JLabel(""));
        return panel;
    }

    // Inicio de sesión
    private void iniciarSesion(JButton boton) {
        String correo = loginView.getCorreo();
        String contrasena = new String(loginView.getContrasena());
        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Ingresa tu correo y tu contraseña.");
            return;
        }

        ejecutarEnSegundoPlano(boton,
                () -> loginController.autenticarUsuario(correo, contrasena),
                usuario -> {
                    if (usuario == null) {
                        mostrarError("No fue posible iniciar sesión. Verifica tus datos.");
                    } else {
                        mostrarPantalla(HOME);
                    }
                });
    }

    // Registro
    private void registrarUsuario(JButton boton) {
        String nombre = registerView.getNombreCompleto();
        String correo = registerView.getCorreo();
        String contrasena = new String(registerView.getContrasena());
        if (nombre.isEmpty() || correo.isEmpty() || registerView.getTelefono().isEmpty()
                || registerView.getRespuesta().isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }
        if (contrasena.length() < 8) {
            mostrarError("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        User usuario = new User();
        try {
            usuario.setNombre(nombre);
            usuario.setTelefono(registerView.getTelefono());
            usuario.setCorreoElectronico(correo);
            usuario.setContraseña(contrasena);
            usuario.setPreguntaSeguridad(registerView.getPregunta());
            usuario.setRespuestaSeguridad(registerView.getRespuesta());
        } catch (IllegalArgumentException exception) {
            mostrarError(exception.getMessage());
            return;
        }

        ejecutarEnSegundoPlano(boton,
                () -> userRegistration.registrarUsuario(usuario),
                registrado -> {
                    if (registrado) {
                        registerView.limpiar();
                        mostrarPantalla(LOGIN);
                        JOptionPane.showMessageDialog(this, "La cuenta se creó correctamente.");
                    } else {
                        mostrarError("No se pudo crear la cuenta. Verifica los datos e intenta de nuevo.");
                    }
                });
    }

    // Recuperación
    private void buscarPregunta(JButton boton) {
        String correo = recoveryView.getCorreo();
        if (correo.isEmpty()) {
            mostrarError("Ingresa el correo electrónico de tu cuenta.");
            return;
        }
        recoveryView.habilitarCambio(false);
        recoveryView.setPregunta("Buscando...");
        ejecutarEnSegundoPlano(boton,
                () -> passwordRecovery.obtenerPreguntaSeguridad(correo),
                pregunta -> {
                    if (pregunta == null || pregunta.isBlank()) {
                        recoveryView.setPregunta("No se encontró la pregunta.");
                        mostrarError("No se encontró una cuenta con ese correo.");
                    } else {
                        recoveryView.setPregunta(pregunta);
                        recoveryView.habilitarCambio(true);
                    }
                });
    }

    // Valida la respuesta y confirma ambas contraseñas
    private void cambiarContrasena(JButton boton) {
        String correo = recoveryView.getCorreo();
        String respuesta = recoveryView.getRespuesta();
        String nueva = new String(recoveryView.getNuevaContrasena());
        String confirmacion = new String(recoveryView.getConfirmarContrasena());
        if (respuesta.isEmpty() || nueva.isEmpty() || confirmacion.isEmpty()) {
            mostrarError("Completa la respuesta y los campos de contraseña.");
            return;
        }
        if (nueva.length() < 8) {
            mostrarError("La nueva contraseña debe tener al menos 8 caracteres.");
            return;
        }
        if (!nueva.equals(confirmacion)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        ejecutarEnSegundoPlano(boton,
                () -> passwordRecovery.validarYCambiarContraseña(correo, respuesta, nueva),
                cambiada -> {
                    if (cambiada) {
                        recoveryView.limpiar();
                        mostrarPantalla(LOGIN);
                        JOptionPane.showMessageDialog(this, "La contraseña se actualizó.");
                    } else {
                        mostrarError("La respuesta no coincide o no se pudo cambiar la contraseña.");
                    }
                });
    }

    // Ejecuta operaciones de base de datos sin bloquear la ventana.
    private <T> void ejecutarEnSegundoPlano(
            JButton boton, Task<T> tarea, Consumer<T> alCompletar) {
        boton.setEnabled(false);
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.ejecutar();
            }

            @Override
            protected void done() {
                boton.setEnabled(true);
                try {
                    alCompletar.accept(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("La operación fue interrumpida. Inténtalo de nuevo.");
                } catch (ExecutionException exception) {
                    Throwable causa = exception.getCause();
                    mostrarError("No se pudo completar la operación: "
                            + (causa == null ? exception.getMessage() : causa.getMessage()));
                }
            }
        }.execute();
    }

    // Navegación y mensajes compartidos
    private void mostrarPantalla(String pantalla) {
        cardLayout.show(screens, pantalla);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "MedCita", JOptionPane.ERROR_MESSAGE);
    }

    @FunctionalInterface
    private interface Task<T> {
        T ejecutar() throws Exception;
    }
}
