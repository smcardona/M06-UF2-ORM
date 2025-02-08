package com.accesadades.orm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="vuelo")
public class Vuelo implements Serializable, Property.PropertyProvider {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private int id;

  // Aeropuerto origen
  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="origen")
  private Aeropuerto origen;

  // Aeropuerto destino
  @ManyToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="destino")
  private Aeropuerto destino;

  // Aviones usados en este vuelo
  @ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
  @JoinTable(
    name = "vuelo_avion",
    joinColumns = @JoinColumn(name = "vuelo_id"),
    inverseJoinColumns = @JoinColumn(name = "avion_id")
  )
  private Set<Avion> aviones = new HashSet<>();

  @OneToMany(mappedBy="vuelo", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
  private Set<Azafata> azafatas = new HashSet<>();

  public int getId() {
    return id;
  }

  public Aeropuerto getOrigen() { return origen; }
  public Aeropuerto getDestino() { return destino; }
  public Set<Avion> getAvion() { return aviones; }
  public Set<Azafata> getAzafatas() { return azafatas; }

  public void setId(int id) {
    this.id = id;
  }

  public void setOrigen(Aeropuerto origen) {
    this.origen = origen;
  }

  public void setDestino(Aeropuerto destino) {
    this.destino = destino;
  }

  public void setAviones(Set<Avion> aviones) {
    this.aviones = aviones;
  }

  public void setAzafatas(Set<Azafata> azafatas) {
    this.azafatas = azafatas;
  }
  
  // PROPIEDADES
  @Transient
  private final Property<?>[] editableProperties = {
    //new Property<>("id", "ID", this::setId, this::getId, Integer::parseInt),
    new Property<>("origen", "origen", this::setOrigen, this::getOrigen, getClass()).required(),
    new Property<>("destino", "desti", this::setDestino, this::getDestino, getClass()).required(),
    new Property<>("aviones", "avions", this::setAviones, this::getAvion, getClass()),
    new Property<>("azafatas", "hostesses", this::setAzafatas, this::getAzafatas, getClass())
  };
  // solo para filtrado
  @Transient
  private final Property<?>[] properties = {
    editableProperties[0], 
    editableProperties[1]
  };

  
  public Property<?>[] getEditableProperties() { return editableProperties; }
  public Property<?>[] getProperties() { return properties; }


  
  
}
