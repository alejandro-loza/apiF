  delimiter |
DROP PROCEDURE IF EXISTS sp_update_amount_from_concept;

CREATE PROCEDURE sp_update_amount_from_concept (IN new_amount decimal(19,2),IN old_amount decimal(19,2), IN new_category_id varchar(255),
                                                IN old_category_id varchar(255), IN new_movement_id varchar(255), IN v_daysinit  int(11),
                                                IN v_daysfinal int(11))
  BEGIN

        DECLARE v_ch_amount INT(11);
        DECLARE v_ch_category INT(11);

        DECLARE v_date datetime;
        DECLARE v_type VARCHAR (255);
        DECLARE v_userid VARCHAR (255);
        DECLARE v_id bigint(20);
        DECLARE v_amount decimal(19,2); 

        DECLARE v_initdate datetime;
        DECLARE v_finaldate datetime;

        DECLARE vn_date datetime;
        DECLARE vn_type VARCHAR (255);
        DECLARE vn_userid VARCHAR (255);
        DECLARE vn_id bigint(20);
        DECLARE vn_amount decimal(19,2);
        DECLARE v_total decimal(19,2);

        SET v_ch_amount=(new_amount != old_amount);
        SET v_ch_category=(new_category_id != old_category_id);#there is a change in category if v_ch_category is equal to 1 or null

        CALL sp_get_movement_data(new_movement_id,old_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);# get the movement_stat record with old data

        IF v_ch_amount THEN #if there is a change in amount
          UPDATE movement_stat ms SET ms.amount=(v_amount-old_amount+new_amount) WHERE ms.id=v_id;# updating the latest amount value.
        ELSEIF (v_ch_category OR v_ch_category IS NULL) THEN #if there is a change of category.

        insert into test values(v_id,v_amount,old_amount);

          UPDATE movement_stat ms SET ms.amount=(v_amount-old_amount) WHERE ms.id=v_id;#Subtract the old amount on the movement_stat record.

          CALL sp_get_movement_data(new_movement_id,new_category_id,v_daysinit,v_daysfinal,vn_date,vn_type,vn_userid,vn_id,vn_amount); # get the record with new category.

           IF vn_amount IS NULL THEN #check if there is no record in movement_stat with new category.
  
            CALL sp_get_initandfinaldate ( v_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);#calculate range of date
            INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) #Insert new record
                values(vn_userid,new_category_id,vn_type,new_amount,v_initdate,v_finaldate);

          ELSE # if there was a record in movement_stat
            SET v_total=new_amount+vn_amount; #Adding the new amount to the existing record.
            UPDATE  movement_stat ms SET ms.amount=v_total WHERE ms.id=vn_id; 
           END IF;

        END IF;
  END;

DROP PROCEDURE IF EXISTS sp_update_amount_from_movement;

CREATE PROCEDURE sp_update_amount_from_movement (IN new_id varchar(255),IN new_type varchar(255),IN old_type varchar(255),
                                                 IN new_custom_date datetime,IN old_custom_date datetime, IN new_account_id varchar(255),
                                                 IN v_daysinit  int(11), IN v_daysfinal int(11))
  BEGIN
  DECLARE v_category_id VARCHAR(255);
  DECLARE v_amount_concept decimal(19,2);
  DECLARE v_userid VARCHAR (255);
  DECLARE v_id_movement_stat bigint(20);
  DECLARE v_amount_movement_stat decimal(19,2);
  DECLARE vn_id_movement_stat bigint(20);
  DECLARE vn_amount_movement_stat decimal(19,2);
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;
  DECLARE done INT DEFAULT FALSE;

  DECLARE v_ch_type int(11);
  DECLARE v_ch_date int(11);
  DEClARE concept_cursor CURSOR FOR SELECT category_id, amount FROM concept WHERE movement_id=new_id;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
 
  SET v_ch_type=new_type != old_type;
  SET v_ch_date=new_custom_date != old_custom_date;
  
  IF v_ch_type OR v_ch_date THEN 

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


  END;
  |
delimiter ;