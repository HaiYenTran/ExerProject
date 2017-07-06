-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__CLEAR_EVENTS`()
BEGIN
	UPDATE EVENTS AS E
		SET E.status=0
	WHERE (E.status=1 AND E.id <> 0);
	
END