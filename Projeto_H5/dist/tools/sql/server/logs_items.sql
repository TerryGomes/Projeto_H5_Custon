CREATE TABLE IF NOT EXISTS `logs_items` (
  `log_id` int(10) DEFAULT NULL,
  `item_object_id` int(11) DEFAULT NULL,
  `item_template_id` int(10) DEFAULT NULL,
  `item_count` int(20) DEFAULT NULL,
  `item_enchant_level` int(2) DEFAULT NULL,
  `lost` int(1) DEFAULT NULL,
  `receiver_name` varchar(35) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;