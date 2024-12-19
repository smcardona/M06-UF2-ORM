package com.accesadades.jdbc;

import java.io.InputStream;
import com.accesadades.jdbc.util.QuickIO;
import com.accesadades.jdbc.util.UtilString;
import com.accesadades.jdbc.Azafata.Filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


public class GestioDBAER {
//Com veurem, aquesta booleana controla si volem sortir de l'aplicació.
    static boolean sortirapp = false;
    static boolean DispOptions = true;
    static QuickIO io = new QuickIO();

    static CRUD_AER crud;
        
    public static void main(String[] args) {

        processClean();

        try {
            // Carregar propietats des de l'arxiu
            Properties properties = new Properties();
            try (InputStream input = GestioDBAER.class.getClassLoader().getResourceAsStream("config.properties")) {
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

                    InputStream input_sch = GestioDBAER.class.getClassLoader().getResourceAsStream(File_create_script);

                    crud = new CRUD_AER(connection);
                    //Aquí farem la creació de la database i de les taules, a més d'inserir dades
                    crud.createDatabase(input_sch);
                    while (sortirapp == false) {
                        menuOptions();
                    }

                } catch (Exception e) {
                    System.err.println("Error al conectar: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al carregar fitxer de propietats: " + e.getMessage());
        }
    }

    public static void menuOptions() throws Exception {

        String message = """
                ==================
                AEROPORT EL RAPT
                ==================

                OPCIONS
                1. CARREGAR DADES DE PROVA
                2. CONSULTAR DADES (READ)
                3. MODIFICAR DADES (UPDATE)
                4. ELIMINAR DADES (DELETE)
                5. INSERIR NOVES DADEs (CREATE)
                6. SORTIR
                """;
        
        System.out.println(message);


        int opcio = Integer.parseInt(io.getInputWithPrompt("Introdueix l'opcio tot seguit >> "));

        switch(opcio) {
            case 1:
                String File_data_script = "db_scripts/DB_Data_HR.sql" ;
    
                InputStream input_data = GestioDBAER.class.getClassLoader().getResourceAsStream(File_data_script);

                if (crud.createDatabase(input_data) == true) {
                    System.out.println("Registres duplicats");
                } else {
                    System.out.println("Registres inserits amb éxit");
                }

                break;
            case 2:
                menuSelectAzafatas();
                break;

            case 3:
                menuEditAzafata();
                break;

            case 4:
                menuDeleteAzafata();
                break;

            case 5:
                menuInsertAzafata();
                break;

            case 6:
                //sortim
                System.out.println("Adèu!!");
                sortirapp = true;
                break;
            default:
                System.out.print("Opcio no vàlida");
                menuOptions();
        }
    
    }

    public static void menuSelectAzafatas() throws Exception {

        int opcio = 0;
        DispOptions = true;

        while (DispOptions) {

            System.out.print("""
                    Com vols obtenir la informació de la azafata
                    1. Per ID
                    2. Per nom
                    3. Per passaport
                    4. Sense filtrar
                    5. Sortir
                    """);


            opcio = Integer.parseInt(
                io.getInputWithPrompt("Introdueix l'opció tot seguit >> ")
            );

            switch(opcio) {
                case 1:
                    System.out.println(
                        crud.readAzafata(Integer.parseInt(io.getInputWithPrompt("ID >> ")))
                    );
                    break;
                case 2:
                    crud.readAzafatas(
                        io.getInputWithPrompt("Nom >> "), Filter.NOMBRE);
                    break;
                case 3:
                    crud.readAzafatas(
                        io.getInputWithPrompt("Passaport >> "), Filter.PASAPORTE);
                    break;
                case 4: 
                    crud.readAzafatas();
                    break;
                case 5:
                    DispOptions = false;
                    return;
                default:
                    System.out.print("Opcio no vàlida");
            }
                
            DispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols fer altra consulta? (S o N):"));
        }
    }

    public static void menuEditAzafata() throws Exception {

        DispOptions = true;

        while (DispOptions) {

            int id = io.processAndReturn("ID Hostessa >> ", Integer::parseInt);

            Azafata toEdit = crud.readAzafata(id);

            if (toEdit == null) {
                System.out.println("Azafata no encontrada con este ID");
                continue;
            }

            io.requestAndSetValue(
                String.format("Nom (%s) >> ", toEdit.getName()),
                name -> {
                    if (name == null || name.isBlank()) return;
                    toEdit.setName(name);
                }
            );
            

            io.requestAndSetValue(
                String.format("Passaport (%s) >> ", toEdit.getPassport()),
                pass -> {
                    if (pass == null || pass.isBlank()) return;
                    toEdit.setPassport(pass);
                }
            );

            io.requestAndSetValue(
                String.format("Telèfon (%s) >> ", toEdit.getPhone()),
                pho -> {
                    if (pho == null || pho.isBlank()) return;
                    toEdit.setPhone(pho);
                }
            );

            io.requestAndSetValue(
                String.format("Ig (%s) >> ", toEdit.getIg()),
                ig -> {
                    if (ig == null || ig.isBlank()) return;
                    toEdit.setIg(ig);
                }
            );

            crud.updateAzafata(toEdit);

            DispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols continuar editant hostesses? ")
            );
            

        }

    }

    public static void menuDeleteAzafata() throws Exception {

        DispOptions = true;

        while (DispOptions) {

            
            int id = io.processAndReturn("ID Hostessa a eliminar >> ", Integer::parseInt);

            crud.deleteAzafata(id);

            DispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols continuar eliminant hostesses? ")
            );

        }
    }

    public static void menuInsertAzafata() throws Exception {
        
        DispOptions = true;

        while (DispOptions) {

            
            String nom = io.getInputWithPrompt("Nom >> ");

            String pass = io.getInputWithPrompt("Passaport >> ");

            String tel = io.getInputWithPrompt("Telèfon >> ");

            String ig = io.getInputWithPrompt("Ig >> ");


            Azafata toInsert = new Azafata(nom, pass, tel, ig);

            crud.insertAzafata(toInsert);

            DispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols afegir més hostesses? ")
            );

        }
    }


    public static void processClean() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
    
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (Exception e) {
                for (int i = 0; i < 15; i++)
                System.out.println();
            }
            return;
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
