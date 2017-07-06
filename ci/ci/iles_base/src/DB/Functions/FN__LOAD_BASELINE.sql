-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE `intDB`.`FN__LOAD_BASELINE` ()
BEGIN
	SELECT * FROM BASELINE_VOTING AS B
	WHERE B.status=1;
END
