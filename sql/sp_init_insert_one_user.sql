delimiter |
DROP PROCEDURE IF EXISTS sp_init_insert_concept_one_user;

CREATE PROCEDURE sp_init_insert_concept_one_user ( IN v_daysinit int(11), IN v_daysfinal int(11), IN v_user_id VARCHAR (255))
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
  DEClARE concept_cursor CURSOR FOR SELECT c.movement_id, c.category_id, c.amount 
                                    FROM concept c 
                                    INNER JOIN movement m ON c.movement_id = m.id 
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE a.user_id = v_user_id
                                    AND m.has_concepts 
                                    AND m.date_deleted IS NULL;

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

DROP PROCEDURE IF EXISTS sp_init_insert_movement_one_user;

CREATE PROCEDURE sp_init_insert_movement_one_user ( IN v_daysinit int(11), IN v_daysfinal int(11), IN v_user_id VARCHAR (255))
  BEGIN

  DECLARE v_movement_id VARCHAR (255);
  DECLARE v_category_id VARCHAR (255);
  DECLARE v_id_movement_stat bigint(20);
  DECLARE v_account_id VARCHAR (255);
  DECLARE v_custom_date datetime;
  DECLARE v_type VARCHAR (255);
  DECLARE vv_userid VARCHAR (255);
  DECLARE v_amount_movement_stat decimal(19,2); 
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;
  DECLARE v_total decimal(19,2); 
  DECLARE v_amount decimal(19,2); 

  DECLARE done INT DEFAULT FALSE;
  DEClARE movement_cursor CURSOR FOR SELECT m.id, m.category_id, m.amount, m.account_id, m.custom_date, m.type
                                    FROM movement m
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE a.user_id = v_user_id
                                    AND NOT m.has_concepts 
                                    AND m.date_deleted IS NULL;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
 
  OPEN movement_cursor;
   
  iterate_movement: LOOP

  SET done = FALSE;
   
  FETCH movement_cursor INTO v_movement_id, v_category_id, v_amount, v_account_id,v_custom_date,v_type;

  IF done THEN
    LEAVE iterate_movement;
  END IF;

   CALL sp_get_movementstat_data_frommovement (v_account_id,v_category_id,v_custom_date,v_type,v_daysinit,
                                                v_daysfinal,vv_userid,v_id_movement_stat,v_amount_movement_stat);

    IF v_amount_movement_stat IS NULL THEN

      CALL sp_get_initandfinaldate ( v_custom_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);
      INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
      values(v_user_id,v_category_id,v_type,v_amount,v_initdate,v_finaldate);
      COMMIT;

    ELSE

      SET v_total=v_amount_movement_stat+v_amount;
      UPDATE  movement_stat SET amount=v_total WHERE id=v_id; 
      COMMIT;

    END IF;

  END LOOP;
  CLOSE movement_cursor;
  
  END;
  |