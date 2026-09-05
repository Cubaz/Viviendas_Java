package ObjetosBD.Vivienda;

import ObjetosBD.Conexion;

import javax.swing.table.DefaultTableModel;
import java.awt.desktop.SystemSleepEvent;
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

//    public DefaultTableModel buscarVivienda(int criterio, String parametro){
//        String SQL;
//        if(criterio == 0){
//            SQL = "SELECT * FROM vivienda WHERE idVivienda like" + parametro + "%";
//        }else{
//            SQL = "SELECT * FROM propietario pr, persona pe WHERE (pr.id_persona = pe.id_persona) AND (pe.per_nombre=" + parametro + ")";
//        }
//
//        try{
//            PS = CN.getConexion().prepareStatement(SQL);
//            RS = PS.executeQuery();
//
//
//        } catch (SQLException e) {
//            System.out.println("ERROR AL BUSCAR VIVIENDA "+ e.getMessage());
//        }
//    }

}
