SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `donations`
-- ----------------------------
DROP TABLE IF EXISTS `donations`;
CREATE TABLE `donations` (
  `transactionID` varchar(30) NOT NULL,
  `email` varchar(255) NOT NULL,
  `amount` int(4) NOT NULL,
  `retrieved` varchar(6) NOT NULL,
  `retriever_ip` varchar(20) NOT NULL,
  `retriever_acc` varchar(18) NOT NULL,
  `retriever_char` varchar(26) NOT NULL,
  `retrieval_date` varchar(30) NOT NULL,
  PRIMARY KEY (`transactionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

