package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GestioDBHR {
//Com veurem, aquesta booleana controla si volem sortir de l'aplicació.
    static boolean sortirapp = false;
    static boolean DispOptions = true;
        
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
                            while (sortirapp == false) {
                                MenuOptions(br1,crudbhr,connection);
                            }

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
    throws NumberFormatException, IOException, SQLException, InterruptedException {

        Terminal terminal = TerminalBuilder.builder().system(true).build();

        String message = "";
        message = "==================";
        printScreen(terminal, message);

        message = "CONSULTA BD HR";
        printScreen(terminal, message);

        message = "==================";
        printScreen(terminal, message);


        message = "OPCIONS";
        printScreen(terminal, message);

        message = "1. CARREGAR TAULA";
        printScreen(terminal, message);

        message = "2. CONSULTAR TOTES LES DADES";
        printScreen(terminal, message);

        message = "3. ALTRES CONSULTES";
        printScreen(terminal, message);

        message = "9. SORTIR";
        printScreen(terminal, message);


        message = "Introdueix l'opcio tot seguit >> ";
        for (char c : message.toCharArray()) {
            terminal.writer().print(c);
            terminal.flush();
            Thread.sleep(10);
        }

        int opcio = Integer.parseInt(br.readLine());

        switch(opcio) {
            case 1:
                String File_data_script = "db_scripts/DB_Data_HR.sql" ;
    
                InputStream input_data = GestioDBHR.class.getClassLoader().getResourceAsStream(File_data_script);

                if (crudbhr.CreateDatabase(connection,input_data) == true) {
                    System.out.println("Registres duplicats");
                } else {
                    System.out.println("Registres inserits amb éxit");
                }

                break;
            case 2:
                //Preguntem de quina taula volem mostrar
                MenuSelect(br,crudbhr,connection);
                break;

            case 3:
                MenuSelectAltres(br,crudbhr,connection);
                break;

            case 9:
                //sortim
                System.out.println("Adéu!!");
                sortirapp = true;
                break;
            default:
                System.out.print("Opcio no vàlida");
                MenuOptions(br,crudbhr,connection);
        }
    
    }

    private static void printScreen(Terminal terminal, String message) throws InterruptedException {
        for (char c : message.toCharArray()) {
            terminal.writer().print(c);
            terminal.flush();
            Thread.sleep(10);
        }
        System.out.println();
    }

    public static void MenuSelect(BufferedReader br, CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {

        int opcio = 0;

        while (DispOptions) {

            System.out.println("De quina taula vols mostrar els seus registres?");
            System.out.println("1. Departaments");
            System.out.println("2. Tasques");
            System.out.println("3. Històric de tasques");
            System.out.println("4. Empleats");
            System.out.println("5. Sortir");

            System.out.print("Introdueix l'opció tot seguit >> ");

            opcio = Integer.parseInt(br.readLine());

            switch(opcio) {
                case 1:
                    crudbhr.ReadAllDatabase(connection, "DEPARTMENTS");
                    break;
                case 2:
                    crudbhr.ReadAllDatabase(connection, "JOBS");
                    break;
                case 3:
                    crudbhr.ReadAllDatabase(connection, "JOB_HISTORY");
                    break;
                case 4:
                    crudbhr.ReadAllDatabase(connection, "EMPLOYEES");
                    break;
                case 5:

                    DispOptions = false;
                    break;
                default:
                    System.out.print("Opcio no vàlida");
            }
                
            if (DispOptions) {
                System.out.println("Vols fer altra consulta? (S o N): ");
                String opcioB = br.readLine();
        
                if (opcioB.equalsIgnoreCase("n")){
                    System.out.println("No, no marxis si us plau!");
                    DispOptions = false;
                    break;
                } 
            }
        }
    }

    public static void MenuSelectAltres(BufferedReader br, CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {

        int opcio = 0;

        while (DispOptions) {

            System.out.println("Quina consulta vols fer?");
            System.out.println("1. Departament per id");
            System.out.println("2. Rang de salaris d'empleats");

            System.out.print("Introdueix l'opció tot seguit >> ");

            opcio = Integer.parseInt(br.readLine());

            switch(opcio) {
                case 1:
                    System.out.println("Introdueix la id del departament >> ");
                    int idDept = Integer.parseInt(br.readLine());
                    crudbhr.ReadDepartamentsId(connection, "DEPARTMENTS", idDept);
                    break;
                case 2:
                    System.out.println("Introdueix el salari mínim dins el rang >> ");
                    float salMin = Float.parseFloat(br.readLine());
                    System.out.println("Introdueix el salari màxim dins el rang >> ");
                    float salMax = Float.parseFloat(br.readLine());
                    crudbhr.ReadSalaries(connection, "EMPLOYEES", salMin,salMax);
            }

        }

    }

}
