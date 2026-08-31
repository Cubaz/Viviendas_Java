package ObjetosBD.Calle;

import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JDCalle {
    private Conexion CN = new Conexion();

    public int insertarCalle(String nombreCalle, int IdColonia){
        String sql = "INSERT INTO calle (nombreCalle, IdColonia) VALUES(?, ?)";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, nombreCalle);
            ps.setInt(2, IdColonia);

            int filas = ps.executeUpdate();

            if(filas > 0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE CALLE EXITOSO");
                    System.out.println("SU ID DE CALLE ES: " + idGenerado);
                }
            }
        }catch (SQLException e){
            System.out.println("Error al insertar calle: " + e.getMessage());
        }
        return -1;
    }
}
