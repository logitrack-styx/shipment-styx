-- MySQL dump 10.13  Distrib 8.4.11, for Win64 (x86_64)
--
-- Host: localhost    Database: logitrack
-- ------------------------------------------------------
-- Server version	8.4.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `logitrack`
--

-- OMITIDO EN RESTAURACION DE PRUEBA: CREATE DATABASE /*!32312 IF NOT EXISTS*/ `logitrack` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

-- OMITIDO EN RESTAURACION DE PRUEBA: USE `logitrack`;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` binary(16) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) NOT NULL,
  `email` varchar(254) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1c96wv36rk2hwui7qhjks3mvg` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (_binary 'Æ4\nƒQéOF¼aƒJ\ß\Ó19','2026-10-21 01:29:53.599460','2026-10-21 01:29:53.599460','juan.perez@example.com','Juan P├®rez','+5493884000000');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `envios`
--

DROP TABLE IF EXISTS `envios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `envios` (
  `id` binary(16) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) NOT NULL,
  `alto` double NOT NULL,
  `ancho` double NOT NULL,
  `codigo_seguimiento` binary(16) NOT NULL,
  `destino_calle` varchar(255) DEFAULT NULL,
  `destino_ciudad` varchar(255) DEFAULT NULL,
  `destino_codigo_postal` varchar(255) DEFAULT NULL,
  `destino_numero` varchar(255) DEFAULT NULL,
  `estado` enum('CANCELADO','ENTREGADO','EN_RUTA','PENDIENTE') NOT NULL,
  `largo` double NOT NULL,
  `origen_calle` varchar(255) DEFAULT NULL,
  `origen_ciudad` varchar(255) DEFAULT NULL,
  `origen_codigo_postal` varchar(255) DEFAULT NULL,
  `origen_numero` varchar(255) DEFAULT NULL,
  `peso` double NOT NULL,
  `cliente_id` binary(16) NOT NULL,
  `repartidor_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKoc583u520tb2dsd41ym690nky` (`codigo_seguimiento`),
  KEY `FKc8kj7vm4uudhuwdu29ffo2fqg` (`cliente_id`),
  KEY `FKb5i2sginj39k7q7mdyw3vsp9j` (`repartidor_id`),
  CONSTRAINT `FKb5i2sginj39k7q7mdyw3vsp9j` FOREIGN KEY (`repartidor_id`) REFERENCES `repartidores` (`id`),
  CONSTRAINT `FKc8kj7vm4uudhuwdu29ffo2fqg` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `envios`
--

LOCK TABLES `envios` WRITE;
/*!40000 ALTER TABLE `envios` DISABLE KEYS */;
INSERT INTO `envios` VALUES (_binary 'b0Y\÷K┌Åéb\¦║Ñ','2026-10-21 01:32:44.730278','2026-10-21 02:06:21.931396',15,20,_binary 'AÜ iÇ@8©┬ÖƒÕíè┴','Alvear','San Salvador de Jujuy','4600','456','CANCELADO',30,'Belgrano','San Salvador de Jujuy','4600','123',2.5,_binary 'Æ4\nƒQéOF¼aƒJ\ß\Ó19',NULL),(_binary '\n}-[ÖL\næ\'\‗e\┘\╚n7','2026-10-21 01:30:43.567389','2026-10-21 02:05:37.468250',15,20,_binary 'ø\Ô)ó\ßáA\Þ▓\Þå\─	\ÓN','Alvear','San Salvador de Jujuy','4600','456','ENTREGADO',30,'Belgrano','San Salvador de Jujuy','4600','123',2.5,_binary 'Æ4\nƒQéOF¼aƒJ\ß\Ó19',_binary 'FÔÿÀBTGñÄ\ý6¿\0¨');
/*!40000 ALTER TABLE `envios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_estados`
--

DROP TABLE IF EXISTS `historial_estados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_estados` (
  `id` binary(16) NOT NULL,
  `estado_anterior` enum('CANCELADO','ENTREGADO','EN_RUTA','PENDIENTE') DEFAULT NULL,
  `estado_nuevo` enum('CANCELADO','ENTREGADO','EN_RUTA','PENDIENTE') NOT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  `envio` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgjc1665e2dlu2kut0c4jq877x` (`envio`),
  CONSTRAINT `FKgjc1665e2dlu2kut0c4jq877x` FOREIGN KEY (`envio`) REFERENCES `envios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_estados`
--

LOCK TABLES `historial_estados` WRITE;
/*!40000 ALTER TABLE `historial_estados` DISABLE KEYS */;
INSERT INTO `historial_estados` VALUES (_binary '\"u@çWêDÌôj«╦Øå¥',NULL,'PENDIENTE','2026-10-20 22:32:44.731276',_binary 'b0Y\÷K┌Åéb\¦║Ñ'),(_binary 'o$P\Ú\ÎM█Á~;ª8i`T','EN_RUTA','ENTREGADO','2026-10-20 23:05:37.463177',_binary '\n}-[ÖL\næ\'\‗e\┘\╚n7'),(_binary 'º\Û\'¡»\þBÿdóÐä^ò·','PENDIENTE','CANCELADO','2026-10-20 23:06:21.930397',_binary 'b0Y\÷K┌Åéb\¦║Ñ'),(_binary '╗Öû=ÀqNGóM\╬\¤v\þ<','PENDIENTE','EN_RUTA','2026-10-20 23:05:07.344294',_binary '\n}-[ÖL\næ\'\‗e\┘\╚n7'),(_binary '¹\÷╩╣l4I<¥ækEW:\¸\õ',NULL,'PENDIENTE','2026-10-20 22:30:43.567389',_binary '\n}-[ÖL\næ\'\‗e\┘\╚n7');
/*!40000 ALTER TABLE `historial_estados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repartidores`
--

DROP TABLE IF EXISTS `repartidores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repartidores` (
  `id` binary(16) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) NOT NULL,
  `estado_repartidor` enum('ACTIVO','INACTIVO') NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo_vehiculo` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repartidores`
--

LOCK TABLES `repartidores` WRITE;
/*!40000 ALTER TABLE `repartidores` DISABLE KEYS */;
INSERT INTO `repartidores` VALUES (_binary 'FÔÿÀBTGñÄ\ý6¿\0¨','2026-10-21 01:30:11.489762','2026-10-21 01:30:11.489762','ACTIVO','Carlos G├│mez','Motocicleta');
/*!40000 ALTER TABLE `repartidores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'logitrack'
--

--
-- Dumping routines for database 'logitrack'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-25 13:10:51
