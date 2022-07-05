CREATE TABLE IF NOT EXISTS `character_blocklist` (
	`obj_Id` INT NOT NULL,
	`target_Id` INT NOT NULL,
	PRIMARY KEY  (`obj_Id`,`target_Id`)
) ENGINE=MyISAM;