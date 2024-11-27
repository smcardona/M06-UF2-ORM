package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class GestioDBHR {
    public static void main(String[] args) {
        try {
            // Ruta de l'arxiu de configuració
            String configFilePath = "C:\\Users\\46456715G\\M06-UF2-JDBC-HR\\demo\\src\\main\\resources\\config.properties";

            // Carregar propietats des de l'arxiu
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
            }

            // Obtenir les credencials com a part del fitxer de propietats
            String dbUrl = properties.getProperty("db.url");
            String dbUser = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");

            // Conectar amb MariaDB
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                System.out.println("Conexió exitosa");
                String File_script = "C:\\Users\\46456715G\\M06-UF2-JDBC-HR\\demo\\db_scripts\\MySQL_Schema_HR.sql" ;

                try (BufferedReader br = new BufferedReader(new FileReader(File_script))) {
                    StringBuilder sqlStatement = new StringBuilder();
                    String line;

                    try (Statement statement = connection.createStatement()) {
                        while ((line = br.readLine()) != null) {
                        // Ignorar comentaris i línies buides
                        line = line.trim();
                        
                            if (line.isEmpty() || line.startsWith("--") || line.startsWith("//") 
                         || line.startsWith("/*")) {
                            continue;
                            }

                            // Acumular la línea al buffer
                            sqlStatement.append(line);

                            // ";" es considera terminació de sentència SQL
                            if (line.endsWith(";")) {
                            // Eliminar ";" i executar la instrucción
                                String sql = sqlStatement.toString().replace(";", "").trim();
                                statement.execute(sql);

                            // Reiniciar el buffer para la siguiente instrucción
                                sqlStatement.setLength(0);
                            }
                        }
                    }
                }

            }
            
        } catch (IOException e) {
            System.err.println("Error al carregar l'arxiu de configuració: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
}
