package com.tercergrupo;

import com.tercergrupo.config.InicializadorBaseDatos;
import com.tercergrupo.view.MedCitaFrame;

import javax.swing.SwingUtilities;

public class MedCitaApp {
    public static void main(String[] args) {
        try {
            InicializadorBaseDatos.inicializar();
            SwingUtilities.invokeLater(() -> new MedCitaFrame().setVisible(true));
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
