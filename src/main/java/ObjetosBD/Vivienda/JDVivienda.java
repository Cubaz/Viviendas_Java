package ObjetosBD.Vivienda;

import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDVivienda {
    private Conexion CN = new Conexion();

    public int insertarVivienda(String tipo, int habitantes, int num_ext, int num_int, int id_calle, float mts_cuadrados){
        String sql = "INSERT INTO vivienda (viv_tipo, viv_habitantes, viv_numExt, viv_numInt, id_calle, viv_mtscuadrados) VALUES (?,?,?,?,?,?)";
        try(PreparedStatement ps= CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, tipo);
            ps.setInt(2, habitantes);
            ps.setInt(3, num_ext);
            ps.setInt(4, num_int);
            ps.setInt(5, id_calle);
            ps.setFloat(6, mts_cuadrados);
            int filas = ps.executeUpdate();
            if(filas >0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE VIVIENDA EXITOSO");
                    System.out.println("Su ID de vivienda es" + idGenerado);
                    return idGenerado;
                }
            }

        }catch(SQLException e){
            System.out.println("ERROR AL INSERTAR VIVIENDA" + e.getMessage());
        }
        return -1;
    }

    public ViviendaBD buscarVivienda(int idVivienda){
        String SQL="SELECT * FROM vivienda WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    return new ViviendaBD(idVivienda, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarVivienda(int idVivienda, String tipo, int habitantes, int numExterior, int numInterior, int idCalle, float metros){
        String SQL = "UPDATE vivienda SET viv_tipo = ?, viv_habitantes = ?, viv_numExt = ?, viv_numInt = ?, id_calle = ?, viv_metroscuadrados = ? WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, tipo);
            PS.setInt(2, habitantes);
            PS.setInt(3, numExterior);
            PS.setInt(4, numInterior);
            PS.setInt(5, idCalle);
            PS.setFloat(6, metros);
            PS.setInt(7, idVivienda);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarVivienda(int idVivienda){
        String SQL = "DELETE FROM vivienda WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR VIVIENDA: " + e.getMessage());
            return false;
        }
    }

}
