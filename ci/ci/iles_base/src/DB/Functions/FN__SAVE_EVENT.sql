-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__SAVE_EVENT`(IN `value` VARCHAR(255))
BEGIN
START TRANSACTION;
	INSERT INTO EVENTS(EVENTS.value)
		VALUES(value);
COMMIT;
END
