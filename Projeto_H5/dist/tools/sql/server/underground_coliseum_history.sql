DROP TABLE IF EXISTS `underground_coliseum_history`;
CREATE TABLE `underground_coliseum_history` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `wins` int(11) NOT NULL,
  PRIMARY KEY (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;