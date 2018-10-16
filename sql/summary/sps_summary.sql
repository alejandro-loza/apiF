delimiter |
DROP PROCEDURE IF EXISTS sp_get_summary_data;

CREATE PROCEDURE sp_get_summary_data (IN v_date datetime, IN v_userid varchar(255), IN v_category_id varchar(255),
                                      IN v_description varchar(255),OUT v_id bigint(20), OUT v_amount decimal(19,2),
                                      OUT v_number_movs int )
  BEGIN
   IF v_category_id IS NULL THEN

      SELECT s.id, s.amount, s.number_movs 
      INTO v_id, v_amount, v_number_movs
      FROM summary s 
      WHERE s.category_id IS NULL
      AND s.user_id=v_userid
      AND s.init_date <= v_date 
      AND s.final_date > v_date
      AND s.description = v_description;  

    ELSE

      SELECT s.id, s.amount, s.number_movs  
      INTO v_id ,v_amount, v_number_movs
      FROM summary s 
      WHERE s.category_id = v_category_id
      AND s.user_id=v_userid
      AND s.init_date <= v_date 
      AND s.final_date > v_date
      AND s.description = v_description;

    END IF;
  END;


DROP PROCEDURE IF EXISTS sp_summary_get_initandfinal_date_month;
  
CREATE PROCEDURE sp_summary_get_initandfinal_date_month (IN v_date datetime, OUT v_initdate datetime, OUT v_finaldate datetime)
  BEGIN

   DECLARE v_monthofyear VARCHAR (255);
   DECLARE v_lastdaymonth VARCHAR (255);
   DECLARE v_firstdatemonth VARCHAR (255);
   DECLARE v_year VARCHAR (255);
   DECLARE v_partdate VARCHAR (255);

   SET v_monthofyear=MONTH(v_date);
   SET v_year=YEAR(v_date);
   SET v_lastdaymonth=LAST_DAY(v_date);
   SET v_firstdatemonth=CONCAT(v_year,'-',v_monthofyear,'-01');

   SET v_initdate=CONCAT(v_firstdatemonth,' 00:00:00.000000');  
   SET v_finaldate=CONCAT(v_lastdaymonth,' 23:59:59.99999');  

  END;

DROP PROCEDURE IF EXISTS sp_summary_insert_by_user;

CREATE PROCEDURE sp_summary_insert_by_user( IN v_user_id varchar (255) )
  BEGIN

  #Varialles for movements without concepts 
  DECLARE v_movement_id varchar (255);
  DECLARE v_category_id varchar (255);
  DECLARE v_description varchar (255);
  DECLARE v_custom_description varchar (255);
  DECLARE v_custom_date datetime;
  DECLARE v_type varchar (255);
  DECLARE v_amount decimal(19,2);
  DECLARE v_duplicated bit(1);
  DECLARE done int DEFAULT FALSE;

 #Varialles for movements with concepts
  DECLARE c_movement_id varchar (255);
  DECLARE c_category_id varchar (255);
  DECLARE c_description varchar (255);
  DECLARE c_custom_description varchar (255);
  DECLARE c_custom_date datetime;
  DECLARE c_type varchar (255);
  DECLARE c_amount decimal(19,2);
  DECLARE c_duplicated bit(1);
 
  DEClARE movement_cursor CURSOR FOR SELECT m.id, m.category_id, m.amount, m.type, m.custom_description, m.custom_date, m.duplicated, m.description 
                                    FROM movement m
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE a.user_id = v_user_id
                                    AND NOT m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL
                                    AND m.type = 'CHARGE';                           
 
  DEClARE concept_cursor CURSOR FOR SELECT m.id, c.category_id, c.amount, m.type, c.description, m.custom_date, m.duplicated, m.description  
                                    FROM concept c 
                                    INNER JOIN movement m on m.id=c.movement_id
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE a.user_id = v_user_id
                                    AND m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL
                                    AND m.type = 'CHARGE'; 

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  #Iterating over the movements that does not have any concept.
  OPEN movement_cursor;
   
  iterate_movement: LOOP

  SET done = FALSE;
   
  FETCH movement_cursor INTO v_movement_id, v_category_id, v_amount, v_type, v_custom_description, v_custom_date, v_duplicated,v_description;

  IF done THEN
    LEAVE iterate_movement;
  END IF;
    
    IF v_custom_description IS NULL THEN
      SET v_custom_description = v_description;
    END IF;


    CALL sp_summary_add_substract( v_movement_id, v_category_id,
     v_amount, v_type, v_custom_description, v_custom_date, v_duplicated,'PLUS');

  END LOOP;
  CLOSE movement_cursor;

  #Iterating over the movements that have concepts.
  OPEN concept_cursor;
   
  iterate_concept: LOOP

  SET done = FALSE;
   
  FETCH concept_cursor INTO c_movement_id, c_category_id, c_amount, c_type, c_custom_description, c_custom_date, c_duplicated,c_description;

  IF done THEN
    LEAVE iterate_concept;
  END IF;

    IF c_custom_description IS NULL THEN
      SET c_custom_description = c_description;
    END IF;

    CALL sp_summary_add_substract( c_movement_id, c_category_id,
     c_amount, c_type, c_custom_description, c_custom_date, c_duplicated,'PLUS');

  END LOOP;
  CLOSE concept_cursor;
  
  END;


