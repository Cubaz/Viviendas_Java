package ObjetosBD;

import java.sql.SQLException;

/** Conserva la causa técnica y ofrece un mensaje recuperable para la interfaz. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String operacion, SQLException causa) {
        super(mensaje(operacion, causa), causa);
    }

    private static String mensaje(String operacion, SQLException causa) {
        String detalle;
        if (causa.getErrorCode() == 1062) detalle = "Ya existe un registro con esa combinación de datos.";
        else if (causa.getErrorCode() == 1451)
            detalle = "No se puede eliminar porque tiene datos relacionados. Elimine primero las relaciones hijas.";
        else if (causa.getErrorCode() == 1452) detalle = "Uno de los registros seleccionados ya no existe. Actualice la selección.";
        else if ((causa.getSQLState() != null && causa.getSQLState().startsWith("08")) || causa.getErrorCode() == 1045 || causa.getErrorCode() == 1049)
            detalle = "No se pudo conectar a MySQL. Revise que esté iniciado y que la base y las credenciales sean correctas.";
        else detalle = "No se pudo completar la operación en la base de datos.";
        return operacion + ": " + detalle;
    }
}
