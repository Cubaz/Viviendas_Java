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
}


