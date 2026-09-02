package ObjetosBD.Vivienda;

import ObjetosBD.Conexion;
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
            System.out.println("ERROR AL INSERTAR VIVIENDA" + e);
        }
        return -1;
    }

    public ViviendaBD obtenerVivienda(int idVivienda) {
        ViviendaBD vivienda = null;
        String sql = "SELECT * FROM vivienda WHERE id_vivienda = " + idVivienda;

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_vivienda");
                String tipo = rs.getString("viv_tipo");
                int habitantes = rs.getInt("viv_habitantes");
                int exte = rs.getInt("viv_numExt");
                int inte = rs.getInt("viv_numInt");
                int id_calle = rs.getInt("id_calle");
                int mts_cuadrados = rs.getInt("viv_mtscuadrados");

                vivienda = new ViviendaBD(id, tipo, habitantes, exte, inte, id_calle, mts_cuadrados);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener persona: " + e.getMessage());
        }

        return vivienda;
    }

}
