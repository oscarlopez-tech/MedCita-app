package com.tercergrupo.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

//  Interfaz visual para el aparta de Inicio de sesion, paleta de colores y tipo de letra. 

public final class EstiloMedCita {
    public static final Color FONDO = new Color(0xEFF6FF);
    private static final Color AZUL = new Color(0x2563EB);
    private static final Color TITULO = new Color(0x1E3A8A);
    private static final Color TEXTO = new Color(0x334155);
    private static final Color BORDE = new Color(0xBFDBFE);
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
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));

        pantalla.add(tarjeta);

        JLabel marca = etiqueta("MEDCITA", AZUL, FUENTE.deriveFont(Font.BOLD, 12));
        tarjeta.add(marca);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(etiqueta(titulo, TITULO, FUENTE.deriveFont(Font.BOLD, 20)));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(etiqueta(descripcion, TEXTO, FUENTE));
        tarjeta.add(Box.createVerticalStrut(20));
        return tarjeta;
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
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        }
        tarjeta.add(campo);
        tarjeta.add(Box.createVerticalStrut(14));
    }

    public static void aviso(JPanel tarjeta, JLabel etiqueta) {
        etiqueta.setFont(FUENTE.deriveFont(12f));
        etiqueta.setForeground(TITULO);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(etiqueta);
        tarjeta.add(Box.createVerticalStrut(14));
    }

    public static JButton boton(String texto, boolean principal) {
        JButton boton = new JButton(texto) {
        };

        boton.setUI(new BasicButtonUI());
        boton.setFont(FUENTE.deriveFont(Font.BOLD));
        boton.setForeground(AZUL);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setRolloverEnabled(true);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return boton;
    }

    public static void acciones(JPanel tarjeta, JButton... botones) {
        for (JButton boton : botones) {
            tarjeta.add(boton);
            tarjeta.add(Box.createVerticalStrut(8));
        }
    }
}
