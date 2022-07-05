CREATE TABLE IF NOT EXISTS `castle_manor_procure` (
	`castle_id` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`crop_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`can_buy` BIGINT NOT NULL DEFAULT '0',
	`start_buy` BIGINT NOT NULL DEFAULT '0',
	`price` BIGINT NOT NULL DEFAULT '0',
	`reward_type` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`period` INT NOT NULL DEFAULT '1',
	PRIMARY KEY  (`castle_id`,`crop_id`,`period`)
) ENGINE=MyISAM;
