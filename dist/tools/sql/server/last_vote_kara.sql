SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for last_vote_kara
-- ----------------------------
DROP TABLE IF EXISTS `last_vote_kara`;
CREATE TABLE `last_vote_kara`  (
  `hwid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `site` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lastReward` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`hwid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of last_vote_kara
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;