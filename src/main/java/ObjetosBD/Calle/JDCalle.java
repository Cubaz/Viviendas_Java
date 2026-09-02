package ObjetosBD.Calle;

import ObjetosBD.Conexion;
import ObjetosBD.Edificio.EdificioBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDCalle {
    private Conexion CN = new Conexion();

    public int insertarCalle(String nombreCalle, int IdColonia){
        String sql = "INSERT INTO calle (cal_nombre, id_colonia) VALUES(?, ?)";

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

    public ObservableList<CalleBD> obtenerCalle() {
        ObservableList<CalleBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM calle";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_calle");
                String nombre = rs.getString("cal_nombre");
                int idColonia = rs.getInt("id_colonia");

                lista.add(new CalleBD(id, nombre, idColonia));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener calles: " + e.getMessage());
        }
        return lista;
    }

    public CalleBD obtenerCalle(int idCalle) {
        CalleBD calle = null;
        String sql = "SELECT * FROM calle";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_calle");
                String nombre = rs.getString("cal_nombre");
                int idColonia = rs.getInt("id_colonia");

                calle = new CalleBD(id, nombre, idColonia);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener calles: " + e.getMessage());
        }

        return calle;
    }
}
