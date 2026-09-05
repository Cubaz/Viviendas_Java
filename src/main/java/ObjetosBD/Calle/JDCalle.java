package ObjetosBD.Calle;

import ObjetosBD.Conexion;
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

    public CalleBD buscarCalleID(int idCalle){
        String SQL = "SELECT * FROM calle WHERE id_calle = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idCalle);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String calleNombre = RS.getString("cal_nombre");
                    int idColonia = RS.getInt("id_colonia");

                    return new CalleBD(idCalle, calleNombre, idColonia);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR CALLE POR ID: " + e.getMessage());
        }
        return null;
    }

    public CalleBD buscarCalleNombre(String nombreCalle){
        String SQL = "SELECT * FROM calle WHERE cal_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + nombreCalle + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_calle");
                    String nombre = RS.getString("cal_nombre");
                    int idColonia = RS.getInt("id_colonia");

                    return new CalleBD(id, nombre, idColonia);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR CALLE POR NOMBRE: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarCalle(int idCalle, String nombre, int IdColonia){
        String SQL = "UPDATE calle SET cal_nombre = ?, id_colonia = ? WHERE id_calle = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, nombre);
            PS.setInt(2, IdColonia);
            PS.setInt(3, idCalle);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR CALLE: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCalle(int idCalle){
        String SQL = "DELETE FROM calle WHERE id_calle = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idCalle);
            return PS.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ERROR AL ELIMINAR CALLE: " + e.getMessage());
            return false;
        }
    }
}
