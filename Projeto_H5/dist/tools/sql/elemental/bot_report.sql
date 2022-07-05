DROP TABLE IF EXISTS `bot_report`;
CREATE TABLE IF NOT EXISTS `bot_report` (
  `report_id` int(10) NOT NULL auto_increment,
  `reported_name` varchar(45) DEFAULT NULL,
  `reported_objectId` int(10) DEFAULT NULL,
  `reporter_name` varchar(45) DEFAULT NULL,
  `reporter_objectId` int(10) DEFAULT NULL,
  `date` DECIMAL(20,0) NOT NULL default 0,
  `reportType` varchar(255) NOT NULL,
  `info` varchar(255) NOT NULL,
  `read` enum('true','false') DEFAULT 'false' NOT NULL,
  PRIMARY KEY (`report_id`)
);