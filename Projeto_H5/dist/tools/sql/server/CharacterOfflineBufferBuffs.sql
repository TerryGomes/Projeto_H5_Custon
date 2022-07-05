CREATE TABLE IF NOT EXISTS `character_offline_buffer_buffs` (
  `charId` int(10) unsigned NOT NULL,
  `skillIds` TEXT NOT NULL,
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;