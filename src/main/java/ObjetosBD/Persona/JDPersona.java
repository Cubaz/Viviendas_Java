package ObjetosBD.Persona;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDPersona {
    private Conexion CN = new Conexion();

    public int insertarPersona(String nombrePersona, int IdFamilia, int edad){
        String sql = "INSERT INTO persona(per_nombre, id_familia, per_edad) VALUES (?, ?, ?)";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, nombrePersona);
            ps.setInt(2, IdFamilia);
            ps.setInt(3, edad);

            int filas = ps.executeUpdate();

            if(filas > 0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE PERSONA EXITOSO");
                    System.out.println("SU ID DE PERSONA ES: " + idGenerado);
                    return idGenerado;
                }
            }
        }catch (SQLException e){
            System.out.println("Error al insertar persona: " + e.getMessage());
        }
        return -1;
    }

    public ObservableList<PersonaBD> obtenerPersona() {
        ObservableList<PersonaBD> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM persona";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_persona");
                String nombre = rs.getString("per_nombre");
                int familia = rs.getInt("id_familia");
                int edad = rs.getInt("per_edad");

                lista.add(new PersonaBD(id, nombre, familia, edad));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener persona: " + e.getMessage());
        }
        return lista;
    }

    public PersonaBD buscarPersonaID(int idPersona){
        String SQL = "SELECT * FROM persona WHERE id_persona = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idPersona);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String nombre = RS.getString("per_nombre");
                    int familia = RS.getInt("id_familia");
                    int edad = RS.getInt("per_edad");

                    return new PersonaBD(idPersona, nombre, familia, edad);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR POR ID: " + e.getMessage());
        }
        return null;
    }

    public PersonaBD buscarPersonaNombre(String nombrePersona){
        String SQL = "SELECT * FROM persona WHERE per_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + nombrePersona + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_persona");
                    String nombre = RS.getString("per_nombre");
                    int familia = RS.getInt("id_familia");
                    int edad = RS.getInt("per_edad");

                    return new PersonaBD(id, nombre, familia, edad);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR POR NOMBRE: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarPersona(int idPersona, String nombrePersona, int idFamilia, int edad){
        String SQL = "UPDATE persona SET per_nombre = ?, id_familia = ?, per_edad = ? WHERE id_persona = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, nombrePersona);
            PS.setInt(2, idFamilia);
            PS.setInt(3, edad);
            PS.setInt(4, idPersona);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR PERSONA: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPersona(int idPersona){
        String SQL = "DELETE FROM persona WHERE id_persona = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idPersona);
            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR PERSONA: " + e.getMessage());
            return false;
        }
    }
}
