package com.tercergrupo.controller;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionController {
    private static final String  URL = "jdbc:mysql://nozomi.proxy.rlwy.net:41111/railway?useSSL=true&requireSSL=false&trustServerCertificate=true&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "KDAQqgbbJZrhfdboLsLyhGfiNryFCwyk";

    public static Connection conectar(){
        Connection conexion = null;

        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conexion != null && !conexion.isClosed()){
                System.out.println("Conexion exitosa a la base de datos de Railway");
            }
        } catch (Exception e) {
            System.out.println("Error al conectar a la base de datos: " + e);
        }
        return conexion;

    }

    
}
