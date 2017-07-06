-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__GET_PARAMNODE`(IN `type` VARCHAR(50), IN `version` VARCHAR(50))
BEGIN
	SELECT P.value FROM NODES AS N
	LEFT JOIN NODES_PARAM AS P
	ON N.id=P.idNodes
	WHERE N.type=type
	AND N.version=version;
END
