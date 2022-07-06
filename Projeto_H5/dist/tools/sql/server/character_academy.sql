SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for character_academy
-- ----------------------------
DROP TABLE IF EXISTS `character_academy`;
CREATE TABLE `character_academy`  (
  `clanId` int(11) NOT NULL DEFAULT 0,
  `charId` int(11) NOT NULL,
  `itemId` int(11) NOT NULL,
  `price` bigint(20) NOT NULL,
  `time` bigint(11) UNSIGNED NOT NULL DEFAULT 5,
  PRIMARY KEY (`clanId`, `charId`, `itemId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of character_academy
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;