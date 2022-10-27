CREATE TABLE IF NOT EXISTS `mods_voting_reward` (
  `id`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `data`  varchar(255) NOT NULL ,
  `scope`  varchar(255) NOT NULL ,
  `time`  bigint UNSIGNED NOT NULL ,
  PRIMARY KEY (`id`)
);