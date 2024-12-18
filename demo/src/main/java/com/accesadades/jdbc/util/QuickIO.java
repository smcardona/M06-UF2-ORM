package com.accesadades.jdbc.util;

import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.accesadades.jdbc.util.exceptions.CancelCommandException;
import com.accesadades.jdbc.util.exceptions.ExitException;
import com.accesadades.jdbc.util.executables.Returner;
import com.accesadades.jdbc.util.executables.Setter;

public class QuickIO {

  private List<String> CANCEL_KEYWORDS = new ArrayList<String>(){};
  private List<String> EXIT_KEYWORDS = new ArrayList<String>(){};
  public QuickIO (String[] cancelKeywords, String[] exitKeywords) {
    if (cancelKeywords != null) CANCEL_KEYWORDS = Arrays.asList(cancelKeywords); 
    if (exitKeywords != null) EXIT_KEYWORDS = Arrays.asList(exitKeywords); 
  }

  public QuickIO() {}

  //* INSTANCE METHODS TO SET A DEFAULT EXIT KEYWORDS */
  public String getInputWithPrompt(String prompt) throws ExitException, CancelCommandException {
    Color.GRE.print(prompt);
    String input = UtilString.normalizeSpace(StdIn.readLine());

    if (CANCEL_KEYWORDS.contains(input))
      throw new CancelCommandException("Entrada de texto cancelada ...");

    if (EXIT_KEYWORDS.contains(input))
      throw new ExitException("Programa cancelado ...");

    return input;
  }

  /**
   * Muestra un prompt, recopila un string y lo procesa con una funcion que debe retornar un valor <p>
   * Ese valor será retornado y debe ser casteado al tipo querido. <p>
   * Cancela la accion usando las palabras clave en la instancia.
   * @param prompt Texto a mostrar como antes de la recopilación
   * @param returner Funcion que procesa el texto y retorna el valor procesado
   * @return Valor de tipo Object generico
   * @throws ExitException Excepcion causada por la recopilación de la palabra clave
   */
  public <R> R processAndReturn(String prompt, Returner<String, R> returner) throws ExitException, CancelCommandException {
    String input = "";
    try {
      input = getInputWithPrompt(prompt);
      return returner.execute(input);
    } 
    catch (NumberFormatException e) {
      Color.RED.printf("\"%s\" no es un número válido%n", input);
      return processAndReturn(prompt, returner); // PIDE DE NUEVO LA PROPIEDAD :D
    } 
    catch (IllegalArgumentException e) {
      Color.RED.println(e.getMessage());
      return processAndReturn(prompt, returner); // PIDE DE NUEVO LA PROPIEDAD :D
    }
  }

  /**
   * Muestra un prompt, recopila un string e intenta asignarlo usando un setter. <p>
   * Informa debidamente los errores segun el tipo de excepcion.<p>
   * Cancela la acción usando unas palabras clave seteada en la instancia.
   * @param prompt Texto a mostrar antes de recopilar la entrada
   * @param setter Ejecutor que se encarga de validar y lanzar debidos errores segun la entrada
   * @throws ExitException Excepcion causada por la recopilación de la palabra clave
   */
  public void requestAndSetValue(String prompt, Setter<String> setter) throws ExitException, CancelCommandException {
    String input = "";
    try {
      input = getInputWithPrompt(prompt);
      setter.execute(input);
    } 
    catch (NumberFormatException e) {
      Color.RED.printf("\"%s\" no es un número válido%n", input);
      requestAndSetValue(prompt, setter); // PIDE DE NUEVO LA PROPIEDAD :D
    } 
    catch (IllegalArgumentException e) {
      Color.RED.println(e.getMessage());
      requestAndSetValue(prompt, setter); // PIDE DE NUEVO LA PROPIEDAD :D
    }
  }

  public static String getInputWithPrompt (String prompt, String[] exceptionalKeywords) throws ExitException, CancelCommandException {
    return new QuickIO(exceptionalKeywords, null).getInputWithPrompt(prompt);
  }

  /**
   * Muestra un prompt, recopila un string e intenta asignarlo usando un setter. <p>
   * Informa debidamente los errores segun el tipo de excepcion. <p>
   * Cancela la acción usando unas palabras clave
   * @param prompt Texto a mostrar antes de recopilar la entrada
   * @param setter Ejecutor que se encarga de validar y lanzar debidos errores segun la entrada
   * @param exceptions Palabras clave para cancelar la recopilación.
   * @throws ExitException Excepcion causada por la ejecución de la palabra clave
   */
  public static void requestAndSetValue(String prompt, Setter<String> setter, String[] exceptions) throws ExitException, CancelCommandException {
    new QuickIO(exceptions, null).requestAndSetValue(prompt, setter);
  }

  public static Object processAndReturn(String prompt, Returner<String, ?> returner, String[] exceptions) throws ExitException, CancelCommandException {
    return new QuickIO(exceptions, null).processAndReturn(prompt, returner);
  }
  
  public static void mkdirIfNotExists(File file) {
    if (file == null) throw new IllegalArgumentException("No ha proporcionado un archivo para crear");
    if (file.exists() && file.isFile()) file.delete();

    if (file.exists()) return;

    file.mkdirs();
  }

  public static void storeFile(File file, String content) {
    try (
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));  
    ) {
      /* System.out.println("Guardando "+file.getAbsolutePath());
      System.out.println("Content: ");
  
      System.out.print(content+"<<<<<END\n"); */
      writer.write(content);
    } catch (IOException e) { throw new RuntimeException(e.getMessage()); }

    System.out.println("GUARDADO: "+file.getName());
  }

  public static void storeObject(File file, Object object) throws Exception {
    try (ObjectOutputStream serializer = new ObjectOutputStream(new FileOutputStream(file))) {
      serializer.writeObject(object);
    }
  }

  public static void storeBinaryFile(File file, byte[] data) {
    try (
      DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));  
    ) {
      writer.write(data);
    } catch (IOException e) { throw new RuntimeException(e.getMessage()); }

    System.out.println("GUARDADO: "+file.getName());
  }

  public static String readChars(DataInput data, int quantity) throws IOException {
    StringBuilder sbf = new StringBuilder();
    for (int i = 0; i < quantity; i++) {
      sbf.append(data.readChar());
    }
    return sbf.toString().trim();
  }

  public static void storeDocument(File file, Document doc) {
    Source source = new DOMSource(doc);
    try {
      Result result = new StreamResult(new FileWriter(file));
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
      transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
      transformer.transform(source, result);
    } 
    catch (Exception e) {
      Color.RED.println("Error al intentar guardar el archivo: "+e);
      Color.RED.println(e.getLocalizedMessage());

    }
  }
}