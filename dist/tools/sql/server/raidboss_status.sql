CREATE TABLE IF NOT EXISTS `raidboss_status` (
	`id` INT NOT NULL,
	`current_hp` INT DEFAULT NULL,
	`current_mp` INT DEFAULT NULL,
	`respawn_delay` INT NOT NULL DEFAULT '0',
	`date_of_death` INT NOT NULL DEFAULT '0',
	`last_killer` text,
	PRIMARY KEY  (`id`)
) ENGINE=MyISAM;