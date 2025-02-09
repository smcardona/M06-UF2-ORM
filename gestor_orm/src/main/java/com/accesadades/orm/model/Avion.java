package com.accesadades.orm.model;

import java.io.Serializable;

import com.accesadades.orm.util.UtilString;

import jakarta.persistence.Transient;

// clase mapeada con hbm
public class Avion implements Serializable, Property.PropertyProvider {


  private int id;
  private String modelo;
  private int capacidad;

  public Avion(){}

  public Avion (String modelo, int cap) {
    this.modelo = modelo;
    this.capacidad = cap;
  }

  public int getId() { return id; }
  public String getModelo() { return modelo; }
  public int getCapacidad() { return capacidad; }

  public void setId(int id) {
    this.id = id;
  }

  public void setModelo(String modelo) {
    modelo = UtilString.normalizeSpace(modelo);
    if (!UtilString.isValidString(modelo)) {
        throw new IllegalArgumentException("El model ha de tenir almenys 4 caràcters.");
    }
    this.modelo = modelo;
  }

  public void setCapacidad(int capacidad) {
    this.capacidad = capacidad;
  }

  @Override
  public String toString() {
    return "Avion{" +
            "id=" + id +
            ", modelo='" + modelo + '\'' +
            ", capacidad=" + capacidad +
            '}';
  }

  
  // PROPIEDADES
  @Transient
  private final Property<?>[] editableProperties = {
    //new Property<>("id", "ID", this::setId, this::getId, Integer::parseInt),
    new Property<>("modelo", "model", this::setModelo, this::getModelo, getClass()),
    new Property<Integer>("capacidad", "capacitat", this::setCapacidad, this::getCapacidad, getClass())
      .with(Integer::parseInt)
  };

  @Transient
  private final Property<?>[] properties = {
    editableProperties[0] // solo para filtrado, modelo
  };

  
  public Property<?>[] getEditableProperties() { return editableProperties; }
  
  public Property<?>[] getProperties() { return properties; }


  
}
