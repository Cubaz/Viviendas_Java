package ObjetosBD.Edificio;

import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JDEdificio {
    private Conexion CN = new Conexion();

    public int insertarEdificio(String nombre){
        String sql = "INSERT INTO edificio(nombre) VALUES (?)";
        try(PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, nombre);
            int filas = ps.executeUpdate();

            if(filas > 0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE EDIFICIO EXITOSO");
                    System.out.println("SU ID DE EDIFICIO ES: " + idGenerado);
                }
            }
        }catch (SQLException e){
            System.out.println("Error al insertar edificio: " + e.getMessage());
        }
        return -1;
    }
}
