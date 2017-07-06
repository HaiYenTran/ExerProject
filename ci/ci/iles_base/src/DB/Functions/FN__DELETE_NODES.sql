-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__DELETE_NODES`(IN `type` VARCHAR(50), IN `version` VARCHAR(50))
BEGIN
	DECLARE z_idNodes INT;

	SELECT	id
			INTO	z_idNodes
			FROM	NODES
			WHERE	NODES.type = type
			AND    NODES.version = version;
			
	IF	z_idNodes IS NOT NULL
		THEN
			DELETE FROM NODES_PARAM WHERE NODES_PARAM.idNodes = z_idNodes;
			DELETE FROM NODES WHERE NODES.id = z_idNodes;
	END IF;

	
END
