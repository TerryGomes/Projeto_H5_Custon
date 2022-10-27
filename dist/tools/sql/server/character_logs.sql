DROP TABLE IF EXISTS `character_logs`;
CREATE TABLE `character_logs` (
  `obj_Id` int(11) NOT NULL,
  `HWID` varchar(48) DEFAULT NULL,
  `action` text,
  `time` bigint(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;