package ObjetosBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JDFamilia {

    private Conexion CN = new Conexion();

    public int insertarFamilia(String apellidos) {
        String sql = "INSERT INTO familia (apellidos) VALUES (?)";
        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, apellidos);
            int filas = ps.executeUpdate(); ///EJECUTA EL INSERT

            /// OBTIENE EL ID RECIÉN GENERADO DE FAMILIA
            if(filas >0){
                var rs=ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE FAMILIA EXITOSO");
                    System.out.println("SU ID DE FAMILIA ES "+ idGenerado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar familia" + e.getMessage());
        }
        return -1;
    }
}