DROP PROCEDURE IF EXISTS sp_init_summary_insert;

CREATE PROCEDURE sp_init_summary_insert()
  BEGIN

  #Varialles for movements without concepts 
  DECLARE v_movement_id varchar (255);
  DECLARE v_category_id varchar (255);
  DECLARE v_description varchar (255);
  DECLARE v_custom_description varchar (255);
  DECLARE v_custom_date datetime;
  DECLARE v_type varchar (255);
  DECLARE v_amount decimal(19,2);
  DECLARE v_duplicated bit(1);
  DECLARE done int DEFAULT FALSE;

 #Varialles for movements with concepts
  DECLARE c_movement_id varchar (255);
  DECLARE c_category_id varchar (255);
  DECLARE c_description varchar (255);
  DECLARE c_custom_description varchar (255);
  DECLARE c_custom_date datetime;
  DECLARE c_type varchar (255);
  DECLARE c_amount decimal(19,2);
  DECLARE c_duplicated bit(1);
 
  DEClARE movement_cursor CURSOR FOR SELECT m.id, m.category_id, m.amount, m.type, m.custom_description, m.custom_date, m.duplicated, m.description 
                                    FROM movement m
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE NOT m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL
                                    AND m.type = 'CHARGE';                           
 
  DEClARE concept_cursor CURSOR FOR SELECT m.id, c.category_id, c.amount, m.type, c.description, m.custom_date, m.duplicated, m.description  
                                    FROM concept c 
                                    INNER JOIN movement m on m.id=c.movement_id
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL
                                    AND m.type = 'CHARGE'; 

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  #Iterating over the movements that does not have any concept.
  OPEN movement_cursor;
   
  iterate_movement: LOOP

  SET done = FALSE;
   
  FETCH movement_cursor INTO v_movement_id, v_category_id, v_amount, v_type, v_custom_description, v_custom_date, v_duplicated,v_description;

  IF done THEN
    LEAVE iterate_movement;
  END IF;
    
    IF v_custom_description IS NULL THEN
      SET v_custom_description = v_description;
    END IF;


    CALL sp_summary_add_substract( v_movement_id, v_category_id,
     v_amount, v_type, v_custom_description, v_custom_date, v_duplicated,'PLUS');

  END LOOP;
  CLOSE movement_cursor;

  #Iterating over the movements that have concepts.
  OPEN concept_cursor;
   
  iterate_concept: LOOP

  SET done = FALSE;
   
  FETCH concept_cursor INTO c_movement_id, c_category_id, c_amount, c_type, c_custom_description, c_custom_date, c_duplicated,c_description;

  IF done THEN
    LEAVE iterate_concept;
  END IF;

    IF c_custom_description IS NULL THEN
      SET c_custom_description = c_description;
    END IF;

    CALL sp_summary_add_substract( c_movement_id, c_category_id,
     c_amount, c_type, c_custom_description, c_custom_date, c_duplicated,'PLUS');

  END LOOP;
  CLOSE concept_cursor;
  
  END;

|
delimiter ;