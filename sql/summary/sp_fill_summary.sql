delimiter |
DROP PROCEDURE IF EXISTS sp_fill_summary;
CREATE PROCEDURE sp_fill_summary ()
  BEGIN

    DECLARE v_user_id VARCHAR(255);
    DECLARE v_id VARCHAR(255);
    DECLARE v_category_id VARCHAR(255);
    DECLARE v_amount DECIMAL(19,2);
    DECLARE v_type VARCHAR(255);
    DECLARE v_custom_description VARCHAR(255);
    DECLARE v_custom_date DATETIME;
    DECLARE done INT DEFAULT FALSE;

    DECLARE movement_cursor CURSOR FOR select u.id as user_id, m.id, m.category_id, m.amount, m.type, m.custom_description, m.custom_date from movement as m inner join account as a on m.account_id = a.id inner join user as u on a.user_id = u.id and m.duplicated is false and m.date_deleted is null and m.custom_description is not null;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN movement_cursor;
    iterate_movement: LOOP

      SET done = FALSE;
      FETCH movement_cursor into v_user_id, v_id, v_category_id, v_amount, v_type, v_custom_description, v_custom_date;

      IF done THEN
        LEAVE iterate_movement;
      END IF;

      CALL sp_summary_add_substract(v_id, v_category_id, v_amount, v_type, v_custom_description, v_custom_date, FALSE , 'PLUS' );

    END LOOP;
    CLOSE movement_cursor;


  END;
  |
delimiter ;
