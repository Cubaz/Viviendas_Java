package ObjetosBD.Familia;

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

public class JDFamilia {
    private final Conexion CN = new Conexion();

    public int insertarFamilia(String apellidos) {
        apellidos = Validaciones.texto(apellidos, "apellidos", 120);
        String sql = "INSERT INTO familia (fam_apellidos) VALUES (?)";
        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, apellidos);
            int filas = ps.executeUpdate(); ///EJECUTA EL INSERT

            /// OBTIENE EL ID RECIÉN GENERADO DE FAMILIA
            if(filas >0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al insertar familia", e);
        }
        return -1;
    }

    public ObservableList<FamiliaBD> obtenerFamilias() {
        ObservableList<FamiliaBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM familia";

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_familia");
                String apellidos = rs.getString("fam_apellidos");

                lista.add(new FamiliaBD(id, apellidos));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener familias", e);
        }
        return lista;
    }

    public FamiliaBD buscarFamiliaID(int idFamilia){
        Validaciones.id(idFamilia, "idFamilia");

        String SQL = "SELECT * FROM familia WHERE id_familia = ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setInt(1, idFamilia);
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_familia");
                    String apellido = RS.getString("fam_apellidos");
                    return new FamiliaBD(id, apellido);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR FAMILIAS POR ID", e);
        }

        return null;
    }

    public ObservableList<FamiliaBD> buscarFamiliaIDTABLA(int idFamilia){
        Validaciones.id(idFamilia, "idFamilia");
        ObservableList<FamiliaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM familia WHERE id_familia = ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setInt(1, idFamilia);
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_familia");
                    String apellido = RS.getString("fam_apellidos");
                    lista.add(new FamiliaBD(id, apellido));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR FAMILIAS POR ID", e);
        }

        return lista;
    }

    public ObservableList<FamiliaBD> buscarFamiliaApellidos(String apellidos){
        apellidos = Validaciones.texto(apellidos, "apellidos", 120);
        ObservableList<FamiliaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM familia WHERE fam_apellidos LIKE ?";

        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)) {
            PS.setString(1, "%" + apellidos + "%");
            try (ResultSet RS = PS.executeQuery()) {
                while (RS.next()) {
                    int id = RS.getInt("id_familia");
                    String apellido = RS.getString("fam_apellidos");
                    lista.add(new FamiliaBD(id, apellido));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR FAMILIAS POR NOMBRE", e);
        }

        return lista;
    }

    public boolean actualizarFamilia(int idFamilia, String apellidos){
        Validaciones.id(idFamilia, "idFamilia");
        apellidos = Validaciones.texto(apellidos, "apellidos", 120);
        String SQL = "UPDATE familia SET fam_apellidos = ? WHERE id_familia = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, apellidos);
            PS.setInt(2, idFamilia);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ACTUALIZAR FAMILIA", e);
        }
    }

    public boolean eliminarFamilia(int idFamilia){
        Validaciones.id(idFamilia, "idFamilia");
        String SQL = "DELETE FROM familia WHERE id_familia = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idFamilia);
            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR FAMILIA", e);
        }
    }
}

