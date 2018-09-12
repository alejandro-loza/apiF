  delimiter |
DROP PROCEDURE IF EXISTS sp_insert_amount;

CREATE PROCEDURE sp_insert_amount (IN new_movement_id VARCHAR(255), IN new_category_id VARCHAR(255),
                                    IN new_amount decimal(19,2), IN v_daysinit  int(11), IN v_daysfinal int(11))
  BEGIN

  DECLARE v_date datetime;
  DECLARE v_type VARCHAR (255);
  DECLARE v_userid VARCHAR (255);
  DECLARE v_id bigint(20);
  DECLARE v_amount decimal(19,2); 
  DECLARE v_initdate datetime;
  DECLARE v_finaldate datetime;
  DECLARE v_total decimal(19,2); 

  CALL sp_get_movement_data(new_movement_id,new_category_id,v_daysinit,v_daysfinal,v_date,v_type,v_userid,v_id,v_amount);

    IF v_amount IS NULL THEN

      CALL sp_get_initandfinaldate ( v_date ,v_daysinit,v_daysfinal, v_initdate,v_finaldate);
      INSERT INTO movement_stat(user_id,category_id,type,amount,init_date,final_date) 
      values(v_userid,new_category_id,v_type,new_amount,v_initdate,v_finaldate);

    ELSE

      SET v_total=new_amount+v_amount;
      UPDATE  movement_stat SET amount=v_total WHERE id=v_id; 

    END IF;
  END;
  |
delimiter ;