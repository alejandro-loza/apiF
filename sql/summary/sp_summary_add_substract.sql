  delimiter |
DROP PROCEDURE IF EXISTS sp_summary_add_substract;

CREATE PROCEDURE sp_summary_add_substract (IN v_movement_id varchar(255), IN v_category_id varchar(255),IN new_amount decimal(19,2),
                                           IN v_type varchar(255), IN v_description varchar(255),IN v_date datetime,IN v_duplicated bit(1),
                                           IN v_operation varchar(255))
  BEGIN

  DECLARE v_userid varchar(255);
  DECLARE v_id bigint(20);
  DECLARE v_amount decimal(19,2); 
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;
  DECLARE v_number_movs int;

  IF v_type is NULL THEN
     SELECT m.type, m.custom_date, a.user_id, m.duplicated 
     INTO v_type,v_date,v_userid, v_duplicated
     FROM movement m 
     INNER JOIN account a ON a.id=m.account_id
     WHERE m.id=v_movement_id;
  ELSE
     SELECT a.user_id 
     INTO v_userid 
     FROM movement m 
     INNER JOIN account a ON a.id=m.account_id
     WHERE m.id=v_movement_id;
  END IF;


  IF v_type ='CHARGE' AND NOT v_duplicated THEN

    IF v_operation = 'PLUS' THEN  

      CALL sp_get_summary_data(v_date,v_userid,v_category_id,v_description,v_id,v_amount,v_number_movs);

        IF v_id IS NULL THEN

          CALL sp_summary_get_initandfinal_date_month ( v_date,v_initdate,v_finaldate);
          INSERT INTO summary(user_id,category_id,amount,init_date,final_date,description,number_movs) 
                       values(v_userid,v_category_id,new_amount,v_initdate,v_finaldate,v_description,1);

        ELSE
          UPDATE  summary SET amount=(v_amount+new_amount), number_movs=(v_number_movs+1) WHERE id=v_id; 
        END IF;
     
    ELSE
    
      CALL sp_get_summary_data(v_date,v_userid,v_category_id,v_description,v_id,v_amount,v_number_movs);
      UPDATE summary SET amount=(v_amount-new_amount), number_movs=(v_number_movs-1) WHERE id=v_id; 

    END IF;
  END IF;
  
  END;

  |
delimiter ;