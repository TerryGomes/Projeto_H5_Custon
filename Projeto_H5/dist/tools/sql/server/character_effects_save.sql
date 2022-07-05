CREATE TABLE IF NOT EXISTS `character_effects_save` (
	`object_id` INT NOT NULL,
	`skill_id` INT NOT NULL,
	`skill_level` INT NOT NULL,
	`effect_count` INT NOT NULL,
	`effect_cur_time` INT NOT NULL,
	`duration` INT NOT NULL,
	`order` INT NOT NULL,
	`id` INT NOT NULL,
	PRIMARY KEY (`object_id`,`skill_id`,`id`)
) ENGINE=MyISAM;