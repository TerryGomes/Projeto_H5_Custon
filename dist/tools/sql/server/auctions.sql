CREATE TABLE  IF NOT EXISTS `auctions` (
  `auction_id` bigint(20) NOT NULL,
  `seller_object_id` int(11) DEFAULT NULL,
  `seller_name` varchar(35) DEFAULT NULL,
  `item_object_id` int(11) DEFAULT NULL,
  `price_per_item` bigint(13) DEFAULT NULL,
  PRIMARY KEY (`auction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
