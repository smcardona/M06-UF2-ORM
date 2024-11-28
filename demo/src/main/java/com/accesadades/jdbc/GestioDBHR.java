package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GestioDBHR {
    public static void main(String[] args) {

        try (BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in))) {

            try {
                // Carregar propietats des de l'arxiu
                Properties properties = new Properties();
                try (InputStream input = GestioDBHR.class.getClassLoader().getResourceAsStream("config.properties")) {
                //try (FileInputStream input = new FileInputStream(configFilePath)) {
                    properties.load(input);
    
                    // Obtenir les credencials com a part del fitxer de propietats
                    String dbUrl = properties.getProperty("db.url");
                    String dbUser = properties.getProperty("db.username");
                    String dbPassword = properties.getProperty("db.password");
    
                    // Conectar amb MariaDB
                    try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                        System.out.println("Conexió exitosa");
    
                        String File_create_script = "db_scripts/DB_Schema_HR.sql" ;
    
                        InputStream input_sch = GestioDBHR.class.getClassLoader().getResourceAsStream(File_create_script);
    
                        CRUDHR crudbhr = new CRUDHR();
                        //Aquí farem la creació de la database i de les taules, a més d'inserir dades
                        crudbhr.CreateDatabase(connection,input_sch);

                        MenuOptions(br1,crudbhr,connection);
    
                    } catch (Exception e) {
                        System.err.println("Error al conectar: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Error al carregar fitxer de propietats: " + e.getMessage());
                }
            } finally {}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void MenuOptions(BufferedReader br, CRUDHR crudbhr, Connection connection) 
    throws NumberFormatException, IOException, SQLException {

        System.out.println("CONSULTA BD HR");
        System.out.println("OPCIONS");
        System.out.println("1. CARREGAR TAULA");
        System.out.println("2. CONSULTAR DADES");

        System.out.print("Introdueix l'opció tot seguit >>");

        int opcio = Integer.parseInt(br.readLine());

        switch(opcio) {
            case 1:
                String File_data_script = "db_scripts/DB_Data_HR.sql" ;
    
                InputStream input_data = GestioDBHR.class.getClassLoader().getResourceAsStream(File_data_script);

                crudbhr.CreateDatabase(connection,input_data);

                break;
            case 2:
                //Preguntem de quina taula volem mostrar
                MenuSelect(br,crudbhr,connection);
                break;

            default:
                System.out.print("Opcio no vàlida");
                MenuOptions(br,crudbhr,connection);
        }
    
    }

    public static void MenuSelect(BufferedReader br, CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {

        int opcio = 0;

        System.out.println("De quina taula vols mostrar els seus registres?");
        System.out.println("1. Departaments");
        System.out.println("2. Tasques");
        System.out.println("3. Històric de tasques");
        System.out.println("4. Empleats");
        System.out.println("5. Sortir");

        System.out.print("Introdueix l'opció tot seguit >>");

        opcio = Integer.parseInt(br.readLine());

        boolean sortir = true;

        while (sortir != false) {
            System.out.println("sortir: " + sortir);
            switch(opcio) {
                case 1:
                    crudbhr.ReadDatabase(connection, "DEPARTMENTS");
                    break;
                case 2:
                    crudbhr.ReadDatabase(connection, "JOBS");
                    break;
                case 3:
                    crudbhr.ReadDatabase(connection, "JOB_HISTORY");
                    break;
                case 4:
                    crudbhr.ReadDatabase(connection, "EMPLOYEES");
                    break;
                case 5:
                    sortir = false;
                    break;
                default:
                    System.out.print("Opcio no vàlida");
                    MenuSelect(br,crudbhr, connection);
            }

            System.out.println("Vols fer altra consulta? (S o N): ");
            String opcioB = br.readLine();
            if (opcioB.equals("N")){
                System.out.print("NO NO NO");
                opcio = 5;
                sortir = false;
                break;
            } else {
                System.out.print("aqui");
                MenuSelect(br,crudbhr, connection);
            }

            System.out.println("sortir - B: " + sortir);

        }

    }
}
