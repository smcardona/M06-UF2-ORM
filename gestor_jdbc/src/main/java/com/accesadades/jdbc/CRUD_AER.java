package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.accesadades.jdbc.util.Color;
import com.accesadades.jdbc.util.Paginator;
import com.accesadades.jdbc.util.QuickIO;
import com.accesadades.jdbc.util.UtilString;

public class CRUD_AER {

    private Connection connection;

    public CRUD_AER(Connection con) {
        connection = con;
    }

    
    public boolean createDatabase(InputStream input) 
    throws IOException, ConnectException, SQLException {

        boolean dupRecord = false;


        // EJECUTA STATEMENTS DE UN ARCHIVO.sql
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
                    System.err.println("SQL_ERROR: "+sqle.getMessage());
                    sqle.printStackTrace();
                } else {
                    dupRecord = true;
                    br.readLine();
                }
            }
        }

        return dupRecord;
    }

    public int insertPersona(Azafata persona) throws ConnectException, SQLException {

        String query = "INSERT INTO persona"  
                    + " (nombre, pasaporte, telefono)"
                    + " VALUES (?, ?, ?)";

        //recuperem valor inicial de l'autocommit
        boolean autocommitvalue = connection.getAutoCommit();

        //el modifiquem a false
        connection.setAutoCommit(false);

        try (PreparedStatement prepstat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            prepstat.setString(1, persona.getName());
            prepstat.setString(2, persona.getPassport());
            prepstat.setString(3, persona.getPhone());
            prepstat.executeUpdate();

            // Obtener el ID generado
            int id = -1;
            ResultSet generatedKeys = prepstat.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                System.out.println("ID assignat: " + id);
            }

            // Fem el commit
            connection.commit();

            System.out.println("Persona afegida correctament");

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

    public int insertAzafata(Azafata azafata) throws ConnectException, SQLException {

        String query = "INSERT INTO azafata (persona_id, ig) VALUES (?, ?)";

        //recuperem valor inicial de l'autocommit
        boolean autocommitvalue = connection.getAutoCommit();

        //el modifiquem a false
        connection.setAutoCommit(false);

        try (PreparedStatement prepstat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Primero inserta la azafata como persona y obtiene la id
            int id = insertPersona(azafata);

            prepstat.setInt(1, id);
            prepstat.setString(2, azafata.getIg());
            prepstat.executeUpdate();

            // Obtener el ID generado
            ResultSet generatedKeys = prepstat.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
                System.out.println("ID assignat: " + id);
            }

            // Fem el commit
            connection.commit();

            System.out.println("Hostessa afegida correctament");

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
    public void readAzafatas(String filter, Azafata.Filter fType) throws Exception {
        String query = """
                SELECT
                    p.ID as ID,
                    p.nombre as NOM,
                    p.pasaporte as PASSAPORT,
                    p.telefono as TELEFON,
                    a.ig as IG
                """ +
                "FROM azafata a join persona p on (a.persona_id = p.id) " +  
                (filter == null || fType == null ? "" : String.format("WHERE %s like ?", fType.name().toLowerCase()));

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            
            if (filter != null) {
                statement.setString(1, filter);
            }

            ResultSet rset = statement.executeQuery();
            
            //obtenim numero de columnes i nom
            int colNum = rset.getMetaData().getColumnCount();

            //Si el nombre de columnes és >0 procedim a llegir i mostrar els registres
            if (colNum > 0) {
                recorrerAzafatas(rset);
            }

        } catch (SQLException sqle) {
            Color.RED.println(sqle.getMessage());
        }
    }

    public void readAzafatas()  throws Exception {
        readAzafatas(null, null);
    }

    public Azafata readAzafata(int id) {
        String query = "SELECT p.id as id, p.nombre as nombre, p.pasaporte as pasaporte, p.telefono as telefono, a.ig as ig "
            + "FROM azafata a join persona p on (a.persona_id = p.id) "
            + "WHERE a.persona_id = ? ;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, id);

            ResultSet rset = statement.executeQuery();
            
            //Si tiene resultado: 
            if (rset.next()) {

                return new Azafata(
                    rset.getInt("id"), 
                    rset.getString("nombre"), 
                    rset.getString("pasaporte"), 
                    rset.getString("telefono"),
                    rset.getString("ig"));

            }

            else return null;
        } catch (SQLException sqle) {
            Color.RED.println(sqle.getMessage());
            return null;
        }
    }

    public void updateAzafata(Azafata toUpdate) throws Exception {
        String queryPersona = "UPDATE persona SET nombre = ?, pasaporte = ?, telefono = ? WHERE id = ?";
        String queryAzafata = "UPDATE azafata SET ig = ? WHERE persona_id = ?";


        try (PreparedStatement prepstat = connection.prepareStatement(queryPersona)) {
            prepstat.setString(1, toUpdate.getName());
            prepstat.setString(2, toUpdate.getPassport());
            prepstat.setString(3, toUpdate.getPhone());
            prepstat.setInt(4, toUpdate.getId());

            int rowsAffected = prepstat.executeUpdate();
            
            try (PreparedStatement prepstatAzafata = connection.prepareStatement(queryAzafata)) {
                prepstatAzafata.setString(1, toUpdate.getIg());
                prepstatAzafata.setInt(2, toUpdate.getId());

                rowsAffected += prepstatAzafata.executeUpdate();
                
                connection.commit();
            }

            

            if (rowsAffected > 0) {
                System.out.println("Hostessa actualitzada exitosament");
            } else {
                System.out.println("No s'ha trobat l'hostessa amb l'ID proporcionat");
            }
        } catch (SQLException sqle) {
            connection.rollback();
            Color.RED.println(sqle.getMessage());
        }
    }

    public void deleteAzafata(int id) throws Exception {

        String queryAzafata = "DELETE FROM azafata WHERE persona_id = ?";
        String queryPersona = "DELETE FROM persona WHERE id = ?";


        try (PreparedStatement prepstat = connection.prepareStatement(queryAzafata)) {
            prepstat.setInt(1, id);

            int rowsAffected = prepstat.executeUpdate();

            try(PreparedStatement prepPersona = connection.prepareStatement(queryPersona)) {
                prepPersona.setInt(1, id);

                rowsAffected += prepPersona.executeUpdate();
                connection.commit();
            }

            if (rowsAffected > 0) {
                System.out.println("Hostessa eliminada exitosament");
            } else {
                System.out.println("No s'ha trobat l'hostessa amb l'ID proporcionat");
            }
        } catch (SQLException sqle) {
            connection.rollback();
            Color.RED.println(sqle.getMessage());
        }

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

    public void recorrerAzafatas(ResultSet rs) throws Exception {
        ArrayList<Azafata> items = new ArrayList<Azafata>();
        while (rs.next()){

            items.add(
                new Azafata(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("passaport"),
                    rs.getString("telefon"),
                    rs.getString("ig")
                )
            );
        }

        Paginator pag = new Paginator(items.toArray(), 5, GestioDBAER.io);
        pag.start();

        if (UtilString.answerToBool(
            GestioDBAER.io.getInputWithPrompt("Vols emmagatzemar aquestes dades? ")
        )){
            QuickIO.mkdirIfNotExists(new File("xmls"));
            QuickIO.storeDocument(
                new File("xmls/"+System.currentTimeMillis()+".xml"), 
                QuickIO.azafatasToXML(items)
                );
        }
    }
    
    public void finishEverything() throws Exception {
        String query = "DROP DATABASE IF EXISTS santiago";

        try(Statement st = connection.createStatement()){

            st.execute(query);
            connection.close();

        }

        
    }
}
