-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
  `log_id` int(11) NOT NULL,
  `player_object_id` int(11) DEFAULT NULL,
  `action_type` varchar(40) DEFAULT NULL,
  `time` bigint(14) DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for logs_items
-- ----------------------------
DROP TABLE IF EXISTS `logs_items`;
CREATE TABLE `logs_items` (
  `log_id` int(10) DEFAULT NULL,
  `item_object_id` int(11) DEFAULT NULL,
  `item_template_id` int(10) DEFAULT NULL,
  `item_count` bigint(25) DEFAULT NULL,
  `item_enchant_level` int(2) DEFAULT NULL,
  `lost` int(1) DEFAULT NULL,
  `receiver_name` varchar(35) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;