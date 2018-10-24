delimiter |
DROP PROCEDURE IF EXISTS sp_summary_change_on_movement;

CREATE PROCEDURE sp_summary_change_on_movement (IN new_has_concepts bit(1),IN old_has_concepts bit(1),IN new_amount decimal(19,2),IN old_amount decimal(19,2),
                                        IN new_category_id varchar(255),IN old_category_id varchar(255),IN new_type varchar(255),IN old_type varchar(255),
                                        IN new_custom_date datetime,IN old_custom_date datetime, IN new_movement_id varchar(255),
                                        IN new_date_deleted datetime, IN new_description varchar(255), IN old_description varchar(255),IN new_duplicated bit(1),IN old_duplicated bit(1))
  BEGIN

  DECLARE v_ch_has_concepts INT(11);
  DECLARE v_ch_amount INT(11);
  DECLARE v_ch_date INT(11);
  DECLARE v_ch_type INT(11);
  DECLARE v_ch_category INT(11);
  DECLARE v_ch_description INT(11);
  DECLARE v_ch_duplicated INT(11);
  #Variables for concepts
  DECLARE v_category_id VARCHAR(255);
  DECLARE v_description VARCHAR(255);
  DECLARE v_amount_concept decimal(19,2);

  DECLARE done INT DEFAULT FALSE;
  DEClARE concept_cursor CURSOR FOR SELECT category_id, amount, description FROM concept WHERE movement_id=new_movement_id;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  SET v_ch_has_concepts=new_has_concepts != old_has_concepts;
  SET v_ch_amount=new_amount != old_amount;
  SET v_ch_date=new_custom_date != old_custom_date;
  SET v_ch_type=new_type != old_type;
  SET v_ch_category=new_category_id != old_category_id;
  SET v_ch_description=new_description != old_description;
  SET v_ch_duplicated=new_duplicated != old_duplicated;
  
  #If user add or remove concepts
  IF v_ch_has_concepts AND new_has_concepts  THEN

    CALL sp_summary_add_substract( new_movement_id, old_category_id, 
      new_amount, new_type, old_description, new_custom_date,new_duplicated,'MINUS' );

  ELSEIF v_ch_has_concepts AND NOT new_has_concepts  THEN

    CALL sp_summary_add_substract(new_movement_id, new_category_id,
     new_amount, new_type, new_description,new_custom_date, new_duplicated,'PLUS');

  END IF;

  # If user change amount, date, type, description or category while it does not have concepts
   IF ((v_ch_amount OR v_ch_date OR v_ch_type OR v_ch_description 
    OR v_ch_duplicated OR v_ch_category OR v_ch_category IS NULL) AND (NOT v_ch_has_concepts AND NOT new_has_concepts)) THEN

    CALL sp_summary_add_substract(new_movement_id,old_category_id,
      old_amount,old_type,old_description,old_custom_date,old_duplicated,'MINUS');

    CALL sp_summary_add_substract(new_movement_id, new_category_id,
     new_amount, new_type, new_description,new_custom_date,new_duplicated,'PLUS');

    # IF user change duplicated,date or type while there are concepts

   ELSEIF ((v_ch_date OR v_ch_type OR v_ch_duplicated ) AND (NOT v_ch_has_concepts AND new_has_concepts)) THEN

    OPEN concept_cursor;
     
    iterate_concept: LOOP

    SET done = FALSE;
     
    FETCH concept_cursor INTO v_category_id, v_amount_concept,v_description;

    IF done THEN
      LEAVE iterate_concept;
    END IF;
   
    CALL sp_summary_add_substract( new_movement_id, v_category_id, 
      v_amount_concept, old_type, v_description, old_custom_date,old_duplicated,'MINUS' );
       
    CALL sp_summary_add_substract(new_movement_id, v_category_id,
     v_amount_concept, new_type,v_description,new_custom_date, new_duplicated,'PLUS');

    END LOOP;
    CLOSE concept_cursor;

   END IF;

   #If User delete a movement and does not have concepts.
   IF new_date_deleted IS NOT NULL AND NOT new_has_concepts THEN

    CALL sp_summary_add_substract( new_movement_id, new_category_id, 
      new_amount, new_type, new_description, new_custom_date,new_duplicated,'MINUS' );
   # If User delete a movement and have concepts
   ELSEIF new_date_deleted IS NOT NULL AND new_has_concepts THEN

    OPEN concept_cursor;
     
    iterate_concept: LOOP

    SET done = FALSE;
     
    FETCH concept_cursor INTO v_category_id, v_amount_concept,v_description;

    IF done THEN
      LEAVE iterate_concept;
    END IF;
   
    CALL sp_summary_add_substract( new_movement_id, v_category_id, 
      v_amount_concept, old_type, v_description, old_custom_date,old_duplicated,'MINUS' );
       
    END LOOP;
    CLOSE concept_cursor;    

  END IF;

  END;
DROP PROCEDURE IF EXISTS sp_summary_change_on_concept;

CREATE PROCEDURE sp_summary_change_on_concept(IN new_amount decimal(19,2), IN old_amount decimal(19,2), IN new_category_id varchar(255),
        IN new_movement_id varchar(255),IN new_concept_type varchar(255), IN new_description varchar(255))
  BEGIN
   DECLARE v_ch_date INT(11);
   DECLARE v_date datetime;
   DECLARE v_userid VARCHAR (255);
   DECLARE v_id VARCHAR (255);
   DECLARE v_type VARCHAR (255);
   DECLARE v_amount decimal(19,2);
   DECLARE v_ch_amount INT(11);

  SET v_ch_amount=new_amount != old_amount;

  IF (v_ch_amount AND new_concept_type='DEFAULT') THEN 

    CALL sp_summary_add_substract( new_movement_id, new_category_id, 
      old_amount, null, new_description, null,null,'MINUS' );

    CALL sp_summary_add_substract( new_movement_id, new_category_id, 
      new_amount, null, new_description, null,null,'PLUS' );
       
  END IF;

  END

|
delimiter ;