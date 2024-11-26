package com.accesadades.jdbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class GestioDBHR {
    public static void main(String[] args) {
        try {
            // Ruta del archivo de configuración
            String configFilePath = "C:\\config\\demo\\config.properties";

            // Cargar propiedades desde el archivo
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
            }

            // Obtener credenciales
            String dbUrl = properties.getProperty("db.url");
            String dbUser = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");

            // Conectar a la base de datos
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Conexión exitosa a la base de datos");

        } catch (IOException e) {
            System.err.println("Error al cargar el archivo de configuración: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}
