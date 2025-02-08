package com.accesadades.orm;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import javax.management.RuntimeErrorException;

/* 
 * IDEAS PARA QUE NO SE ME OLVIDEN
 * - Cuando hay una entidad que contiene otra entidad (vuelo > aeropuertos),
 *   dar opción de si crear el aeropuerto a mano llamando a otra funcion de crear para esa entidad o
 *   dar opción de listar los existentes (Paginator con entity.getAll)
 *   tal ves hacer campos requeridos, como los aeropuertos, cancelar el aeropuerto llevaria a cancelar el comando
 *   hacer campos opcionales
 */


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
import com.accesadades.orm.util.exceptions.ExitException;


public class Main {

  private static boolean dispOptions = true;
  public static QuickIO io = new QuickIO(new String[]{ "cancel" }, new String[]{ "exit" });

  private static DAO<Azafata> azafataDao = new DAO<>(Azafata.class);
  private static DAO<Avion> avionDao = new DAO<>(Avion.class);
  private static DAO<Aeropuerto> aeropuertoDao = new DAO<>(Aeropuerto.class);
  private static DAO<Vuelo> vueloDao = new DAO<>(Vuelo.class);

  public static void main(String[] args) {

    System.out.println("PROBANDO PROGRAMA");

    try {
      fillProperties(new Azafata());
      fillProperties(new Avion());
      fillProperties(new Aeropuerto());
      fillProperties(new Vuelo());
    } catch (Exception e) {
      e.printStackTrace();
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
                5. INSERIR NOVES DADES (CREATE)
                6. SORTIR
                """;
        
        System.out.println(message);


        int opcio = io.processAndReturn("Introdueix l'opcio tot seguit >> ", Integer::parseInt);

        switch(opcio) {
            case 1:
                // TODO: datos demo
                break;
        
            case 2:
                menuSelect();
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
                throw new ExitException();
            default:
                System.out.print("Opcio no vàlida");
                menuOptions();
        }
    
    }

  public static void menuSelect() throws Exception {

        int opcio = 0;
        dispOptions = true;

        while (dispOptions) {

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
                        azafataDao.getById((io.processAndReturn("ID >> ", Integer::parseInt)))
                    );
                    break;
                case 2:
                    //azafataDao.filterBy(null, aeropuertoDao)(io.getInputWithPrompt("Nom >> "), Filter.NOMBRE);
                    break;
                case 3:
                    //crud.readAzafatas(io.getInputWithPrompt("Passaport >> "), Filter.PASAPORTE);
                    break;
                case 4: 
                    //crud.readAzafatas();
                    break;
                case 5:
                    return;
                default:
                    System.out.print("Opcio no vàlida");
            }
                
            dispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols fer altra consulta? (S o N):"));
        }
    }

  // Funcion que setea las propiedades de un objeto
  public static void fillProperties(PropertyProvider instance) throws ExitException {

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

            prop.set(set); // no se si es necesario esto
            break; // necesario para acabar el bucle de required

          } 
          //* Si es otro tipo de campos: Strings, ints, etc
          else {

            String prompt = String.format("%s %s >> ", instance.getClass().getSimpleName(), prop.displayName);
            io.requestAndSetValue(prompt, (value) -> prop.set(value));
            break;
          }

        } catch (CancelCommandException e) {

          // Comprobar que está seguro

          System.out.println("Estas segur que vols cancel·lar aquest comand? Es perdran els valors introduits per: "+
            instance.getClass().getSimpleName());




        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
          throw new RuntimeException("Error en el programa, provocat pel tipatge y propietats al intentar omplir els camps d'una entitat");
        } 
        

      } while (prop.isRequired());
    }
  }

  // Funcion que genera una instancia o la obtiene de un listado en la base de datos
  // Ignorar los throws xd
  public static Object genOrGetInstanceFrom(Class<?> type) 
    throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
    NoSuchMethodException, SecurityException, ExitException, CancelCommandException {

    if (!PropertyProvider.class.isAssignableFrom(type)) {
      throw new RuntimeException("La classe no implementa propietats: "+type.getSimpleName());
    }
    
    
    boolean generate = io.processAndReturn("Vols generar un nou \"" + type.getSimpleName() + "\" ? ",
      UtilString::answerToBool);

    Object instance = null;

    if (generate) {
      // Crea una instancia del tipo que requiera la propiedad
      instance =  type.getConstructor().newInstance();
      // Rellena las propiedades de la instancia
      fillProperties((PropertyProvider) instance);
    }
    // da un listado de elementos 
    else {

      System.out.println("Triant de ja existents");

      List<?> results = DAO.getAllFrom(type);
      Paginator<?> pag = new Paginator<>(results.toArray(), 5, io);
      // Muestra listado con los resultados que enuentra de la base de datos
      pag.start();
      instance = pag.getChoice();

    }

    return instance;

  }

    public static void menuEditAzafata() throws Exception {

        dispOptions = true;

        while (dispOptions) {

            int id = io.processAndReturn("ID Hostessa >> ", Integer::parseInt);

            Azafata toEdit = azafataDao.getById(id);

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

            azafataDao.update(toEdit);

            dispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols continuar editant hostesses? ")
            );
            

        }

    }

    public static void menuDeleteAzafata() throws Exception {

        dispOptions = true;
        

        while (dispOptions) {

            
            int id = io.processAndReturn("ID Hostessa a eliminar >> ", Integer::parseInt);

            //azafataDao.deleteAzafata(id);

            dispOptions = UtilString.answerToBool(
                io.getInputWithPrompt("Vols continuar eliminant hostesses? ")
            );

        }
    }

    public static void menuInsertAzafata() throws Exception {
        
        dispOptions = true;

        while (dispOptions) {

            
            String nom = io.getInputWithPrompt("Nom >> ");

            String pass = io.getInputWithPrompt("Passaport >> ");

            String tel = io.getInputWithPrompt("Telèfon >> ");

            String ig = io.getInputWithPrompt("Ig >> ");


            Azafata toInsert = new Azafata(nom, pass, tel, ig);

            //crud.insertAzafata(toInsert);

            dispOptions = UtilString.answerToBool(
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
