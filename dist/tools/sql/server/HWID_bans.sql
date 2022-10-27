DROP TABLE IF EXISTS `hwid_bans`;
CREATE TABLE `hwid_bans` (
 `HWID` VARCHAR( 48 ) default NULL ,
 `HWIDSecond` varchar(48) default NULL,
 `expiretime` int(11) NOT NULL default '0',
 `comments` varchar(255) default '',
 UNIQUE (`HWID` )
) ENGINE = MYISAM;