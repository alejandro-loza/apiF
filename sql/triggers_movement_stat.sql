delimiter |
DROP TRIGGER IF EXISTS t_insert_amount;
CREATE TRIGGER t_insert_amount AFTER INSERT ON movement
  FOR EACH ROW
  BEGIN
  CALL sp_insert_amount(NEW.id, NEW.category_id, NEW.amount, 1, 1);#This call is for only one day


 END;

DROP TRIGGER IF EXISTS t_insert_amount_from_concept;
CREATE TRIGGER t_insert_amount_from_concept AFTER INSERT ON concept
  FOR EACH ROW
  BEGIN
  CALL sp_insert_amount (NEW.movement_id, NEW.category_id, NEW.amount, 1, 1);#This call is for only one day
  END;
DROP TRIGGER IF EXISTS t_update_amount_from_movement;
CREATE TRIGGER t_update_amount_from_movement AFTER UPDATE ON movement
  FOR EACH ROW
  BEGIN
  CALL sp_change_on_movement ( NEW.has_concepts,OLD.has_concepts,NEW.amount,OLD.amount,NEW.category_id,
                    OLD.category_id,NEW.type,OLD.type,NEW.custom_date,OLD.custom_date,NEW.account_id,NEW.id,NEW.date_deleted,1,1);

  END;

  DROP TRIGGER IF EXISTS t_update_amount_from_concept;
CREATE TRIGGER t_update_amount_from_concept AFTER UPDATE ON concept
  FOR EACH ROW
  BEGIN
  CALL sp_change_on_concept (NEW.amount,OLD.amount,NEW.category_id, NEW.movement_id,NEW.type, 1, 1);#This call is for only one day

  END;

DROP TRIGGER IF EXISTS t_delete_amount;
CREATE TRIGGER t_delete_amount AFTER DELETE ON concept
  FOR EACH ROW
  BEGIN
    CALL sp_delete_amount_from_concept( OLD.movement_id,OLD.category_id, OLD.amount,1,1 );
   
    END;
|
delimiter ;