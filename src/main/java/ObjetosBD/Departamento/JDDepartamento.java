package ObjetosBD.Departamento;
import ObjetosBD.Conexion;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JDDepartamento {

    private Conexion CN= new Conexion();

    public int insertarDepartamento(int id_edificio, int id_vivienda, int piso){
        String sql = "INSERT INTO departamento (id_edificio, id_vivienda, dep_piso) VALUES (?,?,?)";
        try(PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, id_edificio);
            ps.setInt(2, id_vivienda);
            ps.setInt(3, piso);

            int filas = ps.executeUpdate();
            if(filas >0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE DEPARTAMENTO EXITOSO");
                    System.out.println("Su ID de departamento es" + idGenerado);
                    return idGenerado;
                }
            }

        }catch(SQLException e){
            System.out.println("ERROR AL INSERTAR DEPARTAMENTO" + e);
        }
        return -1;
    }

}
