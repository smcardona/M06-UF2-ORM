DROP DATABASE IF EXISTS santiago;
create database if not exists santiago;


use santiago;

CREATE TABLE IF NOT EXISTS PERSONA (

    id INT  PRIMARY key AUTO_INCREMENT,

    nombre VARCHAR(100) NOT NULL,

    pasaporte VARCHAR(50) UNIQUE NOT NULL,

    telefono VARCHAR(20)

);


CREATE TABLE IF NOT EXISTS PILOTO (

    persona_id INT PRIMARY KEY,

    vuelos INT NOT NULL,

    nivel VARCHAR(50),

    FOREIGN KEY (persona_id) REFERENCES PERSONA(id)

);


CREATE TABLE IF NOT EXISTS PASAJERO (

    persona_id INT PRIMARY KEY,

    FOREIGN KEY (persona_id) REFERENCES PERSONA(id)

);


CREATE TABLE IF NOT EXISTS AZAFATA (

    persona_id INT PRIMARY KEY,

    ig VARCHAR(100),

    FOREIGN KEY (persona_id) REFERENCES PERSONA(id)

);


CREATE TABLE IF NOT EXISTS AEROPUERTO (

    id INT  PRIMARY key AUTO_INCREMENT,

    ciudad VARCHAR(100) NOT NULL

);


CREATE TABLE IF NOT EXISTS AVION (

    id INT  PRIMARY key AUTO_INCREMENT,

    modelo VARCHAR(50) NOT NULL,

    capacidad INT NOT NULL

);


CREATE TABLE IF NOT EXISTS VUELO (

    id INT  PRIMARY key AUTO_INCREMENT,

    id_origen INT NOT NULL,

    id_destino INT NOT NULL,

    id_avion INT NOT NULL,

    id_piloto INT NOT NULL,

    FOREIGN KEY (id_origen) REFERENCES AEROPUERTO(id),

    FOREIGN KEY (id_destino) REFERENCES AEROPUERTO(id),

    FOREIGN KEY (id_avion) REFERENCES AVION(id),

    FOREIGN KEY (id_piloto) REFERENCES PILOTO(persona_id)

);


CREATE TABLE IF NOT EXISTS PASAJERO_VUELO (

    id_pasajero INT NOT NULL,

    id_vuelo INT NOT NULL,

    PRIMARY KEY (id_pasajero, id_vuelo),

    FOREIGN KEY (id_pasajero) REFERENCES PASAJERO(persona_id),

    FOREIGN KEY (id_vuelo) REFERENCES VUELO(id)

);


CREATE TABLE IF NOT EXISTS AZAFATA_VUELO (

    id_azafata INT NOT NULL,

    id_vuelo INT NOT NULL,

    PRIMARY KEY (id_azafata, id_vuelo),

    FOREIGN KEY (id_azafata) REFERENCES AZAFATA(persona_id),

    FOREIGN KEY (id_vuelo) REFERENCES VUELO(id)

);


CREATE TABLE IF NOT EXISTS CHECK_IN (

    id_pasajero INT NOT NULL,

    id_vuelo INT NOT NULL,

    asiento VARCHAR(10) NOT NULL,

    PRIMARY KEY (id_pasajero, id_vuelo),

    FOREIGN KEY (id_pasajero) REFERENCES PASAJERO(persona_id),

    FOREIGN KEY (id_vuelo) REFERENCES VUELO(id)

);


