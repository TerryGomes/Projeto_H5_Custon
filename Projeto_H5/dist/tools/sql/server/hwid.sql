DROP TABLE IF EXISTS `hwid`;
CREATE TABLE `hwid` (
  `HWID` varchar(48) NOT NULL,
  `first_time_played` bigint(15) DEFAULT NULL,
  `total_time_played` bigint(11) DEFAULT NULL,
  `poll_answer` int(2) DEFAULT NULL,
  `threat` varchar(32) DEFAULT NULL,
  `warnings` int(2) DEFAULT NULL,
  `seenChangeLog` int(2) DEFAULT NULL,
  `banned` varchar(20) DEFAULT 'false',
  PRIMARY KEY (`HWID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
