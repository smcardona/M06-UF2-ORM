package com.accesadades.orm;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import com.accesadades.orm.model.Aeropuerto;
import com.accesadades.orm.model.Avion;
import com.accesadades.orm.model.Azafata;
import com.accesadades.orm.model.Property;
import com.accesadades.orm.model.Vuelo;
import com.accesadades.orm.model.Property.PropertyProvider;
import com.accesadades.orm.util.Color;
import com.accesadades.orm.util.Paginator;
import com.accesadades.orm.util.QuickIO;
import com.accesadades.orm.util.UtilString;
import com.accesadades.orm.util.exceptions.CancelCommandException;
import com.accesadades.orm.util.exceptions.DatabaseException;
import com.accesadades.orm.util.exceptions.ExitException;


public class Main {

  public static QuickIO io = new QuickIO(new String[]{ "cancel" }, new String[]{ ".exit" });

  private static final Class<?>[] ENTITATS = {
    Avion.class, Aeropuerto.class, Azafata.class, Vuelo.class
  };


  public static void main(String[] args) {

    while (true) {
      try {
        menuOptions();
      } 
      catch (CancelCommandException e) {
        System.out.println("Comanda cancelada");
      } 
      catch(ExitException e) {
        System.out.println("Sortint del programa");
        break;
      } 
      catch (DatabaseException e) {
        Color.RED.println("Error intentan fer acció a la base de dades: "+e.getLocalizedMessage());
      } 
      catch (Exception e) {
        Color.RED.println("Error inesperat al programa !");
        Color.RED.println(e.getLocalizedMessage());
        break;
      }


    }


    //DAO.finishEverything();

  }

    
  public static void menuOptions() throws ExitException, CancelCommandException  {

    String message = """
      ==================
      AEROPORT EL RAPT
      ==================

      OPCIONS
      1. CARREGAR DADES DE PROVA
      2. INSERIR NOVES DADES (CREATE)
      3. CONSULTAR DADES (READ)
      4. MODIFICAR DADES (UPDATE)
      5. ELIMINAR DADES (DELETE)
      6. SORTIR
      (cancel, .exit per cancelar comandes o sortir del programa)
      """;
      
      System.out.println(message);


      int opcio = io.processAndReturn("Introdueix l'opcio tot seguit >> ", Integer::parseInt);

      switch(opcio) {
        case 1:
          loadDemoData();
          break;

        
        case 2:
          menuInsert();
          break;
      
        case 3:
          menuSelect();
          break;

        case 4:
          menuEdit();
          break;

        case 5:
          menuDelete();
          break;

        case 6:
          throw new ExitException();

        default:
          System.out.print("Opcio no vàlida");
          menuOptions();

      }
    
    }

  public static void menuSelect() throws ExitException, CancelCommandException  {

    // pide un tipo, lo instancia, y extrae las propiedades
    Class<?> type = menuSelectType();
    String entName = type.getSimpleName();
    System.out.println("Obtenint dades de "+entName);

    Property<?>[] props = instanceFromType(type).getProperties();

    while (true) {
      System.out.println("Per quin camp vols filtrar la selecció ?");
      System.out.println(String.format("1. ID de %s", entName));
      System.out.println(String.format("2. Mostrar tots els valors de %s", entName));
      // Muestra opciones de campos
      for (int i = 3; i < props.length +3; i++) {
        Property<?> p = props[i-3];
  
        System.out.println(String.format("%d. %s de %s", i, p.displayName, entName));
      }
      // pilla opcion valida
      int choice = io.processAndReturn("Opció > ", val -> {
        int c = Integer.parseInt(val);
        if (c < 1 || c >= props.length+3) throw new IllegalArgumentException("Opció invalida");
        return c;
      });
  
      // Si es 1 = por id
      if (choice == 1) {
        int id = io.processAndReturn("ID >> ", Integer::parseInt);
        
        Object result = DAO.with(type).getById(id);
        if (result == null) 
          Color.RED.println("Cap coincidencia amb aquesta ID :(");
        
        else 
          System.out.println(result.toString());
      }
      // Si es 2 = sin filtrar
      else if(choice == 2) {
        List<?> results = DAO.with(type).getAll();
        if (results.size() == 0) 
          Color.RED.println("Cap valor per l'entitat "+entName);
  
        else {
          System.out.println(results.size() +" coincidencies");
          for (Object item: results) 
            System.out.println(item.toString());
        }
      }
      // Si es otro, por campo
      else {
        Property<?> p = props[choice-3]; 
        String value = io.getInputWithPrompt(p.displayName+" >> ");
  
        List<?> results = DAO.with(type).filterBy(p, value);
        if (results.size() == 0) 
          Color.RED.println("Cap coincidencia amb aquest "+p.displayName);
  
        else {
          System.out.println(results.size() +" coincidencies");
          for (Object item: results) 
            System.out.println(item.toString());
        }
  
      }
  
      // volver a consultar?
      boolean more = io.processAndReturn("Vols continuar buscant instancies de "+entName + " ? ",
        UtilString::answerToBool);
  
      if (!more) break;
    }


  }

