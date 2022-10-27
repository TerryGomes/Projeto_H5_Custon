CREATE TABLE IF NOT EXISTS `report_data` (
  `obj_id` int(11) NOT NULL DEFAULT '0',
  `count` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;