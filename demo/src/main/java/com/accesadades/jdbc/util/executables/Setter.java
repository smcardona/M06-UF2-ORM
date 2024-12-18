package com.accesadades.jdbc.util.executables;

/**
 * Esta interfaz permite recibir un valor por parametro en el ejecutable para luego 
 * ejecutar el codigo con dicho valor, el tipo del valor del parametro se debe indicar por el generico T.
 */
@FunctionalInterface
public interface Setter<T> {
  void execute(T value);
}
