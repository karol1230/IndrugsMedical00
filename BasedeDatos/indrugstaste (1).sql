-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 10-12-2025 a las 20:40:37
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
-- Base de datos: `indrugstaste`
--
CREATE DATABASE IF NOT EXISTS `indrugstaste` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `indrugstaste`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `control`
--

DROP TABLE IF EXISTS `control`;
CREATE TABLE `control` (
  `ID_CONTROL` bigint(20) NOT NULL,
  `FECHA_INICIO_TRATAMIENTO` datetime NOT NULL,
  `FECHA_FIN_TRATAMIENTO` datetime NOT NULL,
  `problema_salud` varchar(255) DEFAULT NULL,
  `cantidad_medic` varchar(255) DEFAULT NULL,
  `frecuencia_medic` varchar(255) DEFAULT NULL,
  `ALARMA_CONTROL` time NOT NULL,
  `ID_USUARIO` bigint(20) NOT NULL,
  `ID_MEDICAMENTOS` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--

DROP TABLE IF EXISTS `domicilio`;
CREATE TABLE `domicilio` (
  `ID_DOMICILIO` bigint(20) NOT NULL,
  `UBICACION_DOMICILIO` varchar(255) NOT NULL,
  `ESTADO_DOMICILIO` varchar(255) NOT NULL,
  `FECHA_ENTREGA_DOMICILIO` datetime NOT NULL,
  `ID_VEHICULO` bigint(20) DEFAULT NULL,
  `ID_ORDENES` bigint(20) NOT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `id_domiciliario` bigint(20) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `domicilio`
--
-------------------------------------------------

--
-- Estructura de tabla para la tabla `inventario`
--

DROP TABLE IF EXISTS `inventario`;
CREATE TABLE `inventario` (
  `ID_INVENTARIO` bigint(20) NOT NULL,
  `FECHA_ENTRADA_INVENTARIO` datetime NOT NULL,
  `FECHA_SALIDA_INVENTARIO` datetime NOT NULL,
  `ID_MEDICAMENTOS` bigint(20) NOT NULL,
  `STOCK_INVENTARIO` int(100) NOT NULL,
  `VENCIMIENTOMED_INVENTARIO` date NOT NULL,
  `ESTADOMED_INVENTARIO` varchar(255) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `inventario`
--

INSERT INTO `inventario` (`ID_INVENTARIO`, `FECHA_ENTRADA_INVENTARIO`, `FECHA_SALIDA_INVENTARIO`, `ID_MEDICAMENTOS`, `STOCK_INVENTARIO`, `VENCIMIENTOMED_INVENTARIO`, `ESTADOMED_INVENTARIO`) VALUES
(1, '2024-08-01 15:37:49', '2025-12-06 17:13:28', 1, 20, '2026-06-01', 'ACTIVO'),
(2, '2024-09-02 08:39:31', '2025-12-06 17:10:54', 2, 62, '2026-06-01', 'ACTIVO'),
(3, '2024-08-06 12:40:00', '2025-12-08 19:20:03', 3, 77, '2026-06-01', 'ACTIVO'),
(4, '2024-04-02 16:43:05', '2024-11-08 15:42:51', 4, 45, '2026-07-13', 'ACTIVO'),
(5, '2024-06-13 14:47:19', '2024-11-07 17:46:07', 5, 63, '2026-07-13', 'ACTIVO'),
(6, '2024-07-09 07:47:29', '2024-10-31 13:46:19', 6, 98, '2026-07-14', 'ACTIVO'),
(7, '2024-01-24 11:47:43', '2024-11-29 12:46:29', 7, 102, '2026-07-21', 'ACTIVO'),
(8, '2024-03-13 09:48:00', '2024-10-29 10:46:36', 8, 56, '2026-12-24', 'ACTIVO'),
(9, '2024-08-13 13:48:13', '2025-11-18 18:20:23', 9, 40, '2026-01-31', 'ACTIVO'),
(10, '2024-04-17 14:48:22', '2024-11-08 09:47:04', 10, 74, '2026-11-27', 'ACTIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamentos`
--

DROP TABLE IF EXISTS `medicamentos`;
CREATE TABLE `medicamentos` (
  `ID_MEDICAMENTOS` bigint(20) NOT NULL,
  `nombre_medicamentos` varchar(255) DEFAULT NULL,
  `descripcion_medicamentos` varchar(255) DEFAULT NULL,
  `IMAGEN_MEDICAMENTO` varchar(255) NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_entrada` datetime(6) DEFAULT NULL,
  `fecha_salida` datetime(6) DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `fecha_vencimiento` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `medicamentos`
--

INSERT INTO `medicamentos` (`ID_MEDICAMENTOS`, `nombre_medicamentos`, `descripcion_medicamentos`, `IMAGEN_MEDICAMENTO`, `estado`, `fecha_entrada`, `fecha_salida`, `stock`, `fecha_vencimiento`) VALUES
(0, '', '', '', NULL, NULL, NULL, NULL, NULL),
(1, 'Aspirina', 'Analgésico', 'aspirina.jpg', NULL, NULL, NULL, NULL, NULL),
(2, 'Ibuprofeno', 'Antiinflamatorio', 'ibuprofeno.png ', NULL, NULL, NULL, NULL, NULL),
(3, 'Paracetamol', 'Antipirético', 'paracetamol.png', NULL, NULL, NULL, NULL, NULL),
(4, 'Omeprazol', 'Antiácido', 'omeprazol.png', NULL, NULL, NULL, NULL, NULL),
(5, 'Azitromicina', 'Antibiótico', 'azitromicina.jpeg ', NULL, NULL, NULL, NULL, NULL),
(6, 'Ciprofloxina', 'Antibiótico', 'ciprofloxina.jpeg ', NULL, NULL, NULL, NULL, NULL),
(7, 'Metformina', 'Antidiabético', 'metformina.jpeg ', NULL, NULL, NULL, NULL, NULL),
(8, 'Atrovastatina', 'Hipolipemiante', 'atrovastanina.jpeg', NULL, NULL, NULL, NULL, NULL),
(9, 'Fexofedina', 'Antialérgico', 'fexofedina.jpeg', NULL, NULL, NULL, NULL, NULL),
(10, 'Dexametasona', 'Corticoide', 'dexametasona.jpeg ', NULL, NULL, NULL, NULL, NULL),
(14, '', NULL, '', NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ordenes`
--

DROP TABLE IF EXISTS `ordenes`;
CREATE TABLE `ordenes` (
  `ID_ORDENES` bigint(20) NOT NULL,
  `FECHA_ENTREGA` datetime NOT NULL,
  `EPS_ORDEN` varchar(255) NOT NULL,
  `ESTADO_ORDEN` varchar(255) NOT NULL DEFAULT 'ACTIVO',
  `USUARIOS_PACIENTE` bigint(20) NOT NULL,
  `CANTIDADMED_ORDEN` int(11) NOT NULL,
  `DIRECCION_ORDEN` varchar(255) NOT NULL,
  `telefono_orden` varchar(255) DEFAULT NULL,
  `FOTO_FORMULA` varchar(255) DEFAULT NULL,
  `formula_medica` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--

--
-- Estructura de tabla para la tabla `ordenes_has_medicamentos`
--

DROP TABLE IF EXISTS `ordenes_has_medicamentos`;
CREATE TABLE `ordenes_has_medicamentos` (
  `ID_MEDICAMENTO_POR_ORDEN` bigint(20) NOT NULL,
  `ID_ORDENES` bigint(20) NOT NULL,
  `ID_MEDICAMENTOS` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

DROP TABLE IF EXISTS `pedido`;
CREATE TABLE `pedido` (
  `id` bigint(20) NOT NULL,
  `nombre_paciente` varchar(255) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `hora_pedido` datetime DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `usuario_domiciliario` bigint(20) NOT NULL,
  `orden_id` bigint(20) DEFAULT NULL,
  `correo_paciente` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedido`
--

--
-- Estructura de tabla para la tabla `pqrs`
--

DROP TABLE IF EXISTS `pqrs`;
CREATE TABLE `pqrs` (
  `ID_PQRS` bigint(20) NOT NULL,
  `TIPO_SOLICITUD` varchar(255) NOT NULL,
  `motivo_pqrs` varchar(255) DEFAULT NULL,
  `ID_USUARIOS` bigint(20) NOT NULL,
  `FECHA_PQRS` datetime NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_respuesta` datetime(6) DEFAULT NULL,
  `respuesta` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pqrs`
--

--
-- Estructura de tabla para la tabla `privilegios`
--

DROP TABLE IF EXISTS `privilegios`;
CREATE TABLE `privilegios` (
  `ID_PRIVILEGIOS` bigint(20) NOT NULL,
  `DESCRIPCION_PRIVILEGIOS` enum('SELECT','INSERT','UPDATE','DELETE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `privilegios`
--

INSERT INTO `privilegios` (`ID_PRIVILEGIOS`, `DESCRIPCION_PRIVILEGIOS`) VALUES
(1, 'SELECT'),
(2, 'UPDATE'),
(3, 'INSERT');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `ID_ROLES` bigint(20) NOT NULL,
  `nombre_rol` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`ID_ROLES`, `nombre_rol`) VALUES
(1, 'Administrador'),
(2, 'Paciente'),
(3, 'Domiciliario');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `ID_USUARIOS` bigint(20) NOT NULL,
  `NOMBRE_USUARIOS` varchar(255) NOT NULL,
  `TIPODOC_USUARIOS` varchar(255) NOT NULL,
  `NUMDOC_USUARIOS` varchar(255) NOT NULL,
  `DIRECCION_USUARIOS` varchar(255) NOT NULL,
  `ESTADO_USUARIO` varchar(255) DEFAULT 'ACTIVO',
  `TELEFONO_USUARIOS` varchar(255) NOT NULL,
  `CORREO_USUARIOS` varchar(255) NOT NULL,
  `contraseña_usuarios` varchar(255) DEFAULT NULL,
  `ID_ROLES_USUARIOS` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--
INSERT INTO `usuarios` (`ID_USUARIOS`, `NOMBRE_USUARIOS`, `TIPODOC_USUARIOS`, `NUMDOC_USUARIOS`, `DIRECCION_USUARIOS`, `ESTADO_USUARIO`, `TELEFONO_USUARIOS`, `CORREO_USUARIOS`, `CONTRASEÑA_USUARIOS`, `ID_ROLES_USUARIOS`) VALUES
(1, 'Karol diley', 'CC', '1109414123', 'Calle 142 #102 b44', 'ACTIVO', '321-796-0181', 'karoldiley@gmail.com', '1109414123', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios_has_privilegios`
--

DROP TABLE IF EXISTS `usuarios_has_privilegios`;
CREATE TABLE `usuarios_has_privilegios` (
  `ID_PRIVILEGIO_USUARIO` bigint(20) NOT NULL,
  `ID_USUARIOS` bigint(20) NOT NULL,
  `ID_PRIVILEGIOS` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `usuarios_has_privilegios`
--

INSERT INTO `usuarios_has_privilegios` (`ID_PRIVILEGIO_USUARIO`, `ID_USUARIOS`, `ID_PRIVILEGIOS`) VALUES
(1, 1, 3);


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculo`
--

DROP TABLE IF EXISTS `vehiculo`;
CREATE TABLE `vehiculo` (
  `ID_VEHICULO` bigint(20) NOT NULL,
  `color_vehiculo` varchar(255) DEFAULT NULL,
  `marca_vehiculo` varchar(255) DEFAULT NULL,
  `placa_vehiculo` varchar(255) DEFAULT NULL,
  `TIPO_VEHICULO` varchar(255) NOT NULL,
  `ESTADO_VEHICULO` varchar(255) NOT NULL DEFAULT 'ACTIVO',
  `PROPIETARIO_USUARIOS` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `vehiculo`
--


--
-- Indices de la tabla `control`
--
ALTER TABLE `control`
  ADD PRIMARY KEY (`ID_CONTROL`),
  ADD KEY `ID_USUARIO` (`ID_USUARIO`),
  ADD KEY `ID_MEDICAMENTOS` (`ID_MEDICAMENTOS`);

--
-- Indices de la tabla `domicilio`
--
ALTER TABLE `domicilio`
  ADD PRIMARY KEY (`ID_DOMICILIO`),
  ADD KEY `ORDENES_ID_ORDENES` (`ID_ORDENES`),
  ADD KEY `VEHÍCULO_ID_VEHÍCULO` (`ID_VEHICULO`),
  ADD KEY `FKn8cmq013i9y9duut0h746kttg` (`id_domiciliario`),
  ADD KEY `FKiendccsqge7emv7w2wbw5nyaj` (`usuario_id`);

--
-- Indices de la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD PRIMARY KEY (`ID_INVENTARIO`),
  ADD KEY `MEDICAMENTOS_ID_MEDICAMENTOS` (`ID_MEDICAMENTOS`);

--
-- Indices de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD PRIMARY KEY (`ID_MEDICAMENTOS`);

--
-- Indices de la tabla `ordenes`
--
ALTER TABLE `ordenes`
  ADD PRIMARY KEY (`ID_ORDENES`),
  ADD KEY `USUARIOS_ID_USUARIOS_PACIENTE` (`USUARIOS_PACIENTE`);

--
-- Indices de la tabla `ordenes_has_medicamentos`
--
ALTER TABLE `ordenes_has_medicamentos`
  ADD PRIMARY KEY (`ID_MEDICAMENTO_POR_ORDEN`),
  ADD KEY `ORDENES_ID_ORDENES` (`ID_ORDENES`),
  ADD KEY `MEDICAMENTOS_ID_MEDICAMENTOS` (`ID_MEDICAMENTOS`);

--
-- Indices de la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_pedido_domiciliario` (`usuario_domiciliario`),
  ADD KEY `fk_pedido_orden` (`orden_id`);

--
-- Indices de la tabla `pqrs`
--
ALTER TABLE `pqrs`
  ADD PRIMARY KEY (`ID_PQRS`),
  ADD KEY `ID_USUARIOS` (`ID_USUARIOS`);

--
-- Indices de la tabla `privilegios`
--
ALTER TABLE `privilegios`
  ADD PRIMARY KEY (`ID_PRIVILEGIOS`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`ID_ROLES`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`ID_USUARIOS`),
  ADD KEY `ROLES_ID_ROLES` (`ID_ROLES_USUARIOS`);

--
-- Indices de la tabla `usuarios_has_privilegios`
--
ALTER TABLE `usuarios_has_privilegios`
  ADD PRIMARY KEY (`ID_PRIVILEGIO_USUARIO`),
  ADD KEY `USUARIOS_ID_USUARIOS` (`ID_USUARIOS`),
  ADD KEY `PRIVILEGIOS_ID_PRIVILEGIOS` (`ID_PRIVILEGIOS`);

--
-- Indices de la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  ADD PRIMARY KEY (`ID_VEHICULO`),
  ADD KEY `USUARIOS_ID_USUARIOS` (`PROPIETARIO_USUARIOS`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `control`
--
ALTER TABLE `control`
  MODIFY `ID_CONTROL` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `domicilio`
--
ALTER TABLE `domicilio`
  MODIFY `ID_DOMICILIO` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT de la tabla `inventario`
--
ALTER TABLE `inventario`
  MODIFY `ID_INVENTARIO` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `ID_MEDICAMENTOS` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `ordenes`
--
ALTER TABLE `ordenes`
  MODIFY `ID_ORDENES` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT de la tabla `ordenes_has_medicamentos`
--
ALTER TABLE `ordenes_has_medicamentos`
  MODIFY `ID_MEDICAMENTO_POR_ORDEN` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `pqrs`
--
ALTER TABLE `pqrs`
  MODIFY `ID_PQRS` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `privilegios`
--
ALTER TABLE `privilegios`
  MODIFY `ID_PRIVILEGIOS` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `ID_ROLES` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `ID_USUARIOS` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT de la tabla `usuarios_has_privilegios`
--
ALTER TABLE `usuarios_has_privilegios`
  MODIFY `ID_PRIVILEGIO_USUARIO` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  MODIFY `ID_VEHICULO` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `control`
--
ALTER TABLE `control`
  ADD CONSTRAINT `ID_MEDICAMENTOS` FOREIGN KEY (`ID_MEDICAMENTOS`) REFERENCES `medicamentos` (`ID_MEDICAMENTOS`),
  ADD CONSTRAINT `ID_USUARIO` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuarios` (`ID_USUARIOS`);

--
-- Filtros para la tabla `domicilio`
--
ALTER TABLE `domicilio`
  ADD CONSTRAINT `FKiendccsqge7emv7w2wbw5nyaj` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`ID_USUARIOS`),
  ADD CONSTRAINT `FKn8cmq013i9y9duut0h746kttg` FOREIGN KEY (`id_domiciliario`) REFERENCES `usuarios` (`ID_USUARIOS`),
  ADD CONSTRAINT `domicilio_ibfk_1` FOREIGN KEY (`ID_ORDENES`) REFERENCES `ordenes` (`ID_ORDENES`),
  ADD CONSTRAINT `domicilio_ibfk_2` FOREIGN KEY (`ID_VEHICULO`) REFERENCES `vehiculo` (`ID_VEHICULO`);

--
-- Filtros para la tabla `inventario`
--
ALTER TABLE `inventario`
  ADD CONSTRAINT `inventario_ibfk_1` FOREIGN KEY (`ID_MEDICAMENTOS`) REFERENCES `medicamentos` (`ID_MEDICAMENTOS`);

--
-- Filtros para la tabla `ordenes`
--
ALTER TABLE `ordenes`
  ADD CONSTRAINT `ordenes_ibfk_1` FOREIGN KEY (`USUARIOS_PACIENTE`) REFERENCES `usuarios` (`ID_USUARIOS`);

--
-- Filtros para la tabla `ordenes_has_medicamentos`
--
ALTER TABLE `ordenes_has_medicamentos`
  ADD CONSTRAINT `ordenes_has_medicamentos_ibfk_1` FOREIGN KEY (`ID_ORDENES`) REFERENCES `ordenes` (`ID_ORDENES`),
  ADD CONSTRAINT `ordenes_has_medicamentos_ibfk_2` FOREIGN KEY (`ID_MEDICAMENTOS`) REFERENCES `medicamentos` (`ID_MEDICAMENTOS`);

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `fk_pedido_domiciliario` FOREIGN KEY (`usuario_domiciliario`) REFERENCES `usuarios` (`ID_USUARIOS`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pedido_orden` FOREIGN KEY (`orden_id`) REFERENCES `ordenes` (`ID_ORDENES`) ON DELETE CASCADE;

--
-- Filtros para la tabla `pqrs`
--
ALTER TABLE `pqrs`
  ADD CONSTRAINT `ID_USUARIOS` FOREIGN KEY (`ID_USUARIOS`) REFERENCES `usuarios` (`ID_USUARIOS`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`ID_ROLES_USUARIOS`) REFERENCES `roles` (`ID_ROLES`);

--
-- Filtros para la tabla `usuarios_has_privilegios`
--
ALTER TABLE `usuarios_has_privilegios`
  ADD CONSTRAINT `usuarios_has_privilegios_ibfk_1` FOREIGN KEY (`ID_USUARIOS`) REFERENCES `usuarios` (`ID_USUARIOS`),
  ADD CONSTRAINT `usuarios_has_privilegios_ibfk_2` FOREIGN KEY (`ID_PRIVILEGIOS`) REFERENCES `privilegios` (`ID_PRIVILEGIOS`);

--
-- Filtros para la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  ADD CONSTRAINT `vehiculo_ibfk_1` FOREIGN KEY (`PROPIETARIO_USUARIOS`) REFERENCES `usuarios` (`ID_USUARIOS`);
COMMIT;

ALTER TABLE control ADD COLUMN ultimo_envio DATETIME NULL;
ALTER TABLE control ADD COLUMN proximo_envio DATETIME NULL;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
