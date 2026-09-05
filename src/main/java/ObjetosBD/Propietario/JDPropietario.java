package ObjetosBD.Propietario;

import ObjetosBD.Conexion;
import ObjetosBD.Persona.PersonaBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDPropietario {
    private Conexion CN = new Conexion();

    public int insertarPropietario(int id_vivienda, int id_persona){
        String sql = "INSERT INTO propietario(id_vivienda, id_persona) VALUES (?, ?)";

        try (PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, id_vivienda);
            ps.setInt(2, id_persona);
            System.out.println("REGISTRO DE PROPIETARIO EXITOSO");

            int filas = ps.executeUpdate();

        }catch (SQLException e){
            System.out.println("Error al insertar propietario: " + e.getMessage());
        }
        return -1;
    }

    public PropietarioBD buscarPropietarioID(int id){
        String SQL = "SELECT * FROM propietario WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, id);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idVivienda = RS.getInt("id_vivienda");
                    int idPersona = RS.getInt("id_persona");

                    return new PropietarioBD(idVivienda, idPersona);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR PROPIETARIO POR ID: " + e.getMessage());
        }
        return null;
    }

    public PersonaBD buscarPropietarioNombre(String nombre){
        String SQL = "SELECT p.* FROM persona p JOIN propietario pr ON p.id_persona = pr.id_persona WHERE p.per_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
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
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR PROPIETARION POR NOMBRE: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarPropietario(int idVivienda, int idPersona){
        String SQL = "UPDATE propietario SET id_persona = ? WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idPersona);
            PS.setInt(2, idVivienda);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR PROPIETARIO: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPropietario(int idVivienda, int idPersona){
        String SQL = "DELETE FROM propietario WHERE id_vivienda = ? AND id_persona = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            PS.setInt(2, idPersona);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR PROPIETARIO: " + e.getMessage());
            return false;
        }
    }
}
