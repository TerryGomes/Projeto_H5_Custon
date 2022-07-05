CREATE TABLE `vote_system` (
  `value_type` tinyint(4) NOT NULL DEFAULT '1',
  `value` char(100) NOT NULL DEFAULT '0',  
  `penalty_time` decimal(20) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;