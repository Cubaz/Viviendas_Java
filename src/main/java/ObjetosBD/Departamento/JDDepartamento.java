package ObjetosBD.Departamento;
import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public DepartamentoBD buscarDepartamento(int idDepartamento){
        String SQL = "SELECT * FROM departamento WHERE id_departamento = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idDepartamento);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idEdificio = RS.getInt("id_edificio");
                    int idVivienda = RS.getInt("id_vivienda");
                    int piso = RS.getInt("dep_piso");

                    return new DepartamentoBD(idDepartamento, idEdificio, idVivienda, piso);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR DEPARTAMENTO " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarDepartamento(int idDepartamento, int idEdificio, int idVivienda, int piso){
        String SQL = "UPDATE departamento SET id_edificio = ?, id_vivienda = ?, dep_piso = ? WHERE id_departamento = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            PS.setInt(2, idVivienda);
            PS.setInt(3, piso);
            PS.setInt(4, idDepartamento);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR DEPARTAMENTO: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarDepartamento(int idDepartamento){
        String SQL = "DELETE FROM departamento WHERE id_departamento = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idDepartamento);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR DEPARTAMENTO: " + e.getMessage());
            return false;
        }
    }
}
