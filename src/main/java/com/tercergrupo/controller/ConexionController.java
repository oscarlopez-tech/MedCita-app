package com.tercergrupo.controller;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionController {
    private static final String URL = "jdbc:mysql://sakura.proxy.rlwy.net:13307/railway?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "prZZuzZVZbraQdgoaubhoBCjmjjxZXkd";

    public static Connection conectar(){
        Connection conexion = null;

        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conexion != null && !conexion.isClosed()){
                System.out.println("Conexion exitosa a la base de datos de Railway");
            }
        } catch (Exception e) {
            System.out.println("Error al conectar a la base de datos: ");
        }
        return conexion;

    }

    
}
