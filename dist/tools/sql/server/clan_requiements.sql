DROP TABLE IF EXISTS `clan_requiements`;
CREATE TABLE `clan_requiements` (
  `clan_id` int(11) NOT NULL,
  `recruting` tinyint(2) DEFAULT NULL,
  `classes` text,
  `question1` text,
  `question2` text,
  `question3` text,
  `question4` text,
  `question5` text,
  `question6` text,
  `question7` text,
  `question8` text,
  PRIMARY KEY (`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;