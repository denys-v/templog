CREATE TABLE `temp_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `taken_at` datetime NOT NULL,
  `temperature` decimal(3,1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
