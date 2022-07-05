CREATE TABLE IF NOT EXISTS `logs` (
  `log_id` int(11) NOT NULL,
  `player_object_id` int(11) DEFAULT NULL,
  `action_type` varchar(40) DEFAULT NULL,
  `time` bigint(14) DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;