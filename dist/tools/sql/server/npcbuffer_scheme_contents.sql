DROP TABLE IF EXISTS `npcbuffer_scheme_contents`;
CREATE TABLE `npcbuffer_scheme_contents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scheme_id` int(11) DEFAULT NULL,
  `skill_id` int(8) DEFAULT NULL,
  `skill_level` int(4) DEFAULT NULL,
  `buff_class` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of npcbuffer_scheme_contents
-- ----------------------------
INSERT INTO `npcbuffer_scheme_contents` VALUES ('30', '7', '1036', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('31', '7', '1040', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('32', '7', '1043', '1', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('33', '7', '1044', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('34', '7', '1045', '6', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('35', '7', '1047', '4', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('36', '7', '1048', '6', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('37', '7', '1059', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('38', '7', '1068', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('39', '7', '1077', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('40', '7', '1085', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('41', '7', '1086', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('42', '7', '1087', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('43', '7', '1204', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('44', '7', '1240', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('45', '7', '1242', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('46', '7', '1243', '6', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('47', '7', '1257', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('48', '7', '1268', '4', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('49', '7', '1303', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('50', '7', '264', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('51', '7', '265', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('52', '7', '266', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('53', '7', '267', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('54', '7', '268', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('55', '7', '269', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('56', '7', '270', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('57', '7', '304', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('58', '7', '305', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('59', '7', '306', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('60', '7', '308', '1', '1');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('61', '7', '349', '1', '1');
