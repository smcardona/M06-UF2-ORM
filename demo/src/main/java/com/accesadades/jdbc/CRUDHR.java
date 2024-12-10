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

public class CRUDHR {
    
    public boolean CreateDatabase(Connection connection, InputStream input) 
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

    public void InsertEmployee(Connection connection, String TableName, Employees employee) 
    throws ConnectException, SQLException {

        String query = "INSERT INTO " + TableName 
                    + " (EMPLOYEE_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE_INT, HIRE_DATE,"
                    + "JOB_ID, SALARY, COMMISSION_PCT, MANAGER_ID, DEPARTMENT_ID, BONUS)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

//recuperem valor inicial de l'autocommit
        boolean autocommitvalue = connection.getAutoCommit();

//el modifiquem a false
        connection.setAutoCommit(false);

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setInt(1, employee.getEmployeeId());
            prepstat.setString(2, employee.getFirstName());
            prepstat.setString(3, employee.getLastName());
            prepstat.setString(4, employee.getEmail());
            prepstat.setString(5, employee.getPhoneInt());
            prepstat.setString(6, employee.getHireDate());
            prepstat.setString(7, employee.getJobId());
            prepstat.setFloat(8, employee.getSalary());
            prepstat.setFloat(9, employee.getCommissionPct());
            prepstat.setInt(10, employee.getManagerId());
            prepstat.setInt(11, employee.getDepartmentId());
            prepstat.setString(12, employee.getBonus());

            prepstat.executeUpdate();

//Fem el commit
            connection.commit();

            System.out.println("Empleat afegit amb èxit");

//deixem l'autocommit com estava
            connection.setAutoCommit(autocommitvalue);
        
        } catch (SQLException sqle) {
            if (!sqle.getMessage().contains("Duplicate entry")) {
                System.err.println(sqle.getMessage());
            } else {
                System.out.println("Registre duplicat");
            }

            connection.rollback();
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
