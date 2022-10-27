CREATE TABLE IF NOT EXISTS `academicians` (
	`objId` int(11) NOT NULL,
	`clanId` int(11) NOT NULL,
	`end_time` bigint(20) unsigned NOT NULL,
	PRIMARY KEY (`objId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;