package ObjetosBD.Persona;

import ObjetosBD.Conexion;

import java.sql.PreparedStatement;
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
}
