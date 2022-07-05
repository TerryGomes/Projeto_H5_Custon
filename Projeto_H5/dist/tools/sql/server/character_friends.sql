CREATE TABLE IF NOT EXISTS `character_friends` (
	`char_id` INT NOT NULL DEFAULT '0',
	`friend_id` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_id`,`friend_id`)
) ENGINE=MyISAM;