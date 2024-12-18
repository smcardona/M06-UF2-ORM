package com.accesadades.jdbc.util.exceptions;

/**
 * Clase indicada para marcar Excepciones en el codigo provocadas por haberse encontrado un 
 * desencadenador que indique el fin de la ejecución de un comando
 */
public class CancelCommandException extends Exception {
  
  public CancelCommandException(String msg) { super(msg); }
}