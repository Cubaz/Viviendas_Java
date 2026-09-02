package ObjetosBD.Persona;

import ObjetosBD.Conexion;
import ObjetosBD.Edificio.EdificioBD;
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

    public PersonaBD obtenerPersona(int idPersona) {
        PersonaBD persona = null;
        String sql = "SELECT * FROM persona WHERE id_persona = " + idPersona;

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_persona");
                String nombre = rs.getString("per_nombre");
                int familia = rs.getInt("id_familia");
                int edad = rs.getInt("per_edad");

                persona = new PersonaBD(id, nombre, familia, edad);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener persona: " + e.getMessage());
        }

        return persona;
    }
}
