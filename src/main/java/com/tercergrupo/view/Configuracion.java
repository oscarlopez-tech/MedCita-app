package com.tercergrupo.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Configuracion extends JDialog {
    public Configuracion(JFrame propietario) {
        super(propietario, "Configuración", true);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Gestión de Usuarios", new GestionUsuarios());

        JButton cerrar = EstiloMedCita.boton("Cerrar", false);
        cerrar.addActionListener(event -> dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setBackground(EstiloMedCita.FONDO);
        pie.add(cerrar);

        add(pestanas, BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(820, 520));
        pack();
        setLocationRelativeTo(propietario);
    }
}
