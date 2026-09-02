package ObjetosBD.Habitante;

import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDHabitante {
    private Conexion conexion = new Conexion();

    public void insertarHabitante(int idPersona, int idVivienda, String rol){
        String sentencia = "INSERT INTO habitantes (id_persona, id_vivienda, hab_rol) VALUES (?, ?, ?)";

        try(PreparedStatement ps = conexion.getConexion().prepareStatement(sentencia)){
            ps.setInt(1, idPersona);
            ps.setInt(2, idVivienda);
            ps.setString(3, rol);
            int filas = ps.executeUpdate();

            if(filas > 0){
                System.out.println("Habitante registrado correctamente");
            }
        }
        catch(SQLException e){
            System.out.println("Error al insertar habitante: " + e.getMessage());
        }
    }
}
