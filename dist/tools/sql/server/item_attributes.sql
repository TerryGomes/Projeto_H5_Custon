CREATE TABLE IF NOT EXISTS `item_attributes` (
  `object_id` int(11) NOT NULL,
  `augmentation_id` int(11) NOT NULL,
  `augmentation_skill_id` int(11) NOT NULL,
  `augmentation_skill_level` int(11) NOT NULL,
  `fire` int(11) NOT NULL,
  `water` int(11) NOT NULL,
  `wind` int(11) NOT NULL,
  `earth` int(11) NOT NULL,
  `holy` int(11) NOT NULL,
  `unholy` int(11) NOT NULL,
  PRIMARY KEY (`object_id`)
) ENGINE=MyISAM;