package com.accesadades.orm.util;

import java.util.Arrays;
import java.util.List;

/**
 * Esta libreria contiene funciones relacionadas a Strings y caracteres.
 * Proporciona metodos para controlar la forma en que se entienden cadenas de texto.
 */

public class UtilString {

  
  public static String normalizeSpace (String text) {
    if (text == null) return "";
    return text.trim().replaceAll("[ \t]+", " ");
  }

  /**
   * Strictly checks if a string can be used by Integer.parseInt
   * @param text String to check
   */
  public static boolean isInteger (String text) {
    if (text.length() == 0) return false;
    boolean isInteger = true;

    char fstChar = text.charAt(0);
    if (Character.isDigit(fstChar)|| (fstChar == '+' || fstChar == '-')) {
      // Doesnt allow single characters that arent numbers
      if ((fstChar == '+' || fstChar == '-') && text.length() == 1) return false;
      for (int i = 1; i < text.length(); i++) {
        char thisChar = text.charAt(i);
        isInteger = Character.isDigit(thisChar);                  
        if(!isInteger) break; // return false
      }
    }
    else isInteger = false;

    return isInteger;
  }

  private static final List<String> POSITIVE_KEYWORDS = Arrays.asList(new String[] {
    "s", "si", "sí", "bueno",
    "y", "yes", "ok"
  });
  /**
   * Este metodo recibe un String que debe ser:
   * "s", "si|í", "bueno", "y", "yes" o "ok" y lo considerará como un valor true
   * sino cualquier otro valor se considera false, incluso null
   */
  public static boolean answerToBool (String resposta) {
    if (null == resposta) return false;
    
    if (POSITIVE_KEYWORDS.contains(resposta.toLowerCase())) return true;

    return false;
  }
  
}
