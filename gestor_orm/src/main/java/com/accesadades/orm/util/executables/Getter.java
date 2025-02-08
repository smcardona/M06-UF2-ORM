package com.accesadades.orm.util.executables;

@FunctionalInterface
public interface Getter<T> {
  
  public T get();
}
