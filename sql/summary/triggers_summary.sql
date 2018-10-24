delimiter |
DROP TRIGGER IF EXISTS t_summary_insert;
CREATE TRIGGER t_summary_insert AFTER INSERT ON movement
  FOR EACH ROW
  BEGIN
    CALL sp_summary_add_substract(NEW.id, NEW.category_id, NEW.amount,NEW.type, NEW.custom_description,NEW.custom_date,NEW.duplicated,'PLUS');
  END;

DROP TRIGGER IF EXISTS t_summary_insert_from_concept;
CREATE TRIGGER t_summary_insert_from_concept AFTER INSERT ON concept
  FOR EACH ROW
  BEGIN
    CALL sp_summary_add_substract(NEW.movement_id, NEW.category_id, NEW.amount,null,NEW.description,null,null,'PLUS');
  END;

DROP TRIGGER IF EXISTS t_summary_update_from_movement;
CREATE TRIGGER t_summary_update_from_movement AFTER UPDATE ON movement
  FOR EACH ROW
  BEGIN
  
    CALL sp_summary_change_on_movement ( NEW.has_concepts,OLD.has_concepts,NEW.amount,OLD.amount,NEW.category_id,
          OLD.category_id,NEW.type,OLD.type,NEW.custom_date,OLD.custom_date,NEW.id,
          NEW.date_deleted, NEW.custom_description, OLD.custom_description,NEW.duplicated,OLD.duplicated);

  END;

  DROP TRIGGER IF EXISTS t_summary_update_from_concept;
CREATE TRIGGER t_summary_update_from_concept AFTER UPDATE ON concept
  FOR EACH ROW
  BEGIN
    CALL sp_summary_change_on_concept(NEW.amount,OLD.amount,NEW.category_id,NEW.movement_id,NEW.type,NEW.description);
  END;


DROP TRIGGER IF EXISTS t_summary_delete_from_concept;
CREATE TRIGGER t_summary_delete_from_concept AFTER DELETE ON concept
  FOR EACH ROW
  BEGIN
    CALL sp_summary_add_substract( OLD.movement_id, OLD.category_id, OLD.amount, null, OLD.description,null,null,'MINUS');
  END;
|
delimiter ;
