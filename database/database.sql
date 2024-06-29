-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           10.4.10-MariaDB - mariadb.org binary distribution
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.7.0.6850
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Listage de la structure de la base pour poly_names
CREATE DATABASE IF NOT EXISTS `poly_names` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `poly_names`;

-- Listage de la structure de table poly_names. card
CREATE TABLE IF NOT EXISTS `card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_color` int(11) NOT NULL,
  `id_word` int(11) NOT NULL,
  `id_round` int(11) DEFAULT NULL,
  `id_game` int(11) NOT NULL,
  `order` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_card_color` (`id_color`),
  KEY `FK_card_word` (`id_word`),
  KEY `FK_card_round` (`id_round`),
  KEY `FK_card_game` (`id_game`),
  CONSTRAINT `FK_card_color` FOREIGN KEY (`id_color`) REFERENCES `color` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_card_game` FOREIGN KEY (`id_game`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_card_round` FOREIGN KEY (`id_round`) REFERENCES `round` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_card_word` FOREIGN KEY (`id_word`) REFERENCES `word` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3151 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.card : ~0 rows (environ)

-- Listage de la structure de table poly_names. color
CREATE TABLE IF NOT EXISTS `color` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `occurrence` tinyint(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.color : ~3 rows (environ)
INSERT INTO `color` (`id`, `name`, `occurrence`) VALUES
	(1, 'blue', 8),
	(2, 'grey', 15),
	(3, 'black', 2);

-- Listage de la structure de table poly_names. game
CREATE TABLE IF NOT EXISTS `game` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT uuid(),
  `status` enum('InProgress','Finished') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'InProgress',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.game : ~0 rows (environ)

-- Listage de la structure de table poly_names. player
CREATE TABLE IF NOT EXISTS `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_game` int(11) NOT NULL,
  `id_role` int(11) DEFAULT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `uid` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT uuid(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid` (`uid`),
  KEY `FK_player_game` (`id_game`),
  KEY `FK_player_role` (`id_role`),
  CONSTRAINT `FK_player_game` FOREIGN KEY (`id_game`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_player_role` FOREIGN KEY (`id_role`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.player : ~0 rows (environ)

-- Listage de la structure de table poly_names. role
CREATE TABLE IF NOT EXISTS `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `canSeeCardsColor` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.role : ~2 rows (environ)
INSERT INTO `role` (`id`, `name`, `canSeeCardsColor`) VALUES
	(1, 'Maître des mots', 1),
	(2, 'Maître de l\'intuition', 0);

-- Listage de la structure de table poly_names. round
CREATE TABLE IF NOT EXISTS `round` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_game` int(11) NOT NULL,
  `clue` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `nb_cards_to_find` int(11) NOT NULL DEFAULT 0,
  `score` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_round_game` (`id_game`),
  CONSTRAINT `FK_round_game` FOREIGN KEY (`id_game`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.round : ~0 rows (environ)

-- Listage de la structure de table poly_names. word
CREATE TABLE IF NOT EXISTS `word` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Listage des données de la table poly_names.word : ~50 rows (environ)
INSERT INTO `word` (`id`, `value`) VALUES
	(1, 'maison'),
	(2, 'chien'),
	(3, 'chat'),
	(4, 'arbre'),
	(5, 'voiture'),
	(6, 'fleur'),
	(7, 'ordinateur'),
	(8, 'table'),
	(9, 'chaise'),
	(10, 'livre'),
	(11, 'porte'),
	(12, 'fenêtre'),
	(13, 'ciel'),
	(14, 'soleil'),
	(15, 'lune'),
	(16, 'étoile'),
	(17, 'pluie'),
	(18, 'neige'),
	(19, 'montagne'),
	(20, 'rivière'),
	(21, 'mer'),
	(22, 'océan'),
	(23, 'forêt'),
	(24, 'jardin'),
	(25, 'parc'),
	(26, 'école'),
	(27, 'université'),
	(28, 'église'),
	(29, 'musée'),
	(30, 'théâtre'),
	(31, 'cinéma'),
	(32, 'restaurant'),
	(33, 'hôtel'),
	(34, 'supermarché'),
	(35, 'magasin'),
	(36, 'hôpital'),
	(37, 'clinique'),
	(38, 'pharmacie'),
	(39, 'gare'),
	(40, 'aéroport'),
	(41, 'station'),
	(42, 'bus'),
	(43, 'train'),
	(44, 'avion'),
	(45, 'bateau'),
	(46, 'vélo'),
	(47, 'moto'),
	(48, 'route'),
	(49, 'pont'),
	(50, 'tunnel');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
