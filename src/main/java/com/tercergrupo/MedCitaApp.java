package com.tercergrupo;

import com.tercergrupo.controller.ConexionController;

public class MedCitaApp {
    public static void main(String[] args) {
        System.out.println("Iniciando MedCitaApp...");
        
        // Verificamos conexión inicial a Railway
        ConexionController.conectar();
        System.out.println("--------------------------------------------------\n");
    }
}