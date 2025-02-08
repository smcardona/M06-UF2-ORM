package com.accesadades.orm.model;

import com.accesadades.orm.util.executables.Getter;
import com.accesadades.orm.util.executables.Returner;
import com.accesadades.orm.util.executables.Setter;

public class Property<T> {

  public final String name;
  public final String displayName;
  private Setter<T> setter;
  private Getter<T> getter;
  private Returner<String, T> process = null;
  private boolean required = false;
  public final Class<T> type;

  @SuppressWarnings("unchecked")
  public Property(String name, String displayName, Setter<T> setter, Getter<T> getter, Class<?> parentClass) {
    this.name = name;
    this.displayName = displayName;
    this.setter = setter;
    this.getter = getter;
    try {
      this.type = (Class<T>) parentClass.getDeclaredField(name).getType();
    } catch (NoSuchFieldException | SecurityException e) {
      throw new RuntimeException(e.getMessage());
    }
  }



  @SuppressWarnings("unchecked")
  public void set(Object value) {

    T processedValue = null;

    if (process != null) {
      processedValue = process.execute(value.toString());
    }
    else {
      processedValue = (T) value;
    }

    setter.execute(processedValue);
  }

  public Property<T> with(Returner<String, T> process) {
    this.process = process;
    return this;
  }

  public T get() { return getter.get(); }


  public Property<T> required() {
    this.required = true;
    return this;
  }

  public boolean isRequired() { return required; }


  public static interface PropertyProvider {

    //public void initProperties();
    public Property<?>[] getProperties();
    public Property<?>[] getEditableProperties();
  }

}
