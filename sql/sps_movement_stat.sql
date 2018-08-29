delimiter |
DROP PROCEDURE IF EXISTS sp_get_movement_data;

CREATE PROCEDURE sp_get_movement_data (IN v_movement_id VARCHAR(255),IN v_category_id VARCHAR(255),
                                IN v_daysinit  int(11), IN v_daysfinal int(11),OUT v_date datetime,OUT v_type VARCHAR (255),
                        OUT v_userid VARCHAR (255),OUT v_id bigint(20),OUT v_amount decimal(19,2))
  BEGIN
  SELECT m.custom_date, m.type, a.user_id
    INTO v_date,v_type,v_userid
    FROM movement m
    INNER JOIN account a on m.account_id =a.id
    WHERE m.id=v_movement_id;

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

DROP PROCEDURE IF EXISTS sp_get_movementstat_data_frommovement;

CREATE PROCEDURE sp_get_movementstat_data_frommovement (IN v_account_id VARCHAR(255),IN v_category_id VARCHAR(255),
                     IN v_date datetime,IN v_type VARCHAR (255),IN v_daysinit  int(11), IN v_daysfinal int(11),
                        OUT v_userid VARCHAR (255),OUT v_id bigint(20),OUT v_amount decimal(19,2))
  BEGIN

  SELECT a.user_id
    INTO v_userid
    FROM account a 
    WHERE a.id=v_account_id;

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
|
delimiter ;