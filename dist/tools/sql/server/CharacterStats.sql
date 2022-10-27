-- Table for character stats

CREATE TABLE IF NOT EXISTS `character_stats` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `variable` VARCHAR(255) NOT NULL,
  `value` BIGINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;