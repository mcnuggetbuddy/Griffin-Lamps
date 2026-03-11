-- =====================================
-- 1) CREAR USUARIO
-- =====================================
DROP USER IF EXISTS 'admin'@'localhost';
CREATE USER 'admin'@'localhost'
IDENTIFIED BY '123456';

-- =====================================
-- 2) CREAR BASE DE DATOS
-- =====================================
DROP DATABASE IF EXISTS griffin_lamps;

CREATE DATABASE griffin_lamps
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON griffin_lamps.*
TO 'admin'@'localhost';

FLUSH PRIVILEGES;

USE griffin_lamps;

-- =====================================
-- 3) TABLA COLECCION
-- =====================================
CREATE TABLE coleccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE (nombre),
    INDEX ndx_nombre (nombre)

) ENGINE=InnoDB;

-- =====================================
-- 4) TABLA PRODUCTO
-- =====================================
CREATE TABLE producto (

    id_producto INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,

    precio_colones DECIMAL(10,2) NOT NULL,

    existencias INT UNSIGNED NOT NULL DEFAULT 0,
    tipo_luz VARCHAR(50) NOT NULL,
    material VARCHAR(100) NOT NULL,
    tipo_conexion VARCHAR(50) NOT NULL,
    duracion_bateria VARCHAR(50) NULL,

    activo BOOLEAN NOT NULL DEFAULT TRUE,
    destacado BOOLEAN NOT NULL DEFAULT FALSE,

    coleccion_id INT NOT NULL,

    CONSTRAINT fk_producto_coleccion
        FOREIGN KEY (coleccion_id)
        REFERENCES coleccion(id),

    INDEX idx_producto_coleccion (coleccion_id)

) ENGINE=InnoDB;

-- =====================================
-- 5) TABLA VARIANTE
-- =====================================
CREATE TABLE variante (

    id INT AUTO_INCREMENT PRIMARY KEY,

    talla VARCHAR(50) NOT NULL,
    precio_extra DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    producto_id INT NOT NULL,

    CONSTRAINT fk_variante_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id_producto)
        ON DELETE CASCADE,

    INDEX idx_variante_producto (producto_id)

) ENGINE=InnoDB;

-- =====================================
-- 6) TABLA COLOR
-- =====================================
CREATE TABLE color (

    id INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(50) NOT NULL,
    codigo_hex VARCHAR(7) NOT NULL,

    producto_id INT NOT NULL,

    CONSTRAINT fk_color_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id_producto)
        ON DELETE CASCADE,

    INDEX idx_color_producto (producto_id)

) ENGINE=InnoDB;

-- =====================================
-- 7) TABLA IMAGEN_PRODUCTO
-- =====================================
CREATE TABLE imagen_producto (

    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Aquí se guarda la URL de Firebase
    ruta_imagen VARCHAR(1024) NOT NULL,

    producto_id INT NOT NULL,

    CONSTRAINT fk_imagen_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id_producto)
        ON DELETE CASCADE,

    INDEX idx_imagen_producto (producto_id)

) ENGINE=InnoDB;

-- =====================================
-- 8) TABLA PEDIDO
-- =====================================
CREATE TABLE pedido (

    id INT AUTO_INCREMENT PRIMARY KEY,

    numero_orden VARCHAR(20) NOT NULL UNIQUE,

    nombre_cliente VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,

    direccion_envio VARCHAR(300) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,

    total_colones DECIMAL(10,2) NOT NULL,

    fecha DATE NOT NULL

) ENGINE=InnoDB;

-- =====================================
-- 9) TABLA DETALLE_PEDIDO
-- =====================================
CREATE TABLE detalle_pedido (

    id INT AUTO_INCREMENT PRIMARY KEY,

    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unit DECIMAL(10,2) NOT NULL,

    color VARCHAR(50) NULL,
    talla VARCHAR(50) NULL,

    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,

    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedido(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id_producto),

    INDEX idx_detalle_pedido (pedido_id),
    INDEX idx_detalle_producto (producto_id)

) ENGINE=InnoDB;

-- =====================================
-- 10) DATOS DE PRUEBA
-- =====================================

-- Colecciones
INSERT INTO coleccion (nombre) VALUES
    ('Nórdica'),
    ('Industrial'),
    ('Minimalista'),
    ('RGB Gaming');

INSERT INTO producto
(nombre, descripcion, precio_colones, existencias, tipo_luz, material, tipo_conexion, duracion_bateria, activo, destacado, coleccion_id)
VALUES

('Acryon',
 'Lámpara de mesa escandinava con luz cálida regulable ideal para dormitorios y salas.',
 35000, 20, 'Cálida', 'Madera y tela', 'Enchufe', NULL, TRUE, TRUE, 1),

('Akari',
 'Lámpara metálica estilo loft industrial perfecta para estudios y oficinas modernas.',
 58000, 12, 'Blanca', 'Metal', 'Enchufe', NULL, TRUE, TRUE, 2),

('Dona',
 'Lámpara compacta con batería recargable ideal para lectura nocturna.',
 22000, 30, 'Natural', 'ABS', 'USB', '8 horas', TRUE, FALSE, 3),

('Kinoko',
 'Lámpara LED RGB con control por app para setups gaming.',
 45000, 15, 'RGB', 'Acrílico', 'USB', '12 horas', TRUE, TRUE, 4),

('Macaron',
 'Lámpara colgante minimalista con madera natural.',
 42000, 8, 'Cálida', 'Madera', 'Enchufe', NULL, TRUE, TRUE, 1);

 
-- Variantes
INSERT INTO variante (talla, precio_extra, producto_id) VALUES
('Pequeña',0,1),
('Grande',8000,1),

('Estándar',0,2),

('Pequeña',0,3),
('Mediana',5000,3),

('Estándar',0,4),

('Estándar',0,5);

-- Colores
INSERT INTO color (nombre,codigo_hex,producto_id) VALUES

('Blanco','#FFFFFF',1),
('Negro','#000000',1),

('Gris','#808080',2),
('Negro','#000000',2),

('Blanco','#FFFFFF',3),

('Negro','#000000',4),
('Blanco','#FFFFFF',4),

('Madera','#D2B48C',5);

-- Imágenes
INSERT INTO imagen_producto (ruta_imagen, producto_id) VALUES
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F1%2FAcryon-1.JPG?alt=media&token=f68c0811-b98e-49de-8151-2ccfc9eaaf22', 1),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F1%2FAcryon-2.JPG?alt=media&token=2e82e8e1-8dfe-4b76-9804-c33b44d2452c', 1),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F1%2FAcryon-3.JPG?alt=media&token=366f5a5b-ae6f-4af7-9b4d-ecd0759a4dd5', 1),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F2%2FAkari-1.jpg?alt=media&token=66d9b84f-f97e-424f-b7a9-9b9b8a105727', 2),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F2%2FAkari-2.jpg?alt=media&token=fadd9991-72b2-4fc3-9efa-5a54d4949e61', 2),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F2%2FAkari-3.jpg?alt=media&token=0f71ce6a-8d25-4bdc-89f6-66d517a1fd09', 2),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F3%2FDona-1.5.jpg?alt=media&token=7a80f7e5-1398-49a9-b375-0b569ec97861', 3),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F3%2FDona-1.jpg?alt=media&token=a8cb9c16-148b-4b71-ae64-92a90442c455', 3),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F3%2FDona-2.5.jpg?alt=media&token=1c49fa27-20e2-4b12-bcc6-2988a7f87272', 3),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F4%2FKinoko-1.JPG?alt=media&token=d3b9e873-793f-4ec0-a0b3-4db9b383df60', 4),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F4%2FKinoko-2.JPG?alt=media&token=d8d2c8d0-cb8b-41d2-80bc-ea0cf500aa9b', 4),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F4%2FKinoko-3.JPG?alt=media&token=889aaec1-ae0a-45f7-b582-97af3b8ac7ae', 4),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F5%2FMacaron-1.jpg?alt=media&token=14920a60-ee91-4637-8b20-529c24d105a1', 5),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F5%2FMacaron-2.jpg?alt=media&token=c12111bf-0d50-4831-b49a-187ad6bc1924', 5),
    ('https://firebasestorage.googleapis.com/v0/b/griffin-lamps.firebasestorage.app/o/productos%2F5%2FMacaron-5.jpg?alt=media&token=1bf23148-810b-47ac-9956-19cf2a26b867', 5);

-- Pedido de prueba
INSERT INTO pedido (numero_orden, nombre_cliente, correo, telefono, direccion_envio, metodo_pago, total_colones, fecha) VALUES
    ('ORD-20260301-001', 'Laura Jiménez', 'laura@email.com', '88001234', 'San José, Costa Rica', 'Sinpe Móvil', 57000.00, '2026-03-01');

-- Detalle pedido de prueba
INSERT INTO detalle_pedido (cantidad, precio_unit, color, talla, pedido_id, producto_id) VALUES
    (1, 35000.00, 'Blanco', 'Pequeño', 1, 1),
    (1, 22000.00, 'Blanco', 'Pequeño', 1, 3);
