DROP TABLE IF EXISTS `add_spawnlist`;
CREATE TABLE `add_spawnlist` (
  `location` varchar(35) NOT NULL DEFAULT '',
  `count` int(11) NOT NULL DEFAULT '0',
  `npc_templateid` int(11) NOT NULL DEFAULT '0',
  `locx` int(11) NOT NULL DEFAULT '0',
  `locy` int(11) NOT NULL DEFAULT '0',
  `locz` int(11) NOT NULL DEFAULT '0',
  `heading` int(11) NOT NULL DEFAULT '0',
  `respawn_delay` int(11) NOT NULL DEFAULT '0',
  `respawn_delay_rnd` int(11) NOT NULL DEFAULT '0',
  `loc_id` int(11) NOT NULL DEFAULT '0',
  `periodOfDay` tinyint(1) NOT NULL DEFAULT '0',
  `reflection` smallint(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_templateid`,`locx`,`locy`,`locz`,`loc_id`,`reflection`),
  KEY `key_npc_templateid` (`npc_templateid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of add_spawnlist
-- ----------------------------
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '83368', '147912', '-3400', '17828', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '83480', '147912', '-3400', '15064', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '83208', '148392', '-3368', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37009', '83204', '148841', '-3368', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37004', '83672', '147992', '-3400', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37011', '83688', '148856', '-3400', '28201', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37010', '83272', '149320', '-3400', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37005', '83352', '149320', '-3400', '48783', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37014', '83448', '149320', '-3400', '44315', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37006', '83688', '149240', '-3400', '35835', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37015', '82888', '149384', '-3464', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '146728', '25880', '-2008', '62980', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '147816', '25544', '-2008', '16383', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '147224', '27384', '-2200', '51188', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37009', '147688', '27368', '-2200', '2555', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '147864', '-55208', '-2728', '45796', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '147368', '-55304', '-2728', '53988', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '147272', '-55480', '-2728', '57343', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37009', '148152', '-55464', '-2728', '47165', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '43752', '-47672', '-792', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '43592', '-47672', '-792', '42281', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '43864', '-47672', '-792', '47619', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '18200', '145192', '-3072', '1287', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '18152', '145272', '-3056', '3675', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '18056', '145336', '-3072', '8384', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '82984', '53096', '-1488', '32472', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '82504', '53144', '-1488', '62800', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '83112', '53944', '-1488', '33226', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '-13976', '123304', '-3120', '27777', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '-14776', '123096', '-3112', '5075', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '-14536', '124072', '-3112', '55756', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '-80792', '149688', '-3040', '26634', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '-80712', '149816', '-3040', '27931', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '87128', '-143480', '-1288', '16383', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37009', '87704', '-143368', '-1288', '27931', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '87816', '-143224', '-1288', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '111304', '219416', '-3544', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37002', '117128', '77144', '-2688', '43515', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37001', '117112', '76984', '-2704', '35323', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37000', '117192', '76776', '-2688', '36123', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37004', '117096', '77064', '-2688', '37604', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '32864', '83704', '147432', '-3400', '11547', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '30627', '81752', '147496', '-3464', '45183', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '30627', '81352', '147496', '-3464', '54566', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '30627', '81752', '149736', '-3464', '44315', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '30627', '81352', '149768', '-3464', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '31324', '83496', '149560', '-3408', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82712', '149320', '-3472', '46596', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82952', '149320', '-3472', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82952', '149544', '-3472', '57343', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82712', '149560', '-3472', '32024', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82728', '147672', '-3472', '14661', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82712', '147896', '-3472', '22020', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82952', '147896', '-3472', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82952', '147656', '-3472', '49895', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '81144', '147656', '-3472', '35048', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '81144', '147896', '-3472', '4836', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '80904', '147896', '-3472', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '80904', '147656', '-3472', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '80904', '149320', '-3472', '16383', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '80904', '149560', '-3472', '16383', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '81144', '149560', '-3472', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '81144', '149320', '-3472', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37024', '82091', '148776', '-3176', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37025', '82088', '148456', '-3176', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37023', '81756', '148446', '-3176', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37020', '81756', '148771', '-3176', '32767', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '82088', '148600', '-3248', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '81928', '148776', '-3248', '11547', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37021', '81768', '148616', '-3248', '0', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '37022', '81912', '148440', '-3248', '49151', '30', '0', '0', '0', '0');
INSERT INTO `add_spawnlist` VALUES ('', '1', '109', '82904', '149496', '-3464', '49151', '30', '0', '0', '0', '0');
