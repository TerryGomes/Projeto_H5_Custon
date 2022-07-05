DROP TABLE IF EXISTS `premium_account_table`;
CREATE TABLE `premium_account_table` (
  `groupId` int(5) NOT NULL,
  `groupName_ru` varchar(255) NOT NULL,
  `groupName_en` varchar(255) NOT NULL,
  `exp` double NOT NULL DEFAULT '1',
  `sp` double NOT NULL DEFAULT '1',
  `adena` double NOT NULL DEFAULT '1',
  `drop` double NOT NULL DEFAULT '1',
  `spoil` double NOT NULL DEFAULT '1',
  `qdrop` double NOT NULL DEFAULT '1',
  `qreward` double NOT NULL DEFAULT '1',
  `delay` int(10) NOT NULL DEFAULT '1',
  `isHours` int(1) NOT NULL DEFAULT '0',
  `itemId` int(5) NOT NULL DEFAULT '57',
  `itemCount` bigint(10) NOT NULL DEFAULT '1000000',
  PRIMARY KEY (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `premium_account_table` VALUES ('2', 'default', 'Default', '2', '2', '2', '2', '2', '2', '2', '1', '0', '57', '1000');
INSERT INTO `premium_account_table` VALUES ('3', 'default', 'Default', '3', '3', '3', '3', '3', '3', '3', '10', '1', '57', '1000000');
INSERT INTO `premium_account_table` VALUES ('4', 'default', 'Default', '1.5', '1.5', '1.5', '1.5', '1.5', '1.5', '1.5', '2', '0', '57', '50000');
INSERT INTO `premium_account_table` VALUES ('5', 'default', 'Default', '5', '5', '5', '5', '5', '5', '5', '1', '1', '57', '100000000');
