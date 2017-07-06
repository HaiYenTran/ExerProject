-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__LOAD_EVENTS`()
BEGIN
	SELECT * FROM EVENTS
	WHERE EVENTS.status=1;
END
