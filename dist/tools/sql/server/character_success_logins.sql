SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for character_success_logins
-- ----------------------------
DROP TABLE IF EXISTS `character_success_logins`;
CREATE TABLE `character_success_logins` (
  `date` mediumtext NOT NULL,
  `login` varchar(32) NOT NULL,
  `char_id` int(11) NOT NULL,
  `hwid` varchar(60) NOT NULL,
  `ip` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
