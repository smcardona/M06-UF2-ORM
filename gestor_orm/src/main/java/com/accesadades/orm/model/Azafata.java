package com.accesadades.orm.model;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


@Entity
@Table(name="azafata")
public class Azafata implements Serializable, Property.PropertyProvider {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private int id;

  @Column
  private String name;

  @Column
  private String phone;

  @Column
  private String passport;

  @Column
  private String ig;

  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="vuelo")
  private Vuelo vuelo;

  public Azafata() {}

  public Azafata(String name, String passaport, String telefono, String ig) {
    this.name = name;
    this.phone = passaport;
    this.passport = telefono;
    this.ig = ig;
  }

  public int getId() { return id; }
  public String getName() { return name; }
  public String getPhone() { return phone; }
  public String getPassport() { return passport; }
  public String getIg() { return ig; }


  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setPassport(String passport) {
    this.passport = passport;
  }

  public void setIg(String ig) {
    this.ig = ig;
  }


  @Override
  public String toString() {
      return "Azafata{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", passport='" + passport + '\'' +
              ", phone='" + phone + '\'' +
              ", ig='" + ig + '\'' +
              '}';
  }

  // PROPIEDADES
  @Transient
  private final Property<?>[] editableProperties = {
    new Property<>("name", "nom", this::setName, this::getName, getClass()),
    new Property<>("passport", "passaport", this::setPassport, this::getPassport, getClass()),
    new Property<>("phone", "telèfon", this::setPhone, this::getPhone, getClass()),
    new Property<>("ig", "instagram", this::setIg, this::getIg, getClass())
  };
  // solo para filtrado
  @Transient
  private final Property<?>[] properties = {
    editableProperties[0], 
    editableProperties[1],
  };

  
  public Property<?>[] getEditableProperties() { return editableProperties; }
  
  public Property<?>[] getProperties() { return properties; }



}
