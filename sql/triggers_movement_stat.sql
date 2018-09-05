  delimiter |
DROP TRIGGER IF EXISTS t_insert_amount;
CREATE TRIGGER t_insert_amount AFTER INSERT ON concept
  FOR EACH ROW
  BEGIN

  CALL sp_insert_amount (NEW.movement_id, NEW.category_id, NEW.amount, 1, 1);#This call is for only one day
  CALL sp_insert_amount (NEW.movement_id, NEW.category_id, NEW.amount, 28, 31); #This call is for one month

 END;

DROP TRIGGER IF EXISTS t_update_amount_from_concept;
CREATE TRIGGER t_update_amount_from_concept AFTER UPDATE ON concept
  FOR EACH ROW
  BEGIN

  CALL sp_update_amount_from_concept (NEW.amount, OLD.amount, NEW.category_id, OLD.category_id, NEW.movement_id,1,1);
  CALL sp_update_amount_from_concept (NEW.amount, OLD.amount, NEW.category_id, OLD.category_id, NEW.movement_id,28,31);

  END;

DROP TRIGGER IF EXISTS t_update_amount_from_movement;
CREATE TRIGGER t_update_amount_from_movement AFTER UPDATE ON movement
  FOR EACH ROW
  BEGIN
   CALL sp_update_amount_from_movement ( NEW.id, NEW.type, OLD.type, NEW.custom_date, OLD.custom_date, NEW.account_id,1,1);
   CALL sp_update_amount_from_movement ( NEW.id, NEW.type, OLD.type, NEW.custom_date, OLD.custom_date, NEW.account_id,28,31);
  END;

DROP TRIGGER IF EXISTS t_delete_amount;
CREATE TRIGGER t_delete_amount AFTER DELETE ON concept
  FOR EACH ROW
  BEGIN
    CALL sp_delete_amount_from_concept( OLD.movement_id,OLD.category_id, OLD.amount,1,1 );
    CALL sp_delete_amount_from_concept( OLD.movement_id,OLD.category_id, OLD.amount,28,31 );
    END;

DROP TRIGGER IF EXISTS t_delete_amountfrommovement;
CREATE TRIGGER t_delete_amountfrommovement AFTER DELETE ON movement
  FOR EACH ROW
  BEGIN
  CALL sp_delete_amount_from_movement ( OLD.id, OLD.account_id, OLD.custom_date, OLD.type, 1,1);
    CALL sp_delete_amount_from_movement ( OLD.id, OLD.account_id, OLD.custom_date, OLD.type, 28,31);
END;
|
delimiter ;