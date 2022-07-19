DROP TABLE IF EXISTS `add_spawnlist`;
CREATE TABLE `add_spawnlist` (
  `location` varchar(35) NOT NULL DEFAULT '',
  `count` int(11) NOT NULL DEFAULT '0',
  `npc_templateid` int(11) NOT NULL DEFAULT '0',
  `locx` int(11) NOT NULL DEFAULT '0',
  `locy` int(11) NOT NULL DEFAULT '0',
  `locz` int(11) NOT NULL DEFAULT '0',
  `heading` int(11) NOT NULL DEFAULT '0',
  `respawn_delay` int(11) NOT NULL DEFAULT '0',
  `respawn_delay_rnd` int(11) NOT NULL DEFAULT '0',
  `loc_id` int(11) NOT NULL DEFAULT '0',
  `periodOfDay` tinyint(1) NOT NULL DEFAULT '0',
  `reflection` smallint(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_templateid`,`locx`,`locy`,`locz`,`loc_id`,`reflection`),
  KEY `key_npc_templateid` (`npc_templateid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
