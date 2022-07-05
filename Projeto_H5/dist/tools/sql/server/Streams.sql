CREATE TABLE `streams` (
  `channel_name` varchar(255) NOT NULL,
  `connected_player_id` int(12) DEFAULT NULL,
  `connected_player_server` varchar(255) DEFAULT NULL,
  `ids_awaiting_approval` varchar(255) DEFAULT NULL,
  `not_rewarded_seconds` int(12) DEFAULT NULL,
  `total_rewarded_seconds_today` int(12) DEFAULT NULL,
  `punished_until_date` int(12) DEFAULT NULL,
  PRIMARY KEY (`channel_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `remdev_payments` (   `id` int(10) unsigned NOT NULL AUTO_INCREMENT,   `obj_id` int(11) DEFAULT NULL,   `char_name` varchar(35) COLLATE utf8_unicode_ci NOT NULL,   `amount` int(11) NOT NULL,   `time` int(11) NOT NULL,   `pay_system` varchar(35) COLLATE utf8_unicode_ci DEFAULT NULL,   `status` tinyint(1) NOT NULL DEFAULT '0',   `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,   `real_amount` int(11) DEFAULT '0',   PRIMARY KEY (`id`) ) ENGINE=InnoDB AUTO_INCREMENT=1376 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
