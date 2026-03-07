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
    id     INT          AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

-- =====================================
-- 4) TABLA PRODUCTO
-- =====================================
CREATE TABLE producto (
    id               INT            AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100)   NOT NULL,
    descripcion      VARCHAR(1000)  NOT NULL,
    precio_colones   DECIMAL(10,2)  NOT NULL,
    tipo_luz         VARCHAR(50)    NOT NULL,
    material         VARCHAR(100)   NOT NULL,
    tipo_conexion    VARCHAR(50)    NOT NULL,
    duracion_bateria VARCHAR(50)    NULL,
    activo           BOOLEAN        NOT NULL DEFAULT TRUE,
    destacado        BOOLEAN        NOT NULL DEFAULT FALSE,
    coleccion_id     INT            NOT NULL,
    FOREIGN KEY (coleccion_id) REFERENCES coleccion(id)
) ENGINE = InnoDB;

-- =====================================
-- 5) TABLA VARIANTE (talla/tamaño)
-- =====================================
CREATE TABLE variante (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    talla       VARCHAR(50)   NOT NULL,
    precio_extra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    producto_id INT           NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE = InnoDB;

-- =====================================
-- 6) TABLA COLOR
-- =====================================
CREATE TABLE color (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL,
    codigo_hex  VARCHAR(7)   NOT NULL,
    producto_id INT          NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE = InnoDB;

-- =====================================
-- 7) TABLA IMAGEN_PRODUCTO
-- =====================================
CREATE TABLE imagen_producto (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    ruta_imagen VARCHAR(1024) NOT NULL,
    producto_id INT           NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE = InnoDB;

-- =====================================
-- 8) TABLA PEDIDO
-- =====================================
CREATE TABLE pedido (
    id               INT           AUTO_INCREMENT PRIMARY KEY,
    numero_orden     VARCHAR(20)   NOT NULL UNIQUE,
    nombre_cliente   VARCHAR(100)  NOT NULL,
    correo           VARCHAR(150)  NOT NULL,
    telefono         VARCHAR(20)   NOT NULL,
    direccion_envio  VARCHAR(300)  NOT NULL,
    metodo_pago      VARCHAR(50)   NOT NULL,
    total_colones    DECIMAL(10,2) NOT NULL,
    fecha            DATE          NOT NULL
) ENGINE = InnoDB;

-- =====================================
-- 9) TABLA DETALLE_PEDIDO
-- =====================================
CREATE TABLE detalle_pedido (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    cantidad    INT           NOT NULL,
    precio_unit DECIMAL(10,2) NOT NULL,
    color       VARCHAR(50)   NULL,
    talla       VARCHAR(50)   NULL,
    pedido_id   INT           NOT NULL,
    producto_id INT           NOT NULL,
    FOREIGN KEY (pedido_id)   REFERENCES pedido(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE = InnoDB;

-- =====================================
-- 10) DATOS DE PRUEBA
-- =====================================

-- Colecciones
INSERT INTO coleccion (nombre) VALUES
    ('Nórdica'),
    ('Industrial'),
    ('Minimalista'),
    ('RGB Gaming');

-- Productos
INSERT INTO producto (nombre, descripcion, precio_colones, tipo_luz, material, tipo_conexion, duracion_bateria, activo, destacado, coleccion_id) VALUES
    ('Luna Nórdica', 'Lámpara de escritorio con diseño escandinavo y luz cálida regulable.', 35000.00, 'Cálida', 'Madera y tela', 'Enchufe', NULL, TRUE, TRUE, 1),
    ('Bolt Industrial', 'Lámpara de pie estilo industrial con acabado metálico.', 58000.00, 'Blanca', 'Metal', 'Enchufe', NULL, TRUE, TRUE, 2),
    ('Zen Mini', 'Pequeña lámpara de mesa con luz natural perfecta para lectura.', 22000.00, 'Natural', 'Plástico ABS', 'USB', '8 horas', TRUE, FALSE, 3),
    ('AuraRGB Pro', 'Lámpara LED RGB con control de colores vía app para gamers.', 45000.00, 'RGB', 'Acrílico y metal', 'Inalámbrica', '12 horas', TRUE, TRUE, 4),
    ('Beam Nórdico', 'Lámpara colgante de madera con pantalla de tela natural.', 42000.00, 'Cálida', 'Madera y tela', 'Enchufe', NULL, TRUE, FALSE, 1);

-- Variantes
INSERT INTO variante (talla, precio_extra, producto_id) VALUES
    ('Pequeño', 0.00, 1),
    ('Grande', 8000.00, 1),
    ('Estándar', 0.00, 2),
    ('Pequeño', 0.00, 3),
    ('Mediano', 5000.00, 3),
    ('Estándar', 0.00, 4),
    ('Estándar', 0.00, 5);

-- Colores
INSERT INTO color (nombre, codigo_hex, producto_id) VALUES
    ('Blanco', '#FFFFFF', 1),
    ('Negro', '#000000', 1),
    ('Gris', '#808080', 2),
    ('Negro', '#000000', 2),
    ('Blanco', '#FFFFFF', 3),
    ('Negro', '#000000', 4),
    ('Blanco', '#FFFFFF', 4),
    ('Natural', '#D2B48C', 5);

-- Imágenes
INSERT INTO imagen_producto (ruta_imagen, producto_id) VALUES
    ('/img/luna1.jpg', 1),
    ('/img/luna2.jpg', 1),
    ('/img/luna3.jpg', 1),
    ('/img/bolt1.jpg', 2),
    ('/img/bolt2.jpg', 2),
    ('/img/bolt3.jpg', 2),
    ('/img/zen1.jpg', 3),
    ('/img/zen2.jpg', 3),
    ('/img/zen3.jpg', 3),
    ('/img/aura1.jpg', 4),
    ('/img/aura2.jpg', 4),
    ('/img/aura3.jpg', 4),
    ('/img/beam1.jpg', 5),
    ('/img/beam2.jpg', 5),
    ('/img/beam3.jpg', 5);

-- Pedido de prueba
INSERT INTO pedido (numero_orden, nombre_cliente, correo, telefono, direccion_envio, metodo_pago, total_colones, fecha) VALUES
    ('ORD-20260301-001', 'Laura Jiménez', 'laura@email.com', '88001234', 'San José, Costa Rica', 'Sinpe Móvil', 57000.00, '2026-03-01');

-- Detalle pedido de prueba
INSERT INTO detalle_pedido (cantidad, precio_unit, color, talla, pedido_id, producto_id) VALUES
    (1, 35000.00, 'Blanco', 'Pequeño', 1, 1),
    (1, 22000.00, 'Blanco', 'Pequeño', 1, 3);
