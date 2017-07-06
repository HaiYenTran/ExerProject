-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__CLEAR_BASELINE`()
BEGIN
	UPDATE BASELINE_VOTING AS B
		SET B.status=0
	WHERE (B.status=1 AND B.id <> 0);
END