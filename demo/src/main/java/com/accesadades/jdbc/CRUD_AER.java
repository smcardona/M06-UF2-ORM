package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class CRUD_AER {
    
    public boolean createDatabase(Connection connection, InputStream input) 
    throws IOException, ConnectException, SQLException {

        boolean dupRecord = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
            StringBuilder sqlStatement = new StringBuilder();
            String line;

            try (Statement statement = connection.createStatement()) {
                while ((line = br.readLine()) != null) {
                // Ignorar comentaris i línies buides
                    line = line.trim();
                        
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("//") || line.startsWith("/*")) {
                            continue;
                    }

                    // Acumular la línea al buffer
                    sqlStatement.append(line);

                    // el caràcter ";" es considera terminació de sentència SQL
                    if (line.endsWith(";")) {
                    // Eliminar el ";" i executar la instrucción
                        String sql = sqlStatement.toString().replace(";", "").trim();
                        statement.execute(sql);

                        // Reiniciar el buffer para la siguiente instrucción
                        sqlStatement.setLength(0);
                    }
                }
            } catch (SQLException sqle) {
                if (!sqle.getMessage().contains("Duplicate entry")) {
                    System.err.println(sqle.getMessage());
                } else {
                    dupRecord = true;
                    br.readLine();
                }
            }
        }

        return dupRecord;
    }

    public int insertPersona(Connection connection, String TableName, Pasajero pasajero) 
    throws ConnectException, SQLException {

        String query = "INSERT INTO PERSONA"  
                    + " (nombre, pasaporte, telefono)"
                    + " VALUES (?,?, ?)";

        //recuperem valor inicial de l'autocommit
        boolean autocommitvalue = connection.getAutoCommit();

        //el modifiquem a false
        connection.setAutoCommit(false);

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setString(1, pasajero.getName());
            prepstat.setString(2, pasajero.getPassaport());
            prepstat.setString(3, pasajero.getTelefono());
            prepstat.executeUpdate();

            // Obtener el ID generado
            int id = -1;
            ResultSet generatedKeys = prepstat.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                System.out.println("ID asignado: " + id);
            }

            // Fem el commit
            connection.commit();

            System.out.println("Persona agregada exitosa mente");

            //deixem l'autocommit com estava
            connection.setAutoCommit(autocommitvalue);
            return id;
        
        } catch (SQLException sqle) {
            if (!sqle.getMessage().contains("Duplicate entry")) {
                System.err.println(sqle.getMessage());
            } else {
                System.out.println("Registre duplicat");
            }

            connection.rollback();
            return -1;
        }

    }

    public int insertPasajero(Connection connection, String TableName, Pasajero pasajero) 
    throws ConnectException, SQLException {

        String query = "INSERT INTO PASAJERO"  
                    + " (persona_id)"
                    + " VALUES (?)";

        //recuperem valor inicial de l'autocommit
        boolean autocommitvalue = connection.getAutoCommit();

        //el modifiquem a false
        connection.setAutoCommit(false);

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            int id = insertPersona(connection, TableName, pasajero);

            prepstat.setInt(1, id);
            prepstat.executeUpdate();

            // Obtener el ID generado
            ResultSet generatedKeys = prepstat.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                System.out.println("ID asignado: " + id);
            }

            // Fem el commit
            connection.commit();

            System.out.println("Persona agregada exitosa mente");

            //deixem l'autocommit com estava
            connection.setAutoCommit(autocommitvalue);
            return id;
        
        } catch (SQLException sqle) {
            if (!sqle.getMessage().contains("Duplicate entry")) {
                System.err.println(sqle.getMessage());
            } else {
                System.out.println("Registre duplicat");
            }

            connection.rollback();
            return -1;
        }

    }

    //Read sense prepared statements, mostra tots els registres
    public void ReadAllDatabase(Connection connection, String TableName) throws ConnectException, SQLException {
        try (Statement statement = connection.createStatement()) {
            
            String query = "SELECT * FROM " + TableName + ";";

            ResultSet rset = statement.executeQuery(query);
            
            //obtenim numero de columnes i nom
            int colNum = getColumnNames(rset);

            //Si el nombre de columnes és >0 procedim a llegir i mostrar els registres
            if (colNum > 0) {

                recorrerRegistres(rset,colNum);

            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public void ReadDepartamentsId(Connection connection, String TableName, int id) 
    throws ConnectException, SQLException {

        String query = "SELECT * FROM " + TableName + " WHERE department_id = ?";

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setInt(1, id);
            ResultSet rset = prepstat.executeQuery();

            int colNum = getColumnNames(rset);

            //Si el nombre de columnes és >0 procedim a llegir i mostrar els registres
            if (colNum > 0) {

                recorrerRegistres(rset,colNum);

            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public void ReadSalaries(Connection connection, String TableName, float salMin, float salMax) 
    throws ConnectException, SQLException {

        String query = "SELECT EMPLOYEE_ID, FIRST_NAME, LAST_NAME, SALARY FROM " 
                     + TableName + " WHERE salary BETWEEN ? AND ?";

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setFloat(1, salMin);
            prepstat.setFloat(2, salMax);
            ResultSet rset = prepstat.executeQuery();

            int colNum = getColumnNames(rset);

            //Si el nombre de columnes és >0 procedim a llegir i mostrar els registres
            if (colNum > 0) {

                recorrerRegistres(rset,colNum);

            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }


    //Aquest mètode auxiliar podria ser utileria del READ, mostra el nom de les columnes i quantes n'hi ha
    public static int getColumnNames(ResultSet rs) throws SQLException {
        
        int numberOfColumns = 0;
        
        if (rs != null) {   
            ResultSetMetaData rsMetaData = rs.getMetaData();
            numberOfColumns = rsMetaData.getColumnCount();   
        
            for (int i = 1; i < numberOfColumns + 1; i++) {  
                String columnName = rsMetaData.getColumnName(i);
                System.out.print(columnName + ", ");
            }
        }
        
        System.out.println();

        return numberOfColumns;
        
    }

    public void recorrerRegistres(ResultSet rs, int ColNum) throws SQLException {

        while(rs.next()) {
            for(int i =0; i<ColNum; i++) {
                if(i+1 == ColNum) {
                    System.out.println(rs.getString(i+1));
                } else {
            
                System.out.print(rs.getString(i+1)+ ", ");
                }
            } 
        }
            
    }
        
}
