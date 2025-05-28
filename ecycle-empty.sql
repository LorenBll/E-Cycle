-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versione server:              10.4.32-MariaDB - mariadb.org binary distribution
-- S.O. server:                  Win64
-- HeidiSQL Versione:            12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dump della struttura del database ecycle
DROP DATABASE IF EXISTS `ecycle`;
CREATE DATABASE IF NOT EXISTS `ecycle` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `ecycle`;

-- Dump della struttura di tabella ecycle.brands
DROP TABLE IF EXISTS `brands`;
CREATE TABLE IF NOT EXISTS `brands` (
  `ID` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.categories
DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `ID` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.characteristics
DROP TABLE IF EXISTS `characteristics`;
CREATE TABLE IF NOT EXISTS `characteristics` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `main_colour` varchar(50) NOT NULL,
  `function` varchar(50) NOT NULL,
  `quality` varchar(50) DEFAULT NULL,
  `prod_year` int(11) DEFAULT NULL,
  `batch` varchar(50) DEFAULT NULL,
  `id_model` varchar(50) NOT NULL,
  `id_category` varchar(50) NOT NULL,
  `id_nature` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `id_model` (`id_model`),
  KEY `id_category` (`id_category`),
  KEY `id_nature` (`id_nature`),
  CONSTRAINT `FK_characteristics_categories` FOREIGN KEY (`id_category`) REFERENCES `categories` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `FK_characteristics_models` FOREIGN KEY (`id_model`) REFERENCES `models` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `FK_characteristics_natures` FOREIGN KEY (`id_nature`) REFERENCES `natures` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

TRUNCATE TABLE `characteristics`;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.interactions
DROP TABLE IF EXISTS `interactions`;
CREATE TABLE IF NOT EXISTS `interactions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `ts_creation` timestamp NOT NULL DEFAULT current_timestamp(),
  `isOffer` bit(1) NOT NULL,
  `id_user` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `FK_requests_users` FOREIGN KEY (`id_user`) REFERENCES `users` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

TRUNCATE TABLE `interactions`;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.models
DROP TABLE IF EXISTS `models`;
CREATE TABLE IF NOT EXISTS `models` (
  `ID` varchar(50) NOT NULL,
  `id_brand` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `id_brand` (`id_brand`),
  CONSTRAINT `FK_models_brands` FOREIGN KEY (`id_brand`) REFERENCES `brands` (`ID`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.natures
DROP TABLE IF EXISTS `natures`;
CREATE TABLE IF NOT EXISTS `natures` (
  `ID` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.negotiations
DROP TABLE IF EXISTS `negotiations`;
CREATE TABLE IF NOT EXISTS `negotiations` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ts_creation` timestamp NOT NULL DEFAULT current_timestamp(),
  `ts_closure` timestamp NULL DEFAULT NULL,
  `wasAccepted` bit(1) DEFAULT NULL,
  `id_sing_offer` int(11) NOT NULL,
  `id_sing_request` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `id_sing_request` (`id_sing_request`),
  KEY `id_sing_offer` (`id_sing_offer`),
  CONSTRAINT `FK_negotiations_sing_offer` FOREIGN KEY (`id_sing_offer`) REFERENCES `sing_offers` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_negotiations_sing_requests` FOREIGN KEY (`id_sing_request`) REFERENCES `sing_requests` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

TRUNCATE TABLE `negotiations`;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.sing_offers
DROP TABLE IF EXISTS `sing_offers`;
CREATE TABLE IF NOT EXISTS `sing_offers` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `price` float NOT NULL,
  `picture_path` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `expiration` date DEFAULT NULL,
  `ts_deletion` timestamp NULL DEFAULT NULL,
  `id_offer` int(11) NOT NULL,
  `id_characteristics` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `id_offer` (`id_offer`),
  KEY `id_characteristics` (`id_characteristics`),
  CONSTRAINT `FK_sing_offer_characteristics` FOREIGN KEY (`id_characteristics`) REFERENCES `characteristics` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_sing_offer_interactions` FOREIGN KEY (`id_offer`) REFERENCES `interactions` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

TRUNCATE TABLE `sing_offers`;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.sing_requests
DROP TABLE IF EXISTS `sing_requests`;
CREATE TABLE IF NOT EXISTS `sing_requests` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `max_price` float DEFAULT NULL,
  `ts_deletion` timestamp NULL DEFAULT NULL,
  `id_request` int(11) NOT NULL,
  `id_characteristics` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `id_request` (`id_request`),
  KEY `id_characteristics` (`id_characteristics`),
  CONSTRAINT `FK_sing_request_characteristics` FOREIGN KEY (`id_characteristics`) REFERENCES `characteristics` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_sing_request_requests` FOREIGN KEY (`id_request`) REFERENCES `interactions` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

TRUNCATE TABLE `sing_requests`;

-- L’esportazione dei dati non era selezionata.

-- Dump della struttura di tabella ecycle.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `surname` varchar(50) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(64) NOT NULL,
  `state` varchar(50) NOT NULL,
  `region` varchar(50) NOT NULL,
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `street` varchar(100) NOT NULL,
  `civic` varchar(10) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

TRUNCATE TABLE `users`;

-- L’esportazione dei dati non era selezionata.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
