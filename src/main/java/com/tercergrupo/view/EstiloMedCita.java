package com.tercergrupo.view;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

//  Interfaz visual para el aparta de Inicio de sesion, paleta de colores y tipo de letra. 

public final class EstiloMedCita {
    public static final Color FONDO = new Color(0xEFF6FF);
    private static final Color AZUL = new Color(0x2563EB);
    private static final Color AZUL_OSCURO = new Color(0x1D4ED8);
    private static final Color TITULO = new Color(0x1E3A8A);
    private static final Color TEXTO = new Color(0x334155);
    private static final Color TEXTO_SUAVE = new Color(51, 69, 85, 150);
    private static final Color BORDE = new Color(0xBFDBFE);
    private static final Color MED = new Color(0x0284C7);
    private static final Color CITA = new Color(0x0F766E);
    private static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 14);

    private EstiloMedCita() { }

    public static void configurarDialogos() {
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("Panel.background", FONDO);
        UIManager.put("OptionPane.messageFont", FUENTE);
        UIManager.put("OptionPane.messageForeground", TITULO);
        UIManager.put("Button.font", FUENTE);
        UIManager.put("Button.background", AZUL);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("OptionPane.okButtonText", "Aceptar");
    }

    public static JPanel formulario(JPanel pantalla, String titulo, String descripcion) {
        pantalla.setLayout(new GridBagLayout());
        pantalla.setBackground(FONDO);
        pantalla.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, AZUL),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        BorderFactory.createEmptyBorder(24, 28, 24, 28))));

        pantalla.add(tarjeta);

        tarjeta.add(logo());
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(etiqueta(titulo, TITULO, FUENTE.deriveFont(Font.BOLD, 20)));
        if (descripcion != null && !descripcion.isBlank()) {
            tarjeta.add(Box.createVerticalStrut(8));
            tarjeta.add(etiqueta(descripcion, TEXTO, FUENTE));
        }
        tarjeta.add(Box.createVerticalStrut(20));
        return tarjeta;
    }

    private static JPanel logo() {
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logo.setOpaque(false);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.add(etiqueta("MED", MED, FUENTE.deriveFont(Font.BOLD, 20)));
        logo.add(etiqueta("CITA", CITA, FUENTE.deriveFont(Font.BOLD, 20)));
        logo.setMaximumSize(logo.getPreferredSize());
        return logo;
    }

    private static JLabel etiqueta(String texto, Color color, Font fuente) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    public static void campo(JPanel tarjeta, String texto, JComponent campo) {
        JLabel etiqueta = etiqueta(texto, TEXTO, FUENTE);
        etiqueta.setLabelFor(campo);
        tarjeta.add(etiqueta);
        tarjeta.add(Box.createVerticalStrut(6));
        campo.setFont(FUENTE);
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setPreferredSize(new Dimension(360, 36));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        if (campo instanceof JTextField) {
            campo.setBorder(bordeCampo(BORDE));
            campo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent evento) {
                    campo.setBorder(bordeCampo(AZUL));
                }

                @Override
                public void focusLost(FocusEvent evento) {
                    campo.setBorder(bordeCampo(BORDE));
                }
            });
        }
        tarjeta.add(campo);
        tarjeta.add(Box.createVerticalStrut(14));
    }

    private static Border bordeCampo(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    public static void aviso(JPanel tarjeta, JLabel etiqueta) {
        etiqueta.setFont(FUENTE.deriveFont(12f));
        etiqueta.setForeground(TITULO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(etiqueta);
        tarjeta.add(Box.createVerticalStrut(14));
    }

    public static void nota(JPanel tarjeta, String texto) {
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(etiqueta(texto, TEXTO_SUAVE, FUENTE.deriveFont(13f)));
    }

    public static JButton boton(String texto, boolean principal) {
        JButton boton = new JButton(texto) {
        };

        boton.setUI(new BasicButtonUI());
        boton.setFont(FUENTE.deriveFont(Font.BOLD));
        boton.setForeground(principal ? Color.WHITE : AZUL);
        boton.setBackground(AZUL);
        boton.setContentAreaFilled(principal);
        boton.setOpaque(principal);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setRolloverEnabled(true);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                boton.setBackground(principal ? AZUL_OSCURO : FONDO);
                boton.setContentAreaFilled(true);
                boton.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                boton.setBackground(AZUL);
                boton.setContentAreaFilled(principal);
                boton.setOpaque(principal);
            }
        });
        return boton;
    }

    public static void acciones(JPanel tarjeta, JButton... botones) {
        for (JButton boton : botones) {
            tarjeta.add(boton);
            tarjeta.add(Box.createVerticalStrut(8));
        }
    }
}
