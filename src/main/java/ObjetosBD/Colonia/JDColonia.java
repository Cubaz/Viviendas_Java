package ObjetosBD.Colonia;
import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JDColonia {
    private Conexion CN = new Conexion();

    public int insertarColonia(String col_nombre){
        String sql = "INSERT INTO colonia (col_nombre) VALUES (?)";
        try(PreparedStatement ps=CN.getConexion().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,col_nombre);
            int filas = ps.executeUpdate();

            if(filas >0) {
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE COLONIA EXITOSO");
                    System.out.println("SU ID DE COLONIA ES " + idGenerado);
                }
            }
        }catch (SQLException e){
            System.out.println("Error al insertar colonia" + e.getMessage());
        }
        return -1;
    }
}
