package com.tercergrupo.controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionController {
    private static final String  URL = "jdbc:mysql://nozomi.proxy.rlwy.net:41111/railway?useSSL=true&requireSSL=false&trustServerCertificate=true&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "KDAQqgbbJZrhfdboLsLyhGfiNryFCwyk";

    public static Connection conectar(){
        Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conexion != null && !conexion.isClosed()){
                System.out.println("Conexion exitosa a la base de datos de Railway");
            }
        } catch (ClassNotFoundException|SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;

    }

    
}
