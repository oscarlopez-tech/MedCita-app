package com.tercergrupo;

import com.tercergrupo.view.MedCitaFrame;

import javax.swing.SwingUtilities;

public class MedCitaApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedCitaFrame().setVisible(true));
    }
}