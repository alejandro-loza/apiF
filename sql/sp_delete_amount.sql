  delimiter |
DROP PROCEDURE IF EXISTS sp_delete_amount_from_concept;

CREATE PROCEDURE sp_delete_amount_from_concept (IN old_movement_id varchar(255), IN old_category_id varchar(255), 
                                                IN old_amount decimal(19,2), IN v_daysinit  int(11), IN v_daysfinal int(11) )
  BEGIN

  DECLARE v_date datetime;
  DECLARE v_type VARCHAR (255);
  DECLARE v_userid VARCHAR (255);
  DECLARE v_id bigint(20);
  DECLARE v_amount decimal(19,2);

  CALL sp_get_movement_data(old_movement_id,old_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);

  UPDATE movement_stat ms SET ms.amount=(v_amount-old_amount) WHERE ms.id=v_id;# updating the latest amount value.
        
  END;

DROP PROCEDURE IF EXISTS sp_delete_amount_from_movement;

CREATE PROCEDURE sp_delete_amount_from_movement (IN old_id varchar(255), IN old_account_id varchar(255),
                                                 IN old_custom_date datetime,IN old_type varchar(255),
                                                 IN v_daysinit  int(11), IN v_daysfinal int(11))
  BEGIN
 
  DECLARE v_category_id VARCHAR(255);
  DECLARE v_amount_concept decimal(19,2);
  DECLARE v_userid VARCHAR (255);
  DECLARE v_id_movement_stat bigint(20);
  DECLARE v_amount_movement_stat decimal(19,2);
  DECLARE done INT DEFAULT FALSE;

  DEClARE concept_cursor CURSOR FOR SELECT category_id, amount FROM concept WHERE movement_id=old_id;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN concept_cursor;
   
  iterate_concept: LOOP

  SET done = FALSE;
   
  FETCH concept_cursor INTO v_category_id, v_amount_concept;
   
  IF done THEN
  LEAVE iterate_concept;
  END IF;

  CALL sp_get_movementstat_data_frommovement (old_account_id,v_category_id,old_custom_date,old_type,v_daysinit,v_daysfinal,
                                                  v_userid,v_id_movement_stat,v_amount_movement_stat);
  UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat-v_amount_concept) WHERE ms.id=v_id_movement_stat;
          
  END LOOP iterate_concept;
  CLOSE concept_cursor;

  END;
  |
delimiter ;