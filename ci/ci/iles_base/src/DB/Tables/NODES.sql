delimiter $$

CREATE TABLE `NODES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) NOT NULL,
  `state` varchar(255) DEFAULT NULL,
  `artifact` varchar(255) DEFAULT NULL,
  `version` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1023 DEFAULT CHARSET=latin1$$


