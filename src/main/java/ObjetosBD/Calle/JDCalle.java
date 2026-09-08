package ObjetosBD.Calle;

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

public class JDCalle {
    private final Conexion CN = new Conexion();

    public int insertarCalle(String nombreCalle, int IdColonia){
        nombreCalle = Validaciones.texto(nombreCalle, "nombreCalle", 120);
        Validaciones.id(IdColonia, "IdColonia");
        String sql = "INSERT INTO calle (cal_nombre, id_colonia) VALUES(?, ?)";

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, nombreCalle);
            ps.setInt(2, IdColonia);

            int filas = ps.executeUpdate();

            if(filas > 0){
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error al insertar calle", e);
        }
        return -1;
    }

    public ObservableList<CalleBD> obtenerCalle() {
        ObservableList<CalleBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM calle";

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_calle");
                String nombre = rs.getString("cal_nombre");
                int idColonia = rs.getInt("id_colonia");

                lista.add(new CalleBD(id, nombre, idColonia));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener calles", e);
        }
        return lista;
    }

    public CalleBD buscarCalleID(int idCalle){
        Validaciones.id(idCalle, "idCalle");
        String SQL = "SELECT * FROM calle WHERE id_calle = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setInt(1, idCalle);
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_calle");
                    String nombre = RS.getString("cal_nombre");
                    int colonia = RS.getInt("id_colonia");
                    return new CalleBD(id, nombre, colonia);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR CALLES POR ID", e);
        }

        return null;
    }

    public ObservableList<CalleBD> buscarCalleIDTABLA(int idCalle){
        Validaciones.id(idCalle, "idCalle");
        ObservableList<CalleBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM calle WHERE id_calle = ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setInt(1, idCalle);
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_calle");
                    String nombre = RS.getString("cal_nombre");
                    int colonia = RS.getInt("id_colonia");
                    lista.add(new CalleBD(id, nombre, colonia));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR CALLES POR ID", e);
        }

        return lista;
    }

    public ObservableList<CalleBD> buscarCalleaNombre(String nombreCalle){
        nombreCalle = Validaciones.texto(nombreCalle, "nombreCalle", 120);
        ObservableList<CalleBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM calle WHERE cal_nombre LIKE ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setString(1, "%" + nombreCalle + "%");
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_calle");
                    String nombre = RS.getString("cal_nombre");
                    int colonia = RS.getInt("id_colonia");
                    lista.add(new CalleBD(id, nombre, colonia));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR CALLES POR NOMBRE", e);
        }

        return lista;
    }

    public boolean actualizarCalle(int idCalle, String nombre, int IdColonia){
        Validaciones.id(idCalle, "idCalle");
        nombre = Validaciones.texto(nombre, "nombre", 120);
        Validaciones.id(IdColonia, "IdColonia");
        String SQL = "UPDATE calle SET cal_nombre = ?, id_colonia = ? WHERE id_calle = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, nombre);
            PS.setInt(2, IdColonia);
            PS.setInt(3, idCalle);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ACTUALIZAR CALLE", e);
        }
    }

    public boolean eliminarCalle(int idCalle){
        Validaciones.id(idCalle, "idCalle");
        String SQL = "DELETE FROM calle WHERE id_calle = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idCalle);
            return PS.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR CALLE", e);
        }
    }
}
