delimiter |
DROP PROCEDURE IF EXISTS sp_get_movement_data;

CREATE PROCEDURE sp_get_movement_data (IN v_movement_id VARCHAR(255),IN v_category_id VARCHAR(255),
                                IN v_daysinit  int(11), IN v_daysfinal int(11),IN v_date datetime,IN v_type VARCHAR (255),
                        IN v_userid VARCHAR (255),OUT v_id bigint(20),OUT v_amount decimal(19,2))
  BEGIN
  
   IF v_category_id IS NULL THEN

      SELECT ms.id, ms.amount 
      INTO v_id,v_amount
      FROM movement_stat ms 
      WHERE ms.type = v_type
      AND ms.category_id IS NULL
      AND ms.user_id=v_userid
      AND ms.init_date <= v_date 
      AND ms.final_date > v_date
      AND DATEDIFF(ms.final_date, ms.init_date) BETWEEN v_daysinit AND v_daysfinal;

    ELSE

      SELECT ms.id, ms.amount 
      INTO v_id,v_amount
      FROM movement_stat ms 
      WHERE ms.type = v_type
      AND ms.category_id = v_category_id
      AND ms.user_id=v_userid
      AND ms.init_date <= v_date 
      AND ms.final_date > v_date
      AND DATEDIFF(ms.final_date, ms.init_date) BETWEEN v_daysinit AND v_daysfinal;

    END IF;
  END;


DROP PROCEDURE IF EXISTS sp_get_initandfinaldate;

CREATE PROCEDURE sp_get_initandfinaldate (IN v_date datetime,IN v_daysinit  int(11), IN v_daysfinal int(11),
                                                          OUT v_initdate datetime,OUT v_finaldate datetime)
  BEGIN

   DECLARE v_monthofyear VARCHAR (255);
   DECLARE v_dayofmonth VARCHAR (255);
   DECLARE v_lastdaymonth VARCHAR (255);
   DECLARE v_firstdatemonth VARCHAR (255);
   DECLARE v_sixteendatemonth VARCHAR (255);
   DECLARE v_year VARCHAR (255);
   DECLARE v_partdate VARCHAR (255);

     IF v_daysinit >= 1 AND  v_daysfinal  <= 1 THEN

       SET v_partdate=DATE(v_date);
       SET v_initdate=CONCAT(v_partdate,' 00:00:00.000000');  
       SET v_finaldate=CONCAT(DATE_ADD(v_partdate, INTERVAL 1 DAY),' 00:00:00.000000'); 

     ELSEIF v_daysinit >= 13 AND  v_daysfinal  <= 16 THEN

      SET v_monthofyear=MONTH(v_date);
      SET v_year=YEAR(v_date);
      SET v_dayofmonth=DAYOFMONTH(v_date);
      SET v_sixteendatemonth=CONCAT(v_year,'-',v_monthofyear,'-16');
     
        IF v_dayofmonth < 16  THEN
          SET v_firstdatemonth=CONCAT(v_year,'-',v_monthofyear,'-01');
          SET v_initdate=CONCAT(v_firstdatemonth,' 00:00:00.000000');
          SET v_finaldate=CONCAT(v_sixteendatemonth,' 00:00:00.000000');
        ELSE
          SET v_initdate=CONCAT(v_sixteendatemonth,' 00:00:00.000000');
          SET v_lastdaymonth=LAST_DAY(v_date);
          SET v_finaldate=CONCAT(DATE_ADD(v_lastdaymonth, INTERVAL 1 DAY),' 00:00:00.000000');  
        END IF;

    ELSEIF v_daysinit >= 28 AND  v_daysfinal  <= 31 THEN

      SET v_monthofyear=MONTH(v_date);
      SET v_year=YEAR(v_date);
      SET v_lastdaymonth=LAST_DAY(v_date);
      SET v_firstdatemonth=CONCAT(v_year,'-',v_monthofyear,'-01');

      SET v_initdate=CONCAT(v_firstdatemonth,' 00:00:00.000000');  
      SET v_finaldate=CONCAT(DATE_ADD(v_lastdaymonth, INTERVAL 1 DAY),' 00:00:00.000000');  


    END IF;
  END;

DROP PROCEDURE IF EXISTS sp_insert_by_user;

