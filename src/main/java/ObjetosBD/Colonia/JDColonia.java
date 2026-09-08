package ObjetosBD.Colonia;
import ObjetosBD.Conexion;
import ObjetosBD.DataAccessException;
import Validation.Validaciones;
import java.sql.Connection;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDColonia {
    private final Conexion CN = new Conexion();

    public int insertarColonia(String nombre){
        nombre = Validaciones.texto(nombre, "nombre", 120);
        String sql = "INSERT INTO colonia (col_nombre) VALUES (?)";
        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,nombre);
            int filas = ps.executeUpdate();

            if(filas >0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error al insertar colonia", e);
        }
        return -1;
    }

    public ObservableList<ColoniaBD> obtenerColonias() {
        ObservableList<ColoniaBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM colonia";

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id_colonia");
                String nombre = rs.getString("col_nombre");

                ColoniaBD colonia = new ColoniaBD(id, nombre);
                colonia.setSup_construida(rs.getFloat("col_supconstruida"));
                lista.add(colonia);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener colonias", e);
        }
        return lista;
    }

    public ObservableList<ColoniaBD> buscarColoniaID(int idColonia){
        Validaciones.id(idColonia, "idColonia");
        ObservableList<ColoniaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM colonia WHERE id_colonia = ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setInt(1, idColonia);
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_colonia");
                    String nombre = RS.getString("col_nombre");
                    ColoniaBD colonia = new ColoniaBD(id, nombre);
                    colonia.setSup_construida(RS.getFloat("col_supconstruida"));
                    lista.add(colonia);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR COLONIAS POR NOMBRE", e);
        }

        return lista;
    }

    public ObservableList<ColoniaBD> buscarColoniaNombre(String nombreColonia){
        nombreColonia = Validaciones.texto(nombreColonia, "nombreColonia", 120);
        ObservableList<ColoniaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM colonia WHERE col_nombre LIKE ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setString(1, "%" + nombreColonia + "%");
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_colonia");
                    String nombre = RS.getString("col_nombre");
                    ColoniaBD colonia = new ColoniaBD(id, nombre);
                    colonia.setSup_construida(RS.getFloat("col_supconstruida"));
                    lista.add(colonia);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR COLONIAS POR NOMBRE", e);
        }

        return lista;
    }

    public boolean actualizarColonia(int idColonia, String nombreColonia) {
        Validaciones.id(idColonia, "Colonia");
        nombreColonia = Validaciones.texto(nombreColonia, "Nombre de colonia", 120);
        String sql = "UPDATE colonia SET col_nombre = ? WHERE id_colonia = ?";
        try (Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombreColonia);
            ps.setInt(2, idColonia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la colonia", e);
        }
    }

    public boolean actualizarColonia(int idColonia, String nombreColonia, float metrosCuadrados){
        Validaciones.id(idColonia, "idColonia");
        nombreColonia = Validaciones.texto(nombreColonia, "nombreColonia", 120);
        if (!Float.isFinite(metrosCuadrados) || metrosCuadrados < 0 || new java.math.BigDecimal(Float.toString(metrosCuadrados)).stripTrailingZeros().scale() > 2 || (double) metrosCuadrados > 9999999999.99) throw new IllegalArgumentException("La superficie debe ser un número no negativo válido");
        String SQL = "UPDATE colonia SET col_nombre = ?, col_supconstruida = ? WHERE id_colonia = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, nombreColonia);
            PS.setFloat(2, metrosCuadrados);
            PS.setInt(3, idColonia);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ACTUALIZAR COLONIA", e);
        }
    }

    public boolean eliminarColonia(int idColonia){
        Validaciones.id(idColonia, "idColonia");
        String SQL = "DELETE FROM colonia WHERE id_colonia = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idColonia);
            return  PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR COLONIA", e);
        }
    }
}
