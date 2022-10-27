CREATE TABLE IF NOT EXISTS `poll` (
  `question` text NOT NULL,
  `answer_id` int(5) NOT NULL,
  `answer_text` text NOT NULL,
  `answer_votes` int(10) NOT NULL DEFAULT '0',
  `end_time` int(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;