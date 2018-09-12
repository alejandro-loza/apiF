  delimiter |
  DROP PROCEDURE IF EXISTS sp_init_insert_movement_stat;

CREATE PROCEDURE sp_init_insert_movement_stat ( IN v_daysinit int(11), IN v_daysfinal int(11))
  BEGIN

  DECLARE v_movement_id VARCHAR (255);
  DECLARE v_category_id VARCHAR (255);
  DECLARE v_amount_concept decimal(19,2);
  DECLARE v_date datetime;
  DECLARE v_type VARCHAR (255);
  DECLARE v_userid VARCHAR (255);
  DECLARE v_id bigint(20);
  DECLARE v_amount decimal(19,2); 
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;
  DECLARE v_total decimal(19,2); 

  DECLARE done INT DEFAULT FALSE;
  DEClARE concept_cursor CURSOR FOR SELECT movement_id, category_id, amount FROM concept;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
 
 
	OPEN concept_cursor;
	 
	iterate_concept: LOOP

	SET done = FALSE;
	 
	FETCH concept_cursor INTO v_movement_id, v_category_id, v_amount_concept;

	IF done THEN
	  LEAVE iterate_concept;
	END IF;

	 CALL sp_get_movement_data(v_movement_id,v_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);

    IF v_amount IS NULL THEN

      CALL sp_get_initandfinaldate ( v_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);
      INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
      values(v_userid,v_category_id,v_type,v_amount_concept,v_initdate,v_finaldate);
      COMMIT;

    ELSE

      SET v_total=v_amount_concept+v_amount;
      UPDATE  movement_stat SET amount=v_total WHERE id=v_id; 
      COMMIT;

    END IF;

	END LOOP;
	CLOSE concept_cursor;
  
  END;
  |
delimiter ;