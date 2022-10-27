CREATE TABLE IF NOT EXISTS `seven_signs_festival` (
	`festivalId` TINYINT(4) NOT NULL DEFAULT '0',
	`cabal` VARCHAR(4) NOT NULL,
	`cycle` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`date` BIGINT(20) NULL DEFAULT '0',
	`score` MEDIUMINT(8) UNSIGNED NOT NULL DEFAULT '0',
	`members` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
	`names` TINYTEXT CHARACTER SET UTF8 NOT NULL,
	PRIMARY KEY (`festivalId`, `cabal`, `cycle`)
) ENGINE=MyISAM;

REPLACE INTO `seven_signs_festival` VALUES
(0,"dawn",1,0,0,"",""),
(1,"dawn",1,0,0,"",""),
(2,"dawn",1,0,0,"",""),
(3,"dawn",1,0,0,"",""),
(4,"dawn",1,0,0,"",""),
(0,"dusk",1,0,0,"",""),
(1,"dusk",1,0,0,"",""),
(2,"dusk",1,0,0,"",""),
(3,"dusk",1,0,0,"",""),
(4,"dusk",1,0,0,"","");