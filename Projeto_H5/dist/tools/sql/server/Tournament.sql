CREATE TABLE `tournament_battles` (
  `id` int(10) NOT NULL,
  `team_1_id` int(10) DEFAULT NULL,
  `team_2_id` int(10) DEFAULT NULL,
  `round` tinyint(2) DEFAULT NULL,
  `battle_date` bigint(13) DEFAULT NULL,
  `winner_id` int(10) DEFAULT NULL,
  `winner_won_games` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tournament_teams` (
  `team_id` int(10) NOT NULL,
  `final_position` int(4) NOT NULL DEFAULT '-1',
  `player_1_id` int(10) DEFAULT NULL,
  `player_2_id` int(10) DEFAULT NULL,
  `player_3_id` int(10) DEFAULT NULL,
  `player_4_id` int(10) DEFAULT NULL,
  `player_5_id` int(10) DEFAULT NULL,
  `player_6_id` int(10) DEFAULT NULL,
  `player_7_id` int(10) DEFAULT NULL,
  `player_8_id` int(10) DEFAULT NULL,
  `player_9_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