  // Funcion que setea las propiedades de un objeto
  public static void fillProperties(PropertyProvider instance) throws ExitException, CancelCommandException {

    // instance puede ser un new o un getReference
    
    for (Property<?> prop: instance.getEditableProperties()) {

      do { // Bucle que se repite si la propiedad es requeria y no es obtenida correctamente

        try {

          Class<?> type = prop.type;

          //* Si es otro objecto con propiedades
          if (PropertyProvider.class.isAssignableFrom((Class<?>) type)) {
            // funcion larga pero util
            Object value = genOrGetInstanceFrom(type);

            if (value == null && prop.isRequired()) {
              Color.RED.println("Aquest camp es requerit !!!");
              continue; // Vuelve a pedir el campo
            } 

            prop.set(value);
            break; // necesario para acabar el bucle de required

          }
          //* Si es un set de objetos seguramente PropertyProviders
          else if (Set.class.isAssignableFrom((Class<?>) type)) {
            // movida extraña para pillar el tipo del generico en el Set
            Type genericType = ((ParameterizedType) instance.getClass().getDeclaredField(prop.name).getGenericType()).getActualTypeArguments()[0];
            //* POR FIN, EL TIPO DE ELEMENTOS QUE CONTIENE EL SET
            Class<?> genericClass = (Class<?>) genericType;
            
            @SuppressWarnings("unchecked")
            Set<Object> set = (Set<Object>) prop.get();

            // Rellena el set de items
            boolean addMore;
            do {
              Object item = genOrGetInstanceFrom(genericClass);
              if (item != null) { set.add(item); }

              addMore = io.processAndReturn("Vols generar un/a altre \"" + genericClass.getSimpleName() + "\" ? ",
                UtilString::answerToBool);

            } while (addMore);

            if (set.isEmpty() && prop.isRequired()) {
              Color.RED.println("Aquest camp es requerit !!!");
              continue; // Vuelve a pedir el campo
            }

            prop.set(set); // no se si es necesario, pero por si acaso
            break; // necesario para acabar el bucle de required

          } 
          //* Si es otro tipo de campo: Strings, ints, etc
          else {

            String prompt = String.format("%s %s %s>> ", instance.getClass().getSimpleName(), prop.displayName,
              (prop.get() != null) ? "("+prop.get()+") " : "");
            io.requestAndSetValue(prompt, (value) -> prop.set(value));
            break;
          }

        } catch (CancelCommandException e) {

          // Comprobar que está seguro

          Color.YEL.println("Estas segur que vols cancel·lar aquest comand? Es perdran els valors introduits per: "+
            instance.getClass().getSimpleName());

          if (io.processAndReturn("Cancel·lar (si / no) :", UtilString::answerToBool)) 
            throw new CancelCommandException();

        } catch (SecurityException | NoSuchFieldException e) {
          throw new RuntimeException("Error en el programa, provocat pel tipatge y propietats al intentar omplir els camps d'una entitat");
        } 
        

      } while (prop.isRequired());
    }
  }

  // Funcion que genera una instancia o la obtiene de un listado en la base de datos
  public static PropertyProvider genOrGetInstanceFrom(Class<?> type) throws ExitException, CancelCommandException {

    if (!PropertyProvider.class.isAssignableFrom(type)) {
      throw new RuntimeException("La classe no implementa propietats: "+type.getSimpleName());
    }
    
    
    boolean generate = io.processAndReturn("Vols generar un/a nou/va \"" + type.getSimpleName() + "\" ? ",
      UtilString::answerToBool);

    PropertyProvider instance = null;

    if (generate) {
      // Crea una instancia del tipo que requiera la propiedad
      instance = instanceFromType(type) ;
      // Rellena las propiedades de la instancia
      fillProperties(instance);
    }
    // da un listado de elementos 
    else {

      System.out.println("Triant de ja existents");

      List<?> results = DAO.with(type).getAll();
      Paginator<?> pag = new Paginator<>(results.toArray(), 5, io);
      // Muestra listado con los resultados que enuentra de la base de datos
      pag.chose();
      instance = (PropertyProvider) pag.getChoice();

    }

    return instance;

  }

  public static void menuEdit() throws ExitException, CancelCommandException  {

    Class<?> type = menuSelectType();
    String entName = type.getSimpleName();
    System.out.println("Modificant dades de "+entName);

    while (true) {

      int id = io.processAndReturn(String.format(
        "ID de %s", type.getSimpleName()), Integer::parseInt);

      PropertyProvider instance = (PropertyProvider) DAO.with(type).getById(id);
      fillProperties(instance);

      System.out.println("Objecte modificat correctament!");
      System.out.println(instance.toString());


      boolean more = io.processAndReturn("Vols continuar editant instancies de "+entName + " ? ",
        UtilString::answerToBool);

      if (!more) break;
      
    }

  }

  public static void menuDelete() throws ExitException, CancelCommandException  {

    Class<?> type = menuSelectType();
    String entName = type.getSimpleName();
    System.out.println("Eliminant dades de "+entName);

    while (true) {

      int id = io.processAndReturn(String.format(
        "ID de %s", type.getSimpleName()), Integer::parseInt);


      PropertyProvider instance = (PropertyProvider) DAO.with(type).getById(id);

      DAO.with(type).remove(instance);

      System.out.println("Objecte eliminat correctament!");
      System.out.println(instance.toString());

      boolean more = io.processAndReturn("Vols continuar editant instancies de "+entName + " ? ",
        UtilString::answerToBool);

      if (!more) break;
      
    }

  
  }

  public static void menuInsert() throws ExitException, CancelCommandException {

    Class<?> type = menuSelectType();
    String entName = type.getSimpleName();
    System.out.println("Creant "+entName);
    
    while (true) {
      
      PropertyProvider instance = instanceFromType(type);
      fillProperties(instance);
      DAO.with(type).save(instance);

      System.out.println("Objecte agregat correctament!");
      System.out.println(instance.toString());
      

      boolean more = io.processAndReturn("Vols afegir més instancies de "+entName + " ? ",
        UtilString::answerToBool);

      if (!more) break;

    }
  }

  public static Class<?> menuSelectType() throws ExitException, CancelCommandException {

    System.out.println();
    System.out.println("De quina entitat vols fer aquesta operació ?");

    for (int i = 1; i < ENTITATS.length+1; i++) {
      System.out.printf("%d. %s%n", i, ENTITATS[i-1].getSimpleName());
    }

    int choice = io.processAndReturn("Opció > ", val -> {
      int c = Integer.parseInt(val);
      if (c < 1 || c >= ENTITATS.length+1) throw new IllegalArgumentException("Opció invalida");
      return c;
    });

    return ENTITATS[choice-1];
  }

  public static PropertyProvider instanceFromType(Class<?> type) {

    // Esto se puede hacer más facilmente con un Map o cosas asi, TODO si hay que seguir escalando el programa

    if (type == Avion.class) return new Avion();
    if (type == Aeropuerto.class) return new Aeropuerto();
    if (type == Azafata.class) return new Azafata();
    if (type == Vuelo.class) return new Vuelo();

    throw new RuntimeException("Error, tipus no valid com entitat: "+type.getSimpleName());
    
  }

  public static void processClean() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {

      try {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } catch (Exception e) {
        for (int i = 0; i < 15; i++) System.out.println();
      }
      return;
    }
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }


  public static void loadDemoData() throws DatabaseException {
    // Create Aeropuertos
    Aeropuerto aeropuerto1 = new Aeropuerto("Barcelona");
    Aeropuerto aeropuerto2 = new Aeropuerto("Madrid");
    Aeropuerto aeropuerto3 = new Aeropuerto("Valencia");

    DAO.with(Aeropuerto.class).save(aeropuerto1);
    DAO.with(Aeropuerto.class).save(aeropuerto2);
    DAO.with(Aeropuerto.class).save(aeropuerto3);

    // Create Aviones
    Avion avion1 = new Avion("Boeing 737", 180);
    Avion avion2 = new Avion("Airbus A320", 160);
    Avion avion3 = new Avion("Boeing 747", 400);
    Avion avion4 = new Avion("Airbus A380", 500);
    Avion avion5 = new Avion("Embraer 190", 100);

    DAO.with(Avion.class).save(avion1);
    DAO.with(Avion.class).save(avion2);
    DAO.with(Avion.class).save(avion3);
    DAO.with(Avion.class).save(avion4);
    DAO.with(Avion.class).save(avion5);

    // Create Azafatas
    Azafata azafata1 = new Azafata("Maria", "12345678A", "600123456", "@maria");
    Azafata azafata2 = new Azafata("Laura", "87654321B", "600654321", "@laura");
    Azafata azafata3 = new Azafata("Ana", "11223344C", "600112233", "@ana");

    DAO.with(Azafata.class).save(azafata1);
    DAO.with(Azafata.class).save(azafata2);
    DAO.with(Azafata.class).save(azafata3);

    // Create Vuelos
    Vuelo vuelo1 = new Vuelo();
    vuelo1.setOrigen(aeropuerto1);
    vuelo1.setDestino(aeropuerto2);
    vuelo1.getAvion().add(avion1);
    vuelo1.getAvion().add(avion2);
    vuelo1.getAzafatas().add(azafata1);
    vuelo1.getAzafatas().add(azafata2);

    Vuelo vuelo2 = new Vuelo();
    vuelo2.setOrigen(aeropuerto2);
    vuelo2.setDestino(aeropuerto3);
    vuelo2.getAvion().add(avion3);
    vuelo2.getAvion().add(avion4);
    vuelo2.getAzafatas().add(azafata2);
    vuelo2.getAzafatas().add(azafata3);

    DAO.with(Vuelo.class).save(vuelo1);
    DAO.with(Vuelo.class).save(vuelo2);
  }

}
