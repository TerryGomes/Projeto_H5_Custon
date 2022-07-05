DROP TABLE IF EXISTS `communitybuff_grp_buffs`;
CREATE TABLE `communitybuff_grp_buffs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `GpId` int(11) NOT NULL,
  `charId` int(11) NOT NULL,
  `buffid` int(11) NOT NULL,
  `bufflvl` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
