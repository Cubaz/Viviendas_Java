package ObjetosBD.Familia;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDFamilia {
    private Conexion CN = new Conexion();

    public int insertarFamilia(String apellidos) {
        String sql = "INSERT INTO familia (fam_apellidos) VALUES (?)";
        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, apellidos);
            int filas = ps.executeUpdate(); ///EJECUTA EL INSERT

            /// OBTIENE EL ID RECIÉN GENERADO DE FAMILIA
            if(filas >0) {
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE FAMILIA EXITOSO");
                    System.out.println("SU ID DE FAMILIA ES " + idGenerado);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar familia" + e.getMessage());
        }
        return -1;
    }

    public ObservableList<FamiliaBD> obtenerFamilias() {
        ObservableList<FamiliaBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM familia";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_familia");
                String apellidos = rs.getString("fam_apellidos");

                lista.add(new FamiliaBD(id, apellidos));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener familias: " + e.getMessage());
        }
        return lista;
    }

    public FamiliaBD buscarFamiliaID(int idFamilia){
        String SQL = "SELECT * FROM familia WHERE id_familia = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idFamilia);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String fam_apellidos = RS.getString("fam_apellidos");

                    return new FamiliaBD(idFamilia, fam_apellidos);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR AL BUSCAR FAMILIA POR ID: " + e.getMessage());
        }
        return null;
    }

    public FamiliaBD buscarFamiliaApellidos(String apellidosFamilia){
        String SQL = "SELECT * FROM familia WHERE fam_apellidos LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + apellidosFamilia + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_familia");
                    String apellidos = RS.getString("fam_apellidos");

                    return new FamiliaBD(id, apellidos);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR POR APELLIDOS: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarFamilia(int idFamilia, String apellidos){
        String SQL = "SELECT * FROM familia WHERE id_familia = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idFamilia);
            PS.setString(2, apellidos);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR FAMILIA: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarFamilia(int idFamilia){
        String SQL = "DELETE FROM familia WHERE id_familia = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idFamilia);
            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR FAMILIA: " + e.getMessage());
            return false;
        }
    }
}


