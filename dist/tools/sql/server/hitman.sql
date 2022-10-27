CREATE TABLE IF NOT EXISTS `event_hitman` (
	`storedId` BIGINT NOT NULL,
	`owner` varchar(25) NOT NULL,
	`target` varchar(25) NOT NULL,
	`itemId` int(11) NOT NULL,
	`itemCount` int(25) NOT NULL,
	`killsCount` int(20) NOT NULL,
	PRIMARY KEY (`storedId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;