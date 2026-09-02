package ObjetosBD.Colonia;
import ObjetosBD.Conexion;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDColonia {
    private Conexion CN = new Conexion();

    public int insertarColonia(String nombre){
        String sql = "INSERT INTO colonia (col_nombre) VALUES (?)";
        try(PreparedStatement ps=CN.getConexion().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,nombre);
            int filas = ps.executeUpdate();

            if(filas >0) {
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE COLONIA EXITOSO");
                    System.out.println("SU ID DE COLONIA ES " + idGenerado);
                }
            }
        }catch (SQLException e){
            System.out.println("Error al insertar colonia" + e.getMessage());
        }
        return -1;
    }

    public ObservableList<ColoniaBD> obtenerColonias() {
        ObservableList<ColoniaBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM colonia"; // Ajusta 'colonia' si tu tabla se llama distinto

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Ajusta 'id_colonia' y 'Nombre' según los nombres exactos en tu MySQL
                int id = rs.getInt("id_colonia");
                String nombre = rs.getString("col_nombre");

                lista.add(new ColoniaBD(id, nombre));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener colonias: " + e.getMessage());
        }
        return lista;
    }
}
