package ObjetosBD.Edificio;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDEdificio {
    private Conexion CN = new Conexion();

    public int insertarEdificio(String nombre){
        String sql = "INSERT INTO edificio(edi_nombre) VALUES (?)";
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

    public ObservableList<EdificioBD> obtenerEdificio() {
        ObservableList<EdificioBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM edificio";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_edificio");
                String nombre = rs.getString("edi_nombre");

                lista.add(new EdificioBD(id, nombre));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener edificios: " + e.getMessage());
        }
        return lista;
    }

    public EdificioBD buscarEdificioID(int idEdificio){
        String SQL = "SELECT * FROM edificio WHERE id_edificio = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String nombreEdi = RS.getString("edi_nombre");

                    return new EdificioBD(idEdificio, nombreEdi);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUCAR EDIFICIO POR ID: " + e.getMessage());
        }
        return null;
    }

    public EdificioBD buscarEdificioNombre(String nombreEdficio){
        String SQL = "SELECT * FROM edificio WHERE edi_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + nombreEdficio + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_edificio");
                    String nombre = RS.getString("edi_nombre");

                    return new EdificioBD(id, nombre);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR EDIFICIO POR NOMBRE: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarEdificio (int idEdificio, String nombreEdficio){
        String SQL = "UPDATE edificio SET edi_nombre = ? WHERE id_edificio = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, nombreEdficio);
            PS.setInt(2, idEdificio);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR EDIFICIO: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEdificio(int idEdificio){
        String SQL = "DELETE FROM edificio WHERE id_edificio = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR EDIFICIO: " + e.getMessage());
            return false;
        }
    }
}
