package ObjetosBD.Edificio;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    return idGenerado;
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

    public ObservableList<Map<String, Object>> buscarEdificios(Integer idEdificio, String nombre) {
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();
        String sentencia = construirSentenciaBuscarEdificios(idEdificio, nombre);

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sentencia)) {
            int index = 1;
            if (idEdificio != null) {
                ps.setInt(index++, idEdificio);
            }
            if (nombre != null && !nombre.isEmpty()) {
                ps.setString(index++, "%" + nombre + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("IdEdificio", rs.getInt("id_edificio"));
                fila.put("Nombre", rs.getString("edi_nombre"));
                datosEncontrados.add(fila);
            }
            if (!datosEncontrados.isEmpty()) return datosEncontrados;
        } catch (SQLException e) {
            System.out.println("Error al consultar edificio: " + e.getMessage());
        }
        return null;
    }

    public String construirSentenciaBuscarEdificios(Integer idEdificio, String nombre) {
        StringBuilder sentencia = new StringBuilder("SELECT id_edificio, edi_nombre FROM edificio");
        List<String> condiciones = new ArrayList<>();
        if (idEdificio != null) condiciones.add("id_edificio = ?");
        if (nombre != null && !nombre.isEmpty()) condiciones.add("edi_nombre LIKE ?");

        if (!condiciones.isEmpty()) {
            sentencia.append(" WHERE ").append(String.join(" AND ", condiciones));
        }
        return sentencia.toString();
    }
}
