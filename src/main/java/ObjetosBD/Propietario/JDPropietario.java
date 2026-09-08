package ObjetosBD.Propietario;

import ObjetosBD.Conexion;
import ObjetosBD.DataAccessException;
import Validation.Validaciones;
import java.sql.Connection;
import ObjetosBD.Persona.PersonaBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDPropietario {
    private final Conexion CN = new Conexion();

    public int insertarPropietario(int id_vivienda, int id_persona){
        Validaciones.id(id_vivienda, "id_vivienda");
        Validaciones.id(id_persona, "id_persona");
        String sql = "INSERT INTO propietario(id_vivienda, id_persona) VALUES (?, ?)";

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, id_vivienda);
            ps.setInt(2, id_persona);

            return ps.executeUpdate();

        }catch (SQLException e) {
            throw new DataAccessException("Error al insertar propietario", e);
        }
    }

    public PropietarioBD buscarPropietarioID(int id){
        Validaciones.id(id, "id");
        String SQL = "SELECT * FROM propietario WHERE id_vivienda = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, id);

            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idVivienda = RS.getInt("id_vivienda");
                    int idPersona = RS.getInt("id_persona");

                    if (RS.next()) throw new IllegalArgumentException("La vivienda tiene varios propietarios; selecciona la persona concreta");
                    return new PropietarioBD(idVivienda, idPersona);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR PROPIETARIO POR ID", e);
        }
        return null;
    }

    public PersonaBD buscarPropietarioNombre(String nombre){
        nombre = Validaciones.texto(nombre, "nombre", 100);
        String SQL = "SELECT p.* FROM persona p JOIN propietario pr ON p.id_persona = pr.id_persona WHERE p.per_nombre LIKE ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, "%" + nombre + "%");
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_persona");
                    String nombrePersona = RS.getString("per_nombre");
                    int familia = RS.getInt("id_familia");
                    int edad = RS.getInt("per_edad");

                    return new PersonaBD(id, nombrePersona, familia, edad);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR PROPIETARION POR NOMBRE", e);
        }
        return null;
    }

    public boolean actualizarPropietario(int idVivienda, int idPersona) {
        Validaciones.id(idPersona, "Nuevo propietario");
        PropietarioBD actual = buscarPropietarioID(idVivienda);
        return actual != null && actualizarPropietario(idVivienda, actual.getId_persona(), idPersona);
    }

    public boolean actualizarPropietario(int idVivienda, int personaAnterior, int nuevaPersona) {
        Validaciones.id(idVivienda, "Vivienda");
        Validaciones.id(personaAnterior, "Propietario anterior");
        Validaciones.id(nuevaPersona, "Nuevo propietario");
        String sql = "UPDATE propietario SET id_persona = ? WHERE id_vivienda = ? AND id_persona = ?";
        try (Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, nuevaPersona);
            ps.setInt(2, idVivienda);
            ps.setInt(3, personaAnterior);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar al propietario", e);
        }
    }

    public boolean eliminarPropietario(int idVivienda, int idPersona){
        Validaciones.id(idVivienda, "idVivienda");
        Validaciones.id(idPersona, "idPersona");
        String SQL = "DELETE FROM propietario WHERE id_vivienda = ? AND id_persona = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            PS.setInt(2, idPersona);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR PROPIETARIO", e);
        }
    }
}
