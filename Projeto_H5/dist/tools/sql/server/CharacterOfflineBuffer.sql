CREATE TABLE IF NOT EXISTS `character_offline_buffers` (
  `charId` int(10) unsigned NOT NULL,
  `price` bigint(10) unsigned NOT NULL DEFAULT '0',
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;