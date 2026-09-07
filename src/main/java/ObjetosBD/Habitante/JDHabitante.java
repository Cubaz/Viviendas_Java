package ObjetosBD.Habitante;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JDHabitante {
    private Conexion conexion = new Conexion();

    public boolean insertarHabitante(int idPersona, int idVivienda, String rol){
        String sentencia = "INSERT INTO habitantes (id_persona, id_vivienda, hab_rol) VALUES (?, ?, ?)";

        try(PreparedStatement ps = conexion.getConexion().prepareStatement(sentencia)){
            ps.setInt(1, idPersona);
            ps.setInt(2, idVivienda);
            ps.setString(3, rol);
            int filas = ps.executeUpdate();

            if(filas > 0){
                return true;
            }
        }
        catch(SQLException e){
            System.out.println("Error al insertar habitante: " + e.getMessage());
        }

        return false;
    }

    public ObservableList<Map<String, Object>> buscarHabitantes(Integer idPersona, Integer idVivienda, String rol){
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();
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

        if(idPersona != null || idVivienda != null || rol != null) sentencia.append("\nWHERE ");

        StringBuilder personaString = new StringBuilder(), viviendaString = new StringBuilder(), rolString = new StringBuilder();
        if(idPersona != null){
            personaString.append("persona.id_persona = ? ");
        }
        if(idVivienda != null){
            viviendaString.append("vivienda.id_vivienda = ? ");
        }
        if(rol != null){
            rolString.append("habitantes.hab_rol = ?");
        }

        sentencia.append(personaString + (!personaString.isEmpty() && (!viviendaString.isEmpty() || !rolString.isEmpty()) ? "AND " : ""));
        sentencia.append(viviendaString + (!viviendaString.isEmpty() && !rolString.isEmpty() ? "AND " : ""));
        sentencia.append(rolString);

        sentencia.append("\nGROUP BY persona.id_persona");
//        System.out.println(sentencia.toString());

        try(PreparedStatement ps = conexion.getConexion().prepareStatement(sentencia.toString())){
            int index =  1;
            if(idPersona != null){
                ps.setInt(index, idPersona);
                index++;
            }
            if(idVivienda != null){
                ps.setInt(index, idVivienda);
                index++;
            }
            if(rol != null){
                ps.setString(index, rol);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Map<String, Object> fila = new HashMap<>();
                fila.put("IdPersona", rs.getInt("id_persona"));
                fila.put("Nombre", rs.getString("per_nombre"));
                fila.put("Rol", rs.getString("hab_rol"));
                fila.put("IdVivienda", rs.getInt("id_vivienda"));
                fila.put("TipoVivienda", rs.getString("viv_tipo"));
                fila.put("NumExt", rs.getInt("viv_numExt"));
                fila.put("NumInt", rs.getInt("viv_numInt"));
                fila.put("MetrosCuadrados", rs.getInt("viv_mtscuadrados"));
                fila.put("IdCalle", rs.getInt("id_calle"));
                fila.put("Calle", rs.getString("cal_nombre"));
                datosEncontrados.add(fila);
            }

            if(!datosEncontrados.isEmpty()) return datosEncontrados;
        }
        catch(SQLException e){
            System.out.println("Error al consultar habitante: " + e.getMessage());
        }
        return null;
    }
}
