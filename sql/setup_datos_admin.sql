/*
  SCRIPT DE DATOS DE PRUEBA
  Ejecuta esto para poblar tu sistema y probar el panel de Admin.
*/

-- 1. Asegurarnos de tener roles
INSERT IGNORE INTO roles (idRol, nombre_rol) VALUES (1, 'cliente'), (2, 'admin');

-- 2. Crear usuarios variados
-- Passwords son '123456' (en texto plano para este proyecto)
INSERT INTO usuarios (email, password, nombre_completo, idRol, esta_activo, fecha_registro) VALUES
('cliente_activo@test.com', '123456', 'Cliente Activo', 1, 1, NOW()),
('cliente_inactivo@test.com', '123456', 'Cliente Bloqueado', 1, 0, NOW()),
('admin_secundario@test.com', '123456', 'Admin Secundario', 2, 1, NOW());

-- 3. Crear trabajos de prueba (asociados al Cliente Activo)
-- NOTA: Necesitamos el ID del cliente activo. Asumimos que es el último insertado o lo buscamos.
-- Para este script simple, usaremos una subconsulta para obtener el ID.

SET @idCliente = (SELECT idUsuario FROM usuarios WHERE email = 'cliente_activo@test.com' LIMIT 1);

INSERT INTO trabajos (idCliente, ruta_archivo, nombre_archivo_original, num_copias, calidad, faz, estado, fecha_solicitud, fecha_retiro_solicitada) VALUES
(@idCliente, 'C:/fake/ruta/archivo1.pdf', 'Tesis_Final.pdf', 2, 'blanco_y_negro', 'doble', 'pendiente', NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY)),
(@idCliente, 'C:/fake/ruta/foto.jpg', 'Foto_Carnet.jpg', 1, 'color', 'simple', 'terminado', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(@idCliente, 'C:/fake/ruta/apuntes.pdf', 'Apuntes_Matematica.pdf', 1, 'blanco_y_negro', 'simple', 'retirado', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));