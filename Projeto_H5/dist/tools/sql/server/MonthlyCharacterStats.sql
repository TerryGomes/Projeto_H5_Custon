-- Table for monthly character and clan stats

CREATE TABLE IF NOT EXISTS `monthly_character_stats` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `variable` VARCHAR(255) NOT NULL,
  `value` BIGINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;