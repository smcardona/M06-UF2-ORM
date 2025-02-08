package com.accesadades.orm.util.exceptions;

/**
 * Clase indicada para marcar Excepciones en el codigo provocadas por haberse encontrado un 
 * desencadenador que indique el fin de un determinado programa
 */
public class ExitException extends Exception {

  public ExitException (String msg) { super(msg); }

  public ExitException () {
    super("Sortint del programa");
  }
}