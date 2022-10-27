CREATE TABLE IF NOT EXISTS `character_premium_items` (
	`charId` INT(11) NOT NULL,
	`itemNum` INT(11) NOT NULL,
	`itemId` INT(11) NOT NULL,
	`itemCount` BIGINT(20) UNSIGNED NOT NULL,
	`itemSender` VARCHAR(50) NOT NULL,
	KEY `charId` (`charId`),
	KEY `itemNum` (`itemNum`)
);