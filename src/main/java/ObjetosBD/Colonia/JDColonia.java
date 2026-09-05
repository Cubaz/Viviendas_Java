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

    public ColoniaBD buscarColoniaID(int idColonia){
        String SQL = "SELECT * FROM colonia WHERE id_colonia = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idColonia);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String nombre = RS.getString("col_nombre");

                    return new ColoniaBD(idColonia, nombre);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR COLONIA POR ID: " + e.getMessage());
        }
        return null;
    }

    public ColoniaBD buscarColoniaNombre(String nombreColonia){
        String SQL = "SELECT * FROM colonia WHERE col_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + nombreColonia + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_colonia");
                    String nombre = RS.getString("col_nombre");

                    return new ColoniaBD(id, nombre);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR COLONIA POR NOMBRE: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarColonia(int idColonia, String nombreColonia, float metrosCuadrados){
        String SQL = "UPDATE colonia SET col_nombre = ?, col_supconstruida = ? WHERE id_colonia = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, nombreColonia);
            PS.setFloat(2, metrosCuadrados);
            PS.setInt(3, idColonia);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR COLONIA: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarColonia(int idColonia){
        String SQL = "DELETE FROM colonia WHERE id_colonia = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idColonia);
            return  PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR COLONIA: " + e.getMessage());
            return false;
        }
    }
}
