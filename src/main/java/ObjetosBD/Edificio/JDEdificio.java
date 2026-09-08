package ObjetosBD.Edificio;

import ObjetosBD.Conexion;
import ObjetosBD.DataAccessException;
import Validation.Validaciones;
import java.sql.Connection;
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
    private final Conexion CN = new Conexion();

    public int insertarEdificio(String nombre){
        nombre = Validaciones.texto(nombre, "nombre", 120);
        String sql = "INSERT INTO edificio(edi_nombre) VALUES (?)";
        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, nombre);
            int filas = ps.executeUpdate();

            if(filas > 0){
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error al insertar edificio", e);
        }
        return -1;
    }

    public ObservableList<EdificioBD> obtenerEdificio() {
        ObservableList<EdificioBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM edificio";

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_edificio");
                String nombre = rs.getString("edi_nombre");

                lista.add(new EdificioBD(id, nombre));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener edificios", e);
        }
        return lista;
    }

    public EdificioBD buscarEdificioID(int idEdificio){
        Validaciones.id(idEdificio, "idEdificio");
        String SQL = "SELECT * FROM edificio WHERE id_edificio = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String nombreEdi = RS.getString("edi_nombre");

                    return new EdificioBD(idEdificio, nombreEdi);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUCAR EDIFICIO POR ID", e);
        }
        return null;
    }

    public EdificioBD buscarEdificioNombre(String nombreEdficio){
        nombreEdficio = Validaciones.texto(nombreEdficio, "nombreEdficio", 120);
        String SQL = "SELECT * FROM edificio WHERE edi_nombre LIKE ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, "%" + nombreEdficio + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_edificio");
                    String nombre = RS.getString("edi_nombre");

                    return new EdificioBD(id, nombre);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR EDIFICIO POR NOMBRE", e);
        }
        return null;
    }

    public boolean actualizarEdificio (int idEdificio, String nombreEdficio){
        Validaciones.id(idEdificio, "idEdificio");
        nombreEdficio = Validaciones.texto(nombreEdficio, "nombreEdficio", 120);
        String SQL = "UPDATE edificio SET edi_nombre = ? WHERE id_edificio = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, nombreEdficio);
            PS.setInt(2, idEdificio);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ACTUALIZAR EDIFICIO", e);
        }
    }

    public boolean eliminarEdificio(int idEdificio){
        Validaciones.id(idEdificio, "idEdificio");
        String SQL = "DELETE FROM edificio WHERE id_edificio = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR EDIFICIO", e);
        }
    }

    public ObservableList<Map<String, Object>> buscarEdificios(Integer idEdificio, String nombre) {
        if (idEdificio != null) Validaciones.id(idEdificio, "idEdificio");
        if (nombre != null && !nombre.isEmpty()) nombre = Validaciones.texto(nombre, "nombre", 120);
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();
        String sentencia = construirSentenciaBuscarEdificios(idEdificio, nombre);

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sentencia)) {
            int index = 1;
            if (idEdificio != null) {
                ps.setInt(index++, idEdificio);
            }
            if (nombre != null && !nombre.isEmpty()) {
                ps.setString(index++, "%" + nombre + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("IdEdificio", rs.getInt("id_edificio"));
                    fila.put("Nombre", rs.getString("edi_nombre"));
                    datosEncontrados.add(fila);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al consultar edificio", e);
        }
        return datosEncontrados;
    }

    public String construirSentenciaBuscarEdificios(Integer idEdificio, String nombre) {
        if (idEdificio != null) Validaciones.id(idEdificio, "idEdificio");
        if (nombre != null && !nombre.isEmpty()) nombre = Validaciones.texto(nombre, "nombre", 120);
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
