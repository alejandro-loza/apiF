delimiter |
DROP PROCEDURE IF EXISTS sp_change_on_movement;

CREATE PROCEDURE sp_change_on_movement (IN new_has_concepts bit(1),IN old_has_concepts bit(1),IN new_amount decimal(19,2),IN old_amount decimal(19,2),
                                        IN new_category_id varchar(255),IN old_category_id varchar(255),IN new_type varchar(255),IN old_type varchar(255),
                                        IN new_custom_date datetime,IN old_custom_date datetime, IN new_account_id varchar(255),IN new_id varchar(255),
                                        IN new_date_deleted datetime,IN v_daysinit int(11), IN v_daysfinal int(11))
  BEGIN

  DECLARE v_ch_has_concepts INT(11);
  DECLARE v_ch_amount INT(11);
  DECLARE v_ch_date INT(11);
  DECLARE v_ch_type INT(11);
  DECLARE v_ch_category INT(11);
  DECLARE v_id_movement_stat bigint(20);
  DECLARE v_amount_movement_stat decimal(19,2);
  DECLARE v_userid VARCHAR (255);
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;
  DECLARE v_new_id_movement_stat bigint(20);
  DECLARE v_new_amount_movement_stat decimal(19,2);
  #Variables for concepts
  DECLARE v_category_id VARCHAR(255);
  DECLARE v_amount_concept decimal(19,2);
  DECLARE vn_id_movement_stat bigint(20);
  DECLARE vn_amount_movement_stat decimal(19,2);
  DECLARE done INT DEFAULT FALSE;
  DEClARE concept_cursor CURSOR FOR SELECT category_id, amount FROM concept WHERE movement_id=new_id;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  SET v_ch_has_concepts=new_has_concepts != old_has_concepts;
  SET v_ch_amount=new_amount != old_amount;
  SET v_ch_date=new_custom_date != old_custom_date;
  SET v_ch_type=new_type != old_type;
  SET v_ch_category=new_category_id != old_category_id;
 
  #If user add or remove concepts
  IF v_ch_has_concepts AND new_has_concepts  THEN

    CALL sp_get_movementstat_data_frommovement (new_account_id,old_category_id,new_custom_date,new_type,v_daysinit,
                                                v_daysfinal,v_userid,v_id_movement_stat,v_amount_movement_stat);

    UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat-new_amount) WHERE ms.id=v_id_movement_stat;

  ELSEIF v_ch_has_concepts AND NOT new_has_concepts  THEN

    CALL sp_get_movementstat_data_frommovement (new_account_id,new_category_id,new_custom_date,new_type,v_daysinit,
                                                v_daysfinal,v_userid,v_id_movement_stat,v_amount_movement_stat);

    IF v_amount_movement_stat IS NULL THEN

      CALL sp_get_initandfinaldate ( new_custom_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);
      INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
      values(v_userid,new_category_id,new_type,new_amount,v_initdate,v_finaldate);

    ELSE

      UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat+new_amount) WHERE ms.id=v_id_movement_stat;

    END IF;

  END IF;

  # If user change amount or date or category while it does not have concepts
   IF ((v_ch_amount OR v_ch_date OR v_ch_type OR v_ch_category OR v_ch_category IS NULL) AND (NOT v_ch_has_concepts AND NOT new_has_concepts)) THEN

    CALL sp_get_movementstat_data_frommovement(new_account_id,old_category_id,old_custom_date,old_type,v_daysinit,
                                                   v_daysfinal,v_userid,v_id_movement_stat,v_amount_movement_stat);

    UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat-old_amount) WHERE ms.id=v_id_movement_stat;

    CALL sp_get_movementstat_data_frommovement(new_account_id,new_category_id,new_custom_date,new_type,v_daysinit,
                                                   v_daysfinal,v_userid,v_new_id_movement_stat,v_new_amount_movement_stat);

    IF v_new_amount_movement_stat IS NULL THEN

      CALL sp_get_initandfinaldate ( new_custom_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);
      INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
      values(v_userid,new_category_id,new_type,new_amount,v_initdate,v_finaldate);

    ELSE

      UPDATE movement_stat ms SET ms.amount=(v_new_amount_movement_stat+new_amount) WHERE ms.id=v_new_id_movement_stat;

    END IF;
  # IF user change date or type while there are concepts
   ELSEIF ((v_ch_date OR v_ch_type ) AND (NOT v_ch_has_concepts AND new_has_concepts)) THEN
  
    OPEN concept_cursor;
     
    iterate_concept: LOOP

    SET done = FALSE;
     
    FETCH concept_cursor INTO v_category_id, v_amount_concept;

    IF done THEN
      LEAVE iterate_concept;
    END IF;

    CALL sp_get_movementstat_data_frommovement (new_account_id,v_category_id,old_custom_date,old_type,v_daysinit,
                                                v_daysfinal,v_userid,v_id_movement_stat,v_amount_movement_stat);

    UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat-v_amount_concept) WHERE ms.id=v_id_movement_stat;
            
    CALL sp_get_movementstat_data_frommovement (new_account_id,v_category_id,new_custom_date,new_type,v_daysinit,
                                                v_daysfinal,v_userid,vn_id_movement_stat,vn_amount_movement_stat);
    IF vn_amount_movement_stat IS NULL THEN 
  
     CALL sp_get_initandfinaldate ( new_custom_date, v_daysinit, v_daysfinal, v_initdate,v_finaldate);
        INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
        values(v_userid,v_category_id,new_type,v_amount_concept,v_initdate,v_finaldate);
    ELSE 
      UPDATE  movement_stat ms SET ms.amount=(vn_amount_movement_stat+v_amount_concept) WHERE ms.id=vn_id_movement_stat; 
     END IF;

    END LOOP;
    CLOSE concept_cursor;
    
   END IF;

   #If User delete a movement and does not have concepts.
   IF new_date_deleted IS NOT NULL AND NOT new_has_concepts THEN

    CALL sp_get_movementstat_data_frommovement (new_account_id,new_category_id,new_custom_date,new_type,v_daysinit,
                                                v_daysfinal,v_userid,v_id_movement_stat,v_amount_movement_stat);

    UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat-new_amount) WHERE ms.id=v_id_movement_stat;
   # If User delete a movement and have concepts
   ELSEIF new_date_deleted IS NOT NULL AND new_has_concepts THEN

    OPEN concept_cursor;
     
    iterate_concept: LOOP

    SET done = FALSE;
     
    FETCH concept_cursor INTO v_category_id, v_amount_concept;

    IF done THEN
      LEAVE iterate_concept;
    END IF;

    CALL sp_get_movementstat_data_frommovement (new_account_id,v_category_id,old_custom_date,old_type,v_daysinit,
                                                v_daysfinal,v_userid,v_id_movement_stat,v_amount_movement_stat);

    UPDATE movement_stat ms SET ms.amount=(v_amount_movement_stat-v_amount_concept) WHERE ms.id=v_id_movement_stat;
    END LOOP;
    CLOSE concept_cursor;
    
   END IF;

  END;

DROP PROCEDURE IF EXISTS sp_change_on_concept;

CREATE PROCEDURE sp_change_on_concept(IN new_amount decimal(19,2), IN old_amount decimal(19,2), IN new_category_id varchar(255),
        IN new_movement_id varchar(255),IN new_concept_type varchar(255), IN v_daysinit  int(11), IN v_daysfinal int(11))
  BEGIN
   DECLARE v_ch_date INT(11);
   DECLARE v_date datetime;
   DECLARE v_userid VARCHAR (255);
   DECLARE v_id VARCHAR (255);
   DECLARE v_type VARCHAR (255);
   DECLARE v_amount decimal(19,2);
  DECLARE v_ch_amount INT(11);

  SET v_ch_amount=new_amount != old_amount;

  CALL sp_get_movement_data(new_movement_id,new_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);
    
  IF (v_ch_amount AND new_concept_type='DEFAULT') THEN 
    
    UPDATE movement_stat ms SET ms.amount=(v_amount-old_amount+new_amount) WHERE ms.id=v_id;
  END IF;

  END;
|
delimiter ;