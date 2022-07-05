CREATE TABLE IF NOT EXISTS `pets` (
	`item_obj_id` INT NOT NULL DEFAULT '0',
	`objId` int,
	`name` VARCHAR(15) CHARACTER SET UTF8 DEFAULT NULL,
	`level` TINYINT UNSIGNED,
	`curHp` mediumint UNSIGNED,
	`curMp` mediumint UNSIGNED,
	`exp` bigint,
	`sp` INT UNSIGNED,
	`fed` SMALLINT UNSIGNED,
	`max_fed` SMALLINT UNSIGNED,
	PRIMARY KEY (item_obj_id)
) ENGINE=MyISAM;