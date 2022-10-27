CREATE TABLE IF NOT EXISTS  `dominion_rewards` (
  `id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `static_badges` int(11) NOT NULL,
  `kill_reward` int(11) NOT NULL,
  `online_reward` int(11) NOT NULL,
  PRIMARY KEY (`id`,`object_id`)
);
