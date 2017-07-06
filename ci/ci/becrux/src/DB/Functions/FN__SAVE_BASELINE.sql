-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__SAVE_BASELINE`(IN `value` VARCHAR(255))
BEGIN
	CALL FN__CLEAR_BASELINE();
	INSERT INTO BASELINE_VOTING(BASELINE_VOTING.value)
		VALUES(value);
END
