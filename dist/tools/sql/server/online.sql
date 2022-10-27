CREATE TABLE IF NOT EXISTS `online` (
  `online` varchar(10) NOT NULL,
  `offline` varchar(10) NOT NULL,
  KEY `online` (`online`),
  KEY `offline` (`offline`)
) DEFAULT CHARSET=utf8