-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__ADD_NODE`(IN `type` VARCHAR(50), IN `state` VARCHAR(50), IN `artifact` VARCHAR(50), IN `version` VARCHAR(50), IN `params` VARCHAR(50))
BEGIN
	DECLARE z_idNodes INT;
	DECLARE countParams INT;
	DECLARE valueParam TEXT;

	SELECT	id
			INTO	z_idNodes
			FROM	NODES
			WHERE	NODES.type = type
			AND    NODES.version = version;
			
	IF	z_idNodes IS NOT NULL
		THEN

		SIGNAL SQLSTATE '45000'	SET MESSAGE_TEXT = 'Cannot add node - Node exists in DB';

	END IF;

	START TRANSACTION;
	INSERT INTO NODES (NODES.type, NODES.state, NODES.artifact, NODES.version) 
		VALUES (type,state,artifact, version);
	SET z_idNodes = LAST_INSERT_ID();
	SET countParams=LENGTH(params)-LENGTH(REPLACE(params, ',', ''))+1;

	WHILE countParams>0 DO
		CALL FN__SPLIT_PROC(params,",",valueParam);
		INSERT INTO NODES_PARAM(idNodes, value) values (z_idNodes, valueParam);
    SET countParams = countParams - 1;
    END WHILE;
	COMMIT;

END