package ObjetosBD.Propietario;

import ObjetosBD.Conexion;
import java.sql.PreparedStatement;
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

}
