-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 08-11-2025 a las 22:13:37
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `fotocopiadora`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `idRol` int(11) NOT NULL COMMENT 'Identificador numérico único para el rol.',
  `nombre_rol` varchar(50) NOT NULL COMMENT 'Nombre descriptivo del rol.'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `trabajos`
--

CREATE TABLE `trabajos` (
  `idTrabajo` int(11) NOT NULL COMMENT 'Identificador para el trabajo/pedido.',
  `idCliente` int(11) NOT NULL COMMENT 'Clave foránea que indica el usuario solicitó el trabajo.',
  `ruta_archivo` varchar(255) NOT NULL COMMENT 'Ubicación del directorio donde se guarda el archivo a imprimir.',
  `nombre_archivo_original` varchar(255) NOT NULL COMMENT '	Nombre original del archivo que subió el cliente.',
  `num_copias` int(11) NOT NULL COMMENT 'Cantidad de copias que se van a imprimir.',
  `calidad` enum('blanco_y_negro','color') NOT NULL COMMENT 'Calidad de la impresión. Valores: ''blanco_y_negro'', ''color''.',
  `faz` enum('simple','doble') NOT NULL COMMENT '	Tipo de faz de la impresión. Valores: ''simple'', ''doble''.',
  `estado` enum('pendiente','terminado','retirado') NOT NULL DEFAULT 'pendiente' COMMENT 'Estado en el que se encuentra el trabajo dentro del proceso de impresión.  Valores: ''pendiente'', ''terminado'', ''retirado''.',
  `fecha_solicitud` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Fecha/hora de creación del pedido.',
  `fecha_retiro_solicitada` datetime NOT NULL COMMENT '	Fecha en la que el usuario pidió que quede preparado el trabajo para ir a retirarlo.',
  `fecha_impresion` datetime DEFAULT NULL COMMENT 'Fecha/hora en que el admin marca el trabajo como ''terminado''.',
  `fecha_entrega` datetime DEFAULT NULL COMMENT 'Fecha/hora en que el admin marca el trabajo como ''retirado''.'
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `idUsuario` int(11) NOT NULL COMMENT 'Identificador para cada cuenta de usuario.',
  `idRol` int(11) NOT NULL COMMENT '	Clave foránea que vincula al usuario con su rol.',
  `email` varchar(255) NOT NULL COMMENT 'Correo electrónico del usuario, usado para el login.',
  `password_hash` varchar(255) NOT NULL COMMENT 'Contraseña cifrada del usuario usada para iniciar sesión.',
  `nombre_completo` varchar(100) DEFAULT NULL COMMENT '	Nombre(s) y apellido(s) del usuario.',
  `esta_activo` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Controla si la cuenta puede iniciar sesión (1 = sí, 0 = no).',
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '	Fecha/hora de creación del usuario.'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`idRol`),
  ADD UNIQUE KEY `nombre_rol` (`nombre_rol`);

--
-- Indices de la tabla `trabajos`
--
ALTER TABLE `trabajos`
  ADD PRIMARY KEY (`idTrabajo`),
  ADD KEY `cliente_id` (`idCliente`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`idUsuario`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `rol_id` (`idRol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `idRol` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador numérico único para el rol.';

--
-- AUTO_INCREMENT de la tabla `trabajos`
--
ALTER TABLE `trabajos`
  MODIFY `idTrabajo` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador para el trabajo/pedido.';

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `idUsuario` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador para cada cuenta de usuario.';

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `trabajos`
--
ALTER TABLE `trabajos`
  ADD CONSTRAINT `trabajos_ibfk_1` FOREIGN KEY (`idCliente`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`idRol`) REFERENCES `roles` (`idRol`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
