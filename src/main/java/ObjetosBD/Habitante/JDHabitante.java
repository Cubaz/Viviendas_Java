package ObjetosBD.Habitante;

import ObjetosBD.Conexion;
import ObjetosBD.DataAccessException;
import Validation.Validaciones;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JDHabitante {
    private final Conexion conexion = new Conexion();

    public boolean insertarHabitante(int idPersona, int idVivienda, String rol){
        Validaciones.id(idPersona, "idPersona");
        Validaciones.id(idVivienda, "idVivienda");
        rol = Validaciones.texto(rol, "rol", 40);
        String sentencia = "INSERT INTO habitantes (id_persona, id_vivienda, hab_rol) VALUES (?, ?, ?)";

        try(Connection c = conexion.getConexion(); PreparedStatement ps = c.prepareStatement(sentencia)){
            ps.setInt(1, idPersona);
            ps.setInt(2, idVivienda);
            ps.setString(3, rol);
            int filas = ps.executeUpdate();

            if(filas > 0){
                return true;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error al insertar habitante", e);
        }

        return false;
    }

    public HabitanteBD buscarHabitante(int idPersona) {
        Validaciones.id(idPersona, "Persona");
        String sql = "SELECT * FROM habitantes WHERE id_persona = ?";
        try (Connection c = conexion.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    HabitanteBD habitante = new HabitanteBD(rs.getInt("id_persona"), rs.getInt("id_vivienda"), rs.getString("hab_rol"));
                    if (rs.next()) throw new IllegalArgumentException("La persona habita varias viviendas; selecciona la vivienda concreta");
                    return habitante;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar al habitante", e);
        }
    }

    public HabitanteBD buscarHabitante(int idPersona, int idVivienda) {
        Validaciones.id(idPersona, "Persona");
        Validaciones.id(idVivienda, "Vivienda");
        String sql = "SELECT * FROM habitantes WHERE id_persona = ? AND id_vivienda = ?";
        try (Connection c = conexion.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setInt(2, idVivienda);
            try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? new HabitanteBD(idPersona, idVivienda, rs.getString("hab_rol")) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar al habitante en la vivienda", e);
        }
    }

    public boolean actualizarHabitante(int idPersona, int nuevoIdVivienda, String nuevoRol) {
        Validaciones.id(nuevoIdVivienda, "Nueva vivienda");
        nuevoRol = Validaciones.texto(nuevoRol, "Rol", 40);
        HabitanteBD actual = buscarHabitante(idPersona);
        return actual != null && actualizarHabitante(idPersona, actual.getIdVivienda(), nuevoIdVivienda, nuevoRol);
    }

    public boolean actualizarHabitante(int idPersona, int viviendaAnterior, int nuevaVivienda, String rol) {
        Validaciones.id(idPersona, "Persona");
        Validaciones.id(viviendaAnterior, "Vivienda anterior");
        Validaciones.id(nuevaVivienda, "Nueva vivienda");
        rol = Validaciones.texto(rol, "Rol", 40);
        String sql = "UPDATE habitantes SET id_vivienda = ?, hab_rol = ? WHERE id_persona = ? AND id_vivienda = ?";
        try (Connection c = conexion.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, nuevaVivienda);
            ps.setString(2, rol);
            ps.setInt(3, idPersona);
            ps.setInt(4, viviendaAnterior);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar al habitante", e);
        }
    }

    public boolean borrarHabitante(int idPersona) {
        HabitanteBD actual = buscarHabitante(idPersona);
        return actual != null && borrarHabitante(idPersona, actual.getIdVivienda());
    }

    public boolean borrarHabitante(int idPersona, int idVivienda) {
        Validaciones.id(idPersona, "Persona");
        Validaciones.id(idVivienda, "Vivienda");
        String sql = "DELETE FROM habitantes WHERE id_persona = ? AND id_vivienda = ?";
        try (Connection c = conexion.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setInt(2, idVivienda);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar al habitante de la vivienda", e);
        }
    }

    public ObservableList<Map<String, Object>> buscarHabitantes(Integer idPersona, Integer idVivienda, String rol){
        if (idPersona != null) Validaciones.id(idPersona, "idPersona");
        if (idVivienda != null) Validaciones.id(idVivienda, "idVivienda");
        if (rol != null && !rol.isEmpty()) rol = Validaciones.texto(rol, "rol", 40);
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();

        String sentencia = construirSentenciaBuscarHabitantes(idPersona, idVivienda, rol);

        try(Connection c = conexion.getConexion(); PreparedStatement ps = c.prepareStatement(sentencia.toString())){
            int index =  1;
            if(idPersona != null){
                ps.setInt(index, idPersona);
                index++;
            }
            if(idVivienda != null){
                ps.setInt(index, idVivienda);
                index++;
            }
            if(rol != null && !rol.isEmpty()){
                ps.setString(index, rol);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while(rs.next()){
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("IdPersona", rs.getInt("id_persona"));
                    fila.put("Nombre", rs.getString("per_nombre"));
                    fila.put("Rol", rs.getString("hab_rol"));
                    fila.put("IdVivienda", rs.getInt("id_vivienda"));
                    fila.put("TipoVivienda", rs.getString("viv_tipo"));
                    fila.put("NumExt", rs.getInt("viv_numExt"));
                    fila.put("NumInt", rs.getInt("viv_numInt"));
                    fila.put("MetrosCuadrados", rs.getBigDecimal("viv_mtscuadrados"));
                    fila.put("IdCalle", rs.getInt("id_calle"));
                    fila.put("Calle", rs.getString("cal_nombre"));
                    datosEncontrados.add(fila);
                }

            }

        }
        catch (SQLException e) {
            throw new DataAccessException("Error al consultar habitante", e);
        }
        return datosEncontrados;
    }

    public String construirSentenciaBuscarHabitantes(Integer idPersona, Integer idVivienda, String rol){
        if (idPersona != null) Validaciones.id(idPersona, "idPersona");
        if (idVivienda != null) Validaciones.id(idVivienda, "idVivienda");
        if (rol != null && !rol.isEmpty()) rol = Validaciones.texto(rol, "rol", 40);
        StringBuilder sentencia = new StringBuilder(
                """
                        SELECT
                             persona.id_persona,
                             persona.per_nombre,
                             habitantes.hab_rol,
                             vivienda.id_vivienda,
                             vivienda.viv_tipo,
                             vivienda.viv_numExt,
                             vivienda.viv_numInt,
                             vivienda.viv_mtscuadrados,
                             calle.id_calle,
                             calle.cal_nombre
                        FROM persona
                             JOIN habitantes ON persona.id_persona = habitantes.id_persona
                             JOIN vivienda ON habitantes.id_vivienda = vivienda.id_vivienda
                             JOIN calle ON vivienda.id_calle = calle.id_calle""");

        if(idPersona != null || idVivienda != null || (rol != null && !rol.isEmpty())) sentencia.append("\nWHERE ");

        StringBuilder personaString = new StringBuilder(), viviendaString = new StringBuilder(), rolString = new StringBuilder();
        if(idPersona != null){
            personaString.append("persona.id_persona = ? ");
        }
        if(idVivienda != null){
            viviendaString.append("vivienda.id_vivienda = ? ");
        }
        if(rol != null && !rol.isEmpty()){
            rolString.append("habitantes.hab_rol = ?");
        }

        sentencia.append(personaString + (!personaString.isEmpty() && (!viviendaString.isEmpty() || !rolString.isEmpty()) ? "AND " : ""));
        sentencia.append(viviendaString + (!viviendaString.isEmpty() && !rolString.isEmpty() ? "AND " : ""));
        sentencia.append(rolString);

        return sentencia.toString();
    }
}
