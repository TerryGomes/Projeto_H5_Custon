CREATE TABLE IF NOT EXISTS `fishing_championship` (
  `PlayerName` varchar(35) CHARACTER SET utf8 NOT NULL,
  `fishLength` double(10,3) NOT NULL,
  `rewarded` int(1) NOT NULL
) ENGINE=MyISAM;