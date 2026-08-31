package ObjetosBD.Edificio;

import ObjetosBD.Conexion;
import ObjetosBD.Familia.FamiliaBD;
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
}
