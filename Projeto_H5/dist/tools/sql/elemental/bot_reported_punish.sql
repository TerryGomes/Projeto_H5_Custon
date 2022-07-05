CREATE TABLE IF NOT EXISTS `bot_reported_punish` (
  `charId` int(11) NOT NULL DEFAULT '0',
  `punish_type` varchar(45) DEFAULT NULL,
  `time_left` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;