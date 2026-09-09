DROP TRIGGER IF EXISTS bloquear_borrado_colonia;
CREATE TRIGGER bloquear_borrado_colonia BEFORE DELETE ON colonia
FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se permite eliminar registros superiores';

DROP TRIGGER IF EXISTS bloquear_borrado_familia;
CREATE TRIGGER bloquear_borrado_familia BEFORE DELETE ON familia
FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se permite eliminar registros superiores';

DROP TRIGGER IF EXISTS bloquear_borrado_edificio;
CREATE TRIGGER bloquear_borrado_edificio BEFORE DELETE ON edificio
FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se permite eliminar registros superiores';

DROP TRIGGER IF EXISTS bloquear_borrado_vivienda;
CREATE TRIGGER bloquear_borrado_vivienda BEFORE DELETE ON vivienda
FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se permite eliminar registros superiores';
