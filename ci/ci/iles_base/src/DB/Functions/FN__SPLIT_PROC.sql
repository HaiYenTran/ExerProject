-- --------------------------------------------------------------------------------
-- Routine DDL
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `FN__SPLIT_PROC`(
    INOUT v_string TEXT, -- text to be split - will contain remaining string 
    IN v_separator TEXT, -- separator 
    OUT o_part TEXT -- part of v_string until first separator met
)
this: BEGIN

	SET o_part = SUBSTRING_INDEX(v_string, v_separator, 1);
    SET v_string = RIGHT(v_string, LENGTH(v_string)-LENGTH(o_part)-1);

END
