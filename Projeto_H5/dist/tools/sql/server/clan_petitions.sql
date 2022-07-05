DROP TABLE IF EXISTS `clan_petitions`;
CREATE TABLE `clan_petitions` (
  `sender_id` int(11) NOT NULL,
  `clan_id` int(11) NOT NULL,
  `answer1` text,
  `answer2` text,
  `answer3` text,
  `answer4` text,
  `answer5` text,
  `answer6` text,
  `answer7` text,
  `answer8` text,
  `comment` text,
  PRIMARY KEY (`sender_id`,`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;