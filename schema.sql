-- MySQL dump 10.16  Distrib 10.1.25-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: revgen
-- ------------------------------------------------------
-- Server version	10.1.25-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cases`
--

DROP TABLE IF EXISTS `cases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cases` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `simulation_id` int(11) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT NULL,
  `implant` tinyint(1) DEFAULT NULL,
  `ttr` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=214659408 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `graph_results`
--

DROP TABLE IF EXISTS `graph_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graph_results` (
  `simulation_id` int(11) NOT NULL,
  `sex_implant` int(11) DEFAULT NULL,
  `sex_ttr` int(11) DEFAULT NULL,
  `implant_ttr` int(11) DEFAULT NULL,
  PRIMARY KEY (`simulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parameter_sets`
--

DROP TABLE IF EXISTS `parameter_sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parameter_sets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `run_id` int(11) DEFAULT NULL,
  `theta_s` double DEFAULT NULL,
  `theta_i0` double DEFAULT NULL,
  `theta_i1` double DEFAULT NULL,
  `alpha00` double DEFAULT NULL,
  `alpha01` double DEFAULT NULL,
  `alpha10` double DEFAULT NULL,
  `alpha11` double DEFAULT NULL,
  `beta00` double DEFAULT NULL,
  `beta01` double DEFAULT NULL,
  `beta10` double DEFAULT NULL,
  `beta11` double DEFAULT NULL,
  `study_length` double DEFAULT NULL,
  `num_cases` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `parameters`
--

DROP TABLE IF EXISTS `parameters`;
/*!50001 DROP VIEW IF EXISTS `parameters`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `parameters` (
  `id` tinyint NOT NULL,
  `date` tinyint NOT NULL,
  `run_id` tinyint NOT NULL,
  `theta_s` tinyint NOT NULL,
  `theta_i0` tinyint NOT NULL,
  `theta_i1` tinyint NOT NULL,
  `alpha00` tinyint NOT NULL,
  `alpha01` tinyint NOT NULL,
  `alpha10` tinyint NOT NULL,
  `alpha11` tinyint NOT NULL,
  `beta00` tinyint NOT NULL,
  `beta01` tinyint NOT NULL,
  `beta10` tinyint NOT NULL,
  `beta11` tinyint NOT NULL,
  `study_length` tinyint NOT NULL,
  `count(cases.id)` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `runs`
--

DROP TABLE IF EXISTS `runs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `simulations`
--

DROP TABLE IF EXISTS `simulations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `simulations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `run_id` int(11) DEFAULT NULL,
  `parameter_set_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10297 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `parameters`
--

/*!50001 DROP TABLE IF EXISTS `parameters`*/;
/*!50001 DROP VIEW IF EXISTS `parameters`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `parameters` AS select `parameter_sets`.`id` AS `id`,`parameter_sets`.`date` AS `date`,`parameter_sets`.`run_id` AS `run_id`,`parameter_sets`.`theta_s` AS `theta_s`,`parameter_sets`.`theta_i0` AS `theta_i0`,`parameter_sets`.`theta_i1` AS `theta_i1`,`parameter_sets`.`alpha00` AS `alpha00`,`parameter_sets`.`alpha01` AS `alpha01`,`parameter_sets`.`alpha10` AS `alpha10`,`parameter_sets`.`alpha11` AS `alpha11`,`parameter_sets`.`beta00` AS `beta00`,`parameter_sets`.`beta01` AS `beta01`,`parameter_sets`.`beta10` AS `beta10`,`parameter_sets`.`beta11` AS `beta11`,`parameter_sets`.`study_length` AS `study_length`,count(`cases`.`id`) AS `count(cases.id)` from ((`parameter_sets` join `simulations` on((`simulations`.`parameter_set_id` = `parameter_sets`.`id`))) join `cases` on((`cases`.`simulation_id` = `simulations`.`id`))) group by `parameter_sets`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-08 10:42:46
