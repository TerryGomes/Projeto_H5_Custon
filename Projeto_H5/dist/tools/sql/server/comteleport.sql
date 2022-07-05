CREATE TABLE `comteleport` (
  `TpId` int(11) NOT NULL AUTO_INCREMENT,
  `charId` int(11) DEFAULT NULL,
  `Xpos` int(9) NOT NULL default '0', 
  `Ypos` int(9) NOT NULL default '0', 
  `Zpos` int(9) NOT NULL default '0', 
  `name` varchar(250) NOT NULL default '', 
  PRIMARY KEY (`TpId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;