CREATE TABLE IF NOT EXISTS`dominion` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `lord_object_id` int(11) NOT NULL DEFAULT '0',
  `wards` varchar(255) NOT NULL,
  `siege_date` bigint(255) NOT NULL,
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Records of dominion
-- ----------------------------
INSERT INTO dominion VALUES ('81', 'Gludio Territory', '0', '81;', '0');
INSERT INTO dominion VALUES ('82', 'Dion Territory', '0', '82;', '0');
INSERT INTO dominion VALUES ('83', 'Giran Territory', '0', '83;', '0');
INSERT INTO dominion VALUES ('84', 'Oren Territory', '0', '84;', '0');
INSERT INTO dominion VALUES ('85', 'Aden Territory', '0', '85;', '0');
INSERT INTO dominion VALUES ('86', 'Innadril Territory', '0', '86;', '0');
INSERT INTO dominion VALUES ('87', 'Goddard Territory', '0', '87;', '0');
INSERT INTO dominion VALUES ('88', 'Rune Territory', '0', '88;', '0');
INSERT INTO dominion VALUES ('89', 'Schuttgart Territory', '0', '89;', '0');
