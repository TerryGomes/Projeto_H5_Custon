CREATE TABLE IF NOT EXISTS `seven_signs_status` (
	`current_cycle` INT NOT NULL DEFAULT '1',
	`festival_cycle` INT NOT NULL DEFAULT '1',
	`active_period` INT NOT NULL DEFAULT '1',
	`date` INT(11) NOT NULL DEFAULT '1',
	`previous_winner` INT NOT NULL DEFAULT '0',
	`dawn_stone_score` BIGINT NOT NULL DEFAULT '0',
	`dawn_festival_score` BIGINT NOT NULL DEFAULT '0',
	`dusk_stone_score` BIGINT NOT NULL DEFAULT '0',
	`dusk_festival_score` BIGINT NOT NULL DEFAULT '0',
	`avarice_owner` INT NOT NULL DEFAULT '0',
	`gnosis_owner` INT NOT NULL DEFAULT '0',
	`strife_owner` INT NOT NULL DEFAULT '0',
	`avarice_dawn_score` INT NOT NULL DEFAULT '0',
	`gnosis_dawn_score` INT NOT NULL DEFAULT '0',
	`strife_dawn_score` INT NOT NULL DEFAULT '0',
	`avarice_dusk_score` INT NOT NULL DEFAULT '0',
	`gnosis_dusk_score` INT NOT NULL DEFAULT '0',
	`strife_dusk_score` INT NOT NULL DEFAULT '0',
	`accumulated_bonus0` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus1` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus2` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus3` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus4` BIGINT NOT NULL DEFAULT '0'
) ENGINE=MyISAM;
REPLACE INTO `seven_signs_status` VALUES
(1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
