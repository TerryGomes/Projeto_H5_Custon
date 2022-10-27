DROP TABLE IF EXISTS `refferal_system`;
CREATE TABLE `refferal_system` (
  `reffered_id` int(13) NOT NULL,
  `reffered_name` varchar(255) NOT NULL,
  `refferer_id` int(13) NOT NULL,
  `refferer_name` varchar(255) NOT NULL,
  PRIMARY KEY (`refferer_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

