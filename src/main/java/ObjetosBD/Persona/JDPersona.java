package ObjetosBD.Persona;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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

    public ObservableList<Map<String, Object>> buscarPersonas(Integer idPersona, String nombre, Integer idFamilia, Integer edad){
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();

        String sentencia = construirSentenciaBuscarPersonas(idPersona, nombre, idFamilia, edad);

        try(PreparedStatement ps = CN.getConexion().prepareStatement(sentencia)){
            int index =  1;
            if(idPersona != null){
                ps.setInt(index++, idPersona);
            }
            if(nombre != null && !nombre.isEmpty()){
                ps.setString(index++, "%" + nombre + "%");
            }
            if(idFamilia != null){
                ps.setInt(index++, idFamilia);
            }
            if(edad != null){
                ps.setInt(index++, edad);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Map<String, Object> fila = new HashMap<>();
                fila.put("IdPersona", rs.getInt("id_persona"));
                fila.put("Nombre", rs.getString("per_nombre"));
                fila.put("Edad", rs.getInt("per_edad"));
                fila.put("IdFamilia", rs.getInt("id_familia"));
                fila.put("ApellidosFamilia", rs.getString("fam_apellidos"));
                datosEncontrados.add(fila);
            }

            if(!datosEncontrados.isEmpty()) return datosEncontrados;
        }
        catch(SQLException e){
            System.out.println("Error al consultar persona: " + e.getMessage());
        }
        return null;
    }

    public String construirSentenciaBuscarPersonas(Integer idPersona, String nombre, Integer idFamilia, Integer edad){
        StringBuilder sentencia = new StringBuilder(
                """
                        SELECT
                             persona.id_persona,
                             persona.per_nombre,
                             persona.per_edad,
                             familia.id_familia,
                             familia.fam_apellidos
                        FROM persona
                             JOIN familia ON persona.id_familia = familia.id_familia""");

        boolean hasCondition = false;
        if(idPersona != null || (nombre != null && !nombre.isEmpty()) || idFamilia != null || edad != null) {
            sentencia.append("\nWHERE ");
            hasCondition = true;
        }

        if(hasCondition) {
            StringBuilder cond = new StringBuilder();
            if(idPersona != null) cond.append("persona.id_persona = ? ");
            if(nombre != null && !nombre.isEmpty()) {
                if(!cond.isEmpty()) cond.append("AND ");
                cond.append("persona.per_nombre LIKE ? ");
            }
            if(idFamilia != null) {
                if(!cond.isEmpty()) cond.append("AND ");
                cond.append("persona.id_familia = ? ");
            }
            if(edad != null) {
                if(!cond.isEmpty()) cond.append("AND ");
                cond.append("persona.per_edad = ? ");
            }
            sentencia.append(cond);
        }

        sentencia.append("\nGROUP BY persona.id_persona");

        return sentencia.toString();
    }
}
