/*
  SCRIPT DE PREPARACIÓN PARA PRUEBAS
  Ejecuta esto en la pestaña SQL de tu base de datos `fotocopiadora`
  en phpMyAdmin ANTES de empezar a probar la aplicación.
*/

-- 1. Colocar los roles en la tabla
INSERT INTO `roles` (`idRol`, `nombre_rol`) VALUES
(1, 'cliente'),
(2, 'admin');

-- 2. Crear el usuario Administrador
-- (Ya que el formulario de registro solo crea clientes)
-- IMPORTANTE: La contraseña es 'admin123' (en texto plano)
INSERT INTO `usuarios` (`idUsuario`, `idRol`, `email`, `password`, `nombre_completo`, `esta_activo`, `fecha_registro`) VALUES
(1, 2, 'admin@fotocopiadora.com', 'admin123', 'Administrador General', 1, current_timestamp());