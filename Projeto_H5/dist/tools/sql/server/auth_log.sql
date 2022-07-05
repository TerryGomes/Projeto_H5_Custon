SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auth_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_log`;
CREATE TABLE `auth_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `account` varchar(14) NOT NULL,
  `hwid` varchar(48) NOT NULL,
  `ip` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1112729 DEFAULT CHARSET=utf8;
