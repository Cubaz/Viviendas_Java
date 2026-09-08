package ObjetosBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Cada operación es propietaria de su conexión y debe cerrarla con try-with-resources. */
public class Conexion implements AutoCloseable {
    private Connection cnx;

    public Connection getConexion() throws SQLException {
        if (cnx == null || cnx.isClosed()) {
            cnx = DriverManager.getConnection(
                    configuracion("viviendas.db.url", "VIVIENDAS_DB_URL",
                            "jdbc:mysql://localhost:3306/viviendas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=10000"),
                    configuracion("viviendas.db.user", "VIVIENDAS_DB_USER", "root"),
                    configuracion("viviendas.db.password", "VIVIENDAS_DB_PASSWORD", "root"));
        }
        return cnx;
    }

    private static String configuracion(String propiedad, String entorno, String defecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null) valor = System.getenv(entorno);
        return valor == null ? defecto : valor;
    }

    @Override
    public void close() throws SQLException {
        if (cnx != null) { cnx.close(); cnx = null; }
    }

    public static void main(String[] args) throws SQLException {
        try (var conexion = new Conexion(); var connection = conexion.getConexion()) {
            System.out.println(connection.isValid(5) ? "Conexión exitosa" : "No se pudo conectar a la BD");
        }
    }
}
