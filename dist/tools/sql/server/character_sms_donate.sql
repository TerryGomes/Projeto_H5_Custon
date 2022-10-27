CREATE TABLE IF NOT EXISTS `character_sms_donate` (
	`id` int(11) NOT NULL DEFAULT '0',
	`service_id` int(11) NOT NULL DEFAULT '0',
	`status` int(11) NOT NULL DEFAULT '0',
	`time` int(10) unsigned NOT NULL DEFAULT '0',
	`curr_id` int(11) NOT NULL DEFAULT '0',
	`sum` decimal(9,2) DEFAULT NULL,
	`profit` decimal(9,2) DEFAULT NULL,
	`email` varchar(35) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`client_id` int(11) NOT NULL DEFAULT '0',
	`wNumber` int(11) NOT NULL DEFAULT '0',
	`wPhone` varchar(35) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`wText` varchar(35) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`wCost` decimal(15,6) DEFAULT NULL,
	`wProfit` decimal(9,2) DEFAULT NULL,
	`wCountry` varchar(35) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`has_reward` int(1) NOT NULL DEFAULT '0',
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;