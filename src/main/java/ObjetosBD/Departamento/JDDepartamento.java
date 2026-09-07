package ObjetosBD.Departamento;
import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDDepartamento {

    private Conexion CN= new Conexion();

    public int insertarDepartamento(int id_edificio, int id_vivienda, int piso){
        String sql = "INSERT INTO departamento (id_edificio, id_vivienda, dep_piso) VALUES (?,?,?)";
        try(PreparedStatement ps = CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, id_edificio);
            ps.setInt(2, id_vivienda);
            ps.setInt(3, piso);

            int filas = ps.executeUpdate();
            if(filas > 0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE DEPARTAMENTO EXITOSO");
                    return idGenerado;
                }
            }
        }catch(SQLException e){
            System.out.println("ERROR AL INSERTAR DEPARTAMENTO" + e);
        }
        return -1;
    }

    public DepartamentoBD buscarDepartamentoVivienda(int idVivienda){
        String SQL = "SELECT * FROM departamento WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idDepartamento = RS.getInt("id_departamento");
                    int idEdificio = RS.getInt("id_edificio");
                    int piso = RS.getInt("dep_piso");

                    return new DepartamentoBD(idDepartamento, idEdificio, idVivienda, piso);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR DEPARTAMENTO POR VIVIENDA " + e.getMessage());
        }
        return null;
    }

    public DepartamentoBD buscarDepartamento(int idDepartamento){
        String SQL = "SELECT * FROM departamento WHERE id_departamento = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idDepartamento);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idEdificio = RS.getInt("id_edificio");
                    int piso = RS.getInt("dep_piso");
                    int idVivienda = RS.getInt("id_vivienda");

                    return new DepartamentoBD(idDepartamento, idEdificio, idVivienda, piso);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR DEPARTAMENTO POR VIVIENDA " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarDepartamento(int idDepartamento, int idEdificio, int idVivienda, int piso){
        String SQL = "UPDATE departamento SET id_edificio = ?, id_vivienda = ?, dep_piso = ? WHERE id_departamento = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            PS.setInt(2, idVivienda);
            PS.setInt(3, piso);
            PS.setInt(4, idDepartamento);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR DEPARTAMENTO: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarDepartamento(int idDepartamento){
        String SQL = "DELETE FROM departamento WHERE id_departamento = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idDepartamento);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR DEPARTAMENTO: " + e.getMessage());
            return false;
        }
    }

    public ObservableList<Map<String, Object>> buscarDepartamentos(Integer idDepartamento, Integer idEdificio, Integer idVivienda, Integer piso){
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();

        String sentencia = construirSentenciaBuscarDepartamentos(idDepartamento, idEdificio, idVivienda, piso);

        try(PreparedStatement ps = CN.getConexion().prepareStatement(sentencia)){
            int index =  1;
            if(idDepartamento != null){
                ps.setInt(index++, idDepartamento);
            }
            if(idEdificio != null){
                ps.setInt(index++, idEdificio);
            }
            if(idVivienda != null){
                ps.setInt(index++, idVivienda);
            }
            if(piso != null){
                ps.setInt(index++, piso);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Map<String, Object> fila = new HashMap<>();
                fila.put("IdDepartamento", rs.getInt("id_departamento"));
                fila.put("IdEdificio", rs.getInt("id_edificio"));
                fila.put("NombreEdificio", rs.getString("edi_nombre"));
                fila.put("IdVivienda", rs.getInt("id_vivienda"));
                fila.put("TipoVivienda", rs.getString("viv_tipo"));
                fila.put("Piso", rs.getInt("dep_piso"));
                datosEncontrados.add(fila);
            }

            if(!datosEncontrados.isEmpty()) return datosEncontrados;
        }
        catch(SQLException e){
            System.out.println("Error al consultar departamento: " + e.getMessage());
        }
        return null;
    }

    public String construirSentenciaBuscarDepartamentos(Integer idDepartamento, Integer idEdificio, Integer idVivienda, Integer piso){
        StringBuilder sentencia = new StringBuilder(
                """
                        SELECT
                             departamento.id_departamento,
                             departamento.id_edificio,
                             edificio.edi_nombre,
                             departamento.id_vivienda,
                             vivienda.viv_tipo,
                             departamento.dep_piso
                        FROM departamento
                             JOIN edificio ON departamento.id_edificio = edificio.id_edificio
                             JOIN vivienda ON departamento.id_vivienda = vivienda.id_vivienda""");

        if(idDepartamento != null || idEdificio != null || idVivienda != null || piso != null) sentencia.append("\nWHERE ");

        List<String> condiciones = new ArrayList<>();
        if(idDepartamento != null) condiciones.add("departamento.id_departamento = ?");
        if(idEdificio != null) condiciones.add("departamento.id_edificio = ?");
        if(idVivienda != null) condiciones.add("departamento.id_vivienda = ?");
        if(piso != null) condiciones.add("departamento.dep_piso = ?");

        sentencia.append(String.join(" AND ", condiciones));

        return sentencia.toString();
    }
}
