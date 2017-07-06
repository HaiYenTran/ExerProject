-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__GET_NODES`(IN `type` VARCHAR(50), IN `version` VARCHAR(50))
BEGIN
	SELECT * FROM NODES AS N
	WHERE N.type=type
	AND N.version=version;
END