CREATE PROCEDURE sp_insert_by_user( IN v_user_id varchar (255) , IN v_daysinit int(11), IN v_daysfinal int(11) )
  BEGIN

  #Varialles for movements without concepts 
  DECLARE v_movement_id varchar (255);
  DECLARE v_category_id varchar (255);
  DECLARE v_custom_date datetime;
  DECLARE v_type varchar (255);
  DECLARE v_amount decimal(19,2);
  DECLARE v_duplicated bit(1);
  DECLARE done int DEFAULT FALSE;

 #Varialles for movements with concepts
  DECLARE c_movement_id varchar (255);
  DECLARE c_category_id varchar (255);
  DECLARE c_custom_date datetime;
  DECLARE c_type varchar (255);
  DECLARE c_amount decimal(19,2);
  DECLARE c_duplicated bit(1);
 
  DEClARE movement_cursor CURSOR FOR SELECT m.id, m.category_id, m.amount, m.type, m.custom_date, m.duplicated
                                    FROM movement m
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE a.user_id = v_user_id
                                    AND NOT m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL;
                                                        
 
  DEClARE concept_cursor CURSOR FOR SELECT m.id, c.category_id, c.amount, m.type, m.custom_date, m.duplicated 
                                    FROM concept c 
                                    INNER JOIN movement m on m.id=c.movement_id
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE a.user_id = v_user_id
                                    AND m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL;
                                

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  #Iterating over the movements that does not have any concept.
  OPEN movement_cursor;
   
  iterate_movement: LOOP

  SET done = FALSE;
   
  FETCH movement_cursor INTO v_movement_id, v_category_id, v_amount, v_type, v_custom_date, v_duplicated;

  IF done THEN
    LEAVE iterate_movement;
  END IF;

     CALL sp_add_substract(v_movement_id, v_category_id, v_amount,v_type,v_custom_date,v_duplicated,'PLUS',v_daysinit,v_daysfinal);

  END LOOP;
  CLOSE movement_cursor;

  #Iterating over the movements that have concepts.
  OPEN concept_cursor;
   
  iterate_concept: LOOP

  SET done = FALSE;
   
  FETCH concept_cursor INTO c_movement_id, c_category_id, c_amount, c_type, c_custom_date, c_duplicated;

  IF done THEN
    LEAVE iterate_concept;
  END IF;

     CALL sp_add_substract(c_movement_id, c_category_id, c_amount,c_type,c_custom_date,c_duplicated,'PLUS',v_daysinit,v_daysfinal);

  END LOOP;
  CLOSE concept_cursor;
  
  END;
DROP PROCEDURE IF EXISTS sp_insert;

CREATE PROCEDURE sp_insert( IN v_daysinit int(11), IN v_daysfinal int(11) )
  BEGIN

  #Varialles for movements without concepts 
  DECLARE v_movement_id varchar (255);
  DECLARE v_category_id varchar (255);
  DECLARE v_custom_date datetime;
  DECLARE v_type varchar (255);
  DECLARE v_amount decimal(19,2);
  DECLARE v_duplicated bit(1);
  DECLARE done int DEFAULT FALSE;

 #Varialles for movements with concepts
  DECLARE c_movement_id varchar (255);
  DECLARE c_category_id varchar (255);
  DECLARE c_custom_date datetime;
  DECLARE c_type varchar (255);
  DECLARE c_amount decimal(19,2);
  DECLARE c_duplicated bit(1);
 
  DEClARE movement_cursor CURSOR FOR SELECT m.id, m.category_id, m.amount, m.type, m.custom_date, m.duplicated
                                    FROM movement m
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE NOT m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL;
                                                        
 
  DEClARE concept_cursor CURSOR FOR SELECT m.id, c.category_id, c.amount, m.type, m.custom_date, m.duplicated 
                                    FROM concept c 
                                    INNER JOIN movement m on m.id=c.movement_id
                                    INNER JOIN account a ON m.account_id = a.id
                                    WHERE m.has_concepts
                                    AND NOT m.duplicated 
                                    AND m.date_deleted IS NULL;
                                

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  #Iterating over the movements that does not have any concept.
  OPEN movement_cursor;
   
  iterate_movement: LOOP

  SET done = FALSE;
   
  FETCH movement_cursor INTO v_movement_id, v_category_id, v_amount, v_type, v_custom_date, v_duplicated;

  IF done THEN
    LEAVE iterate_movement;
  END IF;

     CALL sp_add_substract(v_movement_id, v_category_id, v_amount,v_type,v_custom_date,v_duplicated,'PLUS',v_daysinit,v_daysfinal);

  END LOOP;
  CLOSE movement_cursor;

  #Iterating over the movements that have concepts.
  OPEN concept_cursor;
   
  iterate_concept: LOOP

  SET done = FALSE;
   
  FETCH concept_cursor INTO c_movement_id, c_category_id, c_amount, c_type, c_custom_date, c_duplicated;

  IF done THEN
    LEAVE iterate_concept;
  END IF;

     CALL sp_add_substract(c_movement_id, c_category_id, c_amount,c_type,c_custom_date,c_duplicated,'PLUS',v_daysinit,v_daysfinal);

  END LOOP;
  CLOSE concept_cursor;
  
  END;
|
delimiter ;