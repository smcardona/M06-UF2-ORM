-- Inserción de datos en la tabla PERSONA
INSERT INTO PERSONA (nombre, pasaporte, telefono) VALUES
('Santiago Pérez', 'P1234567', '555-1234'),
('Carla García', 'P2345678', '555-5678'),
('Luis Fernández', 'P3456789', '555-9876'),
('Ana López', 'P4567890', '555-4567'),
('Jorge Morales', 'P5678901', '555-7890'),
('Laura Díaz', 'P6789012', '555-3456'),
('Diego Martínez', 'P7890123', '555-6543'),
('Sofía Ramírez', 'P8901234', '555-0123'),
('María Torres', 'P9012345', '555-9870'),
('Carlos Gómez', 'P0123456', '555-7891');

-- Inserción de datos en la tabla PILOTO
INSERT INTO PILOTO (persona_id, vuelos, nivel) VALUES
(1, 100, 'Capitán'),
(2, 80, 'Primer Oficial'),
(3, 120, 'Capitán'),
(4, 50, 'Primer Oficial');

-- Inserción de datos en la tabla PASAJERO
INSERT INTO PASAJERO (persona_id) VALUES
(5), (6), (7), (8), (9), (10);

-- Inserción de datos en la tabla AZAFATA
INSERT INTO AZAFATA (persona_id, ig) VALUES
(6, '@laura_diaz'),
(7, '@diego_martinez'),
(8, '@sofia_ramirez'),
(9, '@maria_torres'),
(10, '@carlos_gomez');

-- Inserción de datos en la tabla AEROPUERTO
INSERT INTO AEROPUERTO (ciudad) VALUES
('Madrid'),
('Barcelona'),
('Valencia'),
('Sevilla'),
('Bilbao'),
('Granada'),
('Málaga'),
('Alicante'),
('Zaragoza'),
('Santiago de Compostela');

-- Inserción de datos en la tabla AVION
INSERT INTO AVION (modelo, capacidad) VALUES
('Airbus A320', 180),
('Boeing 737', 200),
('Embraer E190', 100),
('Airbus A330', 280),
('Boeing 777', 350),
('ATR 72', 70),
('Boeing 747', 400),
('Airbus A321', 220),
('Bombardier CRJ200', 50),
('Airbus A350', 300);

-- Inserción de datos en la tabla VUELO
INSERT INTO VUELO (id_origen, id_destino, id_avion, id_piloto) VALUES
(1, 2, 1, 1),
(3, 4, 2, 2),
(5, 6, 3, 3),
(7, 8, 4, 4),
(9, 10, 5, 1),
(2, 3, 6, 2),
(4, 5, 7, 3),
(6, 7, 8, 4),
(8, 9, 9, 1),
(10, 1, 10, 2);

-- Inserción de datos en la tabla PASAJERO_VUELO
INSERT INTO PASAJERO_VUELO (id_pasajero, id_vuelo) VALUES
(5, 1), (6, 1), (7, 2), (8, 2), (9, 3),
(10, 3), (5, 4), (6, 4), (7, 5), (8, 5);

-- Inserción de datos en la tabla AZAFATA_VUELO
INSERT INTO AZAFATA_VUELO (id_azafata, id_vuelo) VALUES
(6, 1), (7, 1), (8, 2), (9, 2), (10, 3),
(6, 3), (7, 4), (8, 4), (9, 5), (10, 5);

-- Inserción de datos en la tabla CHECK_IN
INSERT INTO CHECK_IN (id_pasajero, id_vuelo, asiento) VALUES
(5, 1, '1A'), (6, 1, '1B'), (7, 2, '2A'), (8, 2, '2B'),
(9, 3, '3A'), (10, 3, '3B'), (5, 4, '4A'), (6, 4, '4B'),
(7, 5, '5A'), (8, 5, '5B');
