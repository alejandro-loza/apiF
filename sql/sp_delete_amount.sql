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
  |
delimiter ;