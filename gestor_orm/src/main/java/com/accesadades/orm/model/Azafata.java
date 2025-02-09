package com.accesadades.orm.model;

import java.io.Serializable;

import com.accesadades.orm.util.UtilString;

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

  public Azafata(String name, String passport, String phone, String ig) {
    this.name = name;
    this.passport = passport;
    this.phone = phone;
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
    name = UtilString.normalizeSpace(name);
    if (!UtilString.isValidString(name)) {
        throw new IllegalArgumentException("El nom ha de tenir almenys 4 caràcters.");
    }
    this.name = name;
  }

  public void setPhone(String phone) {
    phone = UtilString.normalizeSpace(phone);
    if (!UtilString.isValidString(phone)) {
        throw new IllegalArgumentException("El telèfon ha de tenir almenys 4 caràcters.");
    }
    this.phone = phone;
  }

  public void setPassport(String passport) {
    passport = UtilString.normalizeSpace(passport);
    if (!UtilString.isValidString(passport)) {
        throw new IllegalArgumentException("El passaport ha de tenir almenys 4 caràcters.");
    }
    this.passport = passport;
  }

  public void setIg(String ig) {
    ig = UtilString.normalizeSpace(ig);
    if (!UtilString.isValidString(ig)) {
        throw new IllegalArgumentException("L'Instagram ha de tenir almenys 4 caràcters.");
    }
    this.ig = ig;
  }

  public void setVuelo(Vuelo vuelo) {
    this.vuelo = vuelo;
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
