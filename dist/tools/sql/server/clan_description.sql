CREATE TABLE IF NOT EXISTS `clan_description` (
	`clan_id` INT UNSIGNED NOT NULL,
	`description` text NOT NULL,
	PRIMARY KEY(`clan_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;