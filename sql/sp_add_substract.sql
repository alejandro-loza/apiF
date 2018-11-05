  delimiter |
DROP PROCEDURE IF EXISTS sp_add_substract;

CREATE PROCEDURE sp_add_substract (IN v_movement_id varchar(255), IN v_category_id varchar(255),IN new_amount decimal(19,2),
                                           IN v_type varchar(255),IN v_date datetime,IN v_duplicated bit(1),
                                           IN v_operation varchar(255), IN v_daysinit  int(11), IN v_daysfinal int(11))
  BEGIN

  DECLARE v_userid varchar(255);
  DECLARE v_id bigint(20);
  DECLARE v_amount decimal(19,2); 
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;


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


  IF NOT v_duplicated THEN

    IF v_operation = 'PLUS' THEN  

      CALL sp_get_movement_data(v_movement_id,v_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);

      IF v_id IS NULL THEN

        CALL sp_get_initandfinaldate ( v_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);
        INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
        values(v_userid,v_category_id,v_type,new_amount,v_initdate,v_finaldate);

      ELSE

        UPDATE  movement_stat SET amount=(new_amount+v_amount) WHERE id=v_id; 

      END IF;

    ELSE
    
      CALL sp_get_movement_data(v_movement_id,v_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);

      UPDATE movement_stat ms SET ms.amount=(v_amount-new_amount) WHERE ms.id=v_id;

    END IF;
  END IF;
  
  END;
  |
delimiter ;