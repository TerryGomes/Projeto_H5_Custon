DROP TABLE IF EXISTS `academy_request`;
CREATE TABLE `academy_request` (
  `time` tinyint(2) unsigned NOT NULL DEFAULT '5',
  `clanId` int(11) NOT NULL DEFAULT '0',
  `seats` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `price` bigint(20) unsigned NOT NULL DEFAULT '0',
  `item` int(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`clanId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;