CREATE TABLE `facebook_completed_tasks` (
  `player_id` int(11) NOT NULL,
  `taken_date` bigint(13) NOT NULL,
  `comment_approved` tinyint(1) NOT NULL,
  `rewarded` tinyint(1) NOT NULL,
  `action_id` varchar(36) NOT NULL,
  `action_type_name` varchar(16) NOT NULL,
  `executor_id` varchar(32) NOT NULL,
  `created_date` bigint(13) NOT NULL,
  `extraction_date` bigint(13) NOT NULL,
  `message` text NOT NULL,
  `father_id` varchar(36) NOT NULL,
  PRIMARY KEY (`player_id`,`action_id`,`action_type_name`,`father_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `facebook_official_posts` (
  `post_id` varchar(36) NOT NULL,
  `rewards_like` tinyint(1) NOT NULL,
  `rewards_comment` tinyint(1) NOT NULL,
  `rewards_post` tinyint(1) NOT NULL,
  `rewards_share` tinyint(1) NOT NULL,
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `facebook_profiles` (
  `id` varchar(32) NOT NULL,
  `name` varchar(128) NOT NULL,
  `last_completed_task_date` bigint(13) NOT NULL DEFAULT '-1',
  `positive_points_like` tinyint(4) NOT NULL,
  `positive_points_comment` tinyint(4) NOT NULL,
  `positive_points_post` tinyint(4) NOT NULL,
  `positive_points_share` int(4) NOT NULL,
  `negative_points_like` int(6) NOT NULL,
  `negative_points_comment` int(6) NOT NULL,
  `negative_points_post` int(6) NOT NULL,
  `negative_points_share` int(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
