CREATE TABLE `bonus` (
  `player` varchar(255) NOT NULL,
  `obj_id` int(11) NOT NULL,
  `hwid` varchar(255) NOT NULL,
  `ip` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`player`,`obj_id`,`hwid`,`ip`)
);