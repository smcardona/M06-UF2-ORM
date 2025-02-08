package com.accesadades.orm.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="aeropuerto")
public class Aeropuerto implements Serializable, Property.PropertyProvider {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private int id;

  @Column
  private String ciudad;

  public Aeropuerto() {}

  public Aeropuerto(String ciudad) {
    this.ciudad = ciudad;
  }

  public int getId() { return id; }
  public String getCiudad() { return ciudad; }

  public void setId(int id) {
    this.id = id;
  }

  public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
  }

  // PROPIEDADES
  @Transient
  private final Property<?>[] editableProperties = {
    
    //new Property<>("id", "ID", this::setId, this::getId, Integer::parseInt),
    new Property<String>("ciudad", "ciutat", this::setCiudad, this::getCiudad, getClass()),

  };

  @Transient
  private final Property<?>[] properties = {
    editableProperties[0] // solo para filtrado, ciudad
  };

  
  public Property<?>[] getEditableProperties() { return editableProperties; }
  
  public Property<?>[] getProperties() { return properties; }
  
}