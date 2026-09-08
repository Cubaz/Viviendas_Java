package ObjetosBD.Departamento;
import ObjetosBD.Conexion;
import ObjetosBD.DataAccessException;
import Validation.Validaciones;
import java.sql.Connection;
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

    private final Conexion CN= new Conexion();

    public int insertarDepartamento(int id_edificio, int id_vivienda, int piso){
        Validaciones.id(id_edificio, "id_edificio");
        Validaciones.id(id_vivienda, "id_vivienda");
        Validaciones.rango(piso, "piso", 0, 65535);
        String sql = "INSERT INTO departamento (id_edificio, id_vivienda, dep_piso) VALUES (?,?,?)";
        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            verificarTipoVivienda(c, id_vivienda);
            ps.setInt(1, id_edificio);
            ps.setInt(2, id_vivienda);
            ps.setInt(3, piso);

            int filas = ps.executeUpdate();
            if(filas > 0){
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL INSERTAR DEPARTAMENTO", e);
        }
        return -1;
    }

    public DepartamentoBD buscarDepartamentoVivienda(int idVivienda){
        Validaciones.id(idVivienda, "idVivienda");
        String SQL = "SELECT * FROM departamento WHERE id_vivienda = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idDepartamento = RS.getInt("id_departamento");
                    int idEdificio = RS.getInt("id_edificio");
                    int piso = RS.getInt("dep_piso");

                    return new DepartamentoBD(idDepartamento, idEdificio, idVivienda, piso);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR DEPARTAMENTO POR VIVIENDA", e);
        }
        return null;
    }

    public DepartamentoBD buscarDepartamento(int idDepartamento){
        Validaciones.id(idDepartamento, "idDepartamento");
        String SQL = "SELECT * FROM departamento WHERE id_departamento = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idDepartamento);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int idEdificio = RS.getInt("id_edificio");
                    int piso = RS.getInt("dep_piso");
                    int idVivienda = RS.getInt("id_vivienda");

                    return new DepartamentoBD(idDepartamento, idEdificio, idVivienda, piso);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR DEPARTAMENTO POR VIVIENDA", e);
        }
        return null;
    }

    public boolean actualizarDepartamento(int idDepartamento, int idEdificio, int idVivienda, int piso){
        Validaciones.id(idDepartamento, "idDepartamento");
        Validaciones.id(idEdificio, "idEdificio");
        Validaciones.id(idVivienda, "idVivienda");
        Validaciones.rango(piso, "piso", 0, 65535);
        String SQL = "UPDATE departamento SET id_edificio = ?, id_vivienda = ?, dep_piso = ? WHERE id_departamento = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            verificarTipoVivienda(c, idVivienda);
            PS.setInt(1, idEdificio);
            PS.setInt(2, idVivienda);
            PS.setInt(3, piso);
            PS.setInt(4, idDepartamento);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ACTUALIZAR DEPARTAMENTO", e);
        }
    }

    public boolean eliminarDepartamento(int idDepartamento){
        Validaciones.id(idDepartamento, "idDepartamento");
        String SQL = "DELETE FROM departamento WHERE id_departamento = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idDepartamento);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR DEPARTAMENTO", e);
        }
    }

    public ObservableList<Map<String, Object>> buscarDepartamentos(Integer idDepartamento, Integer idEdificio, Integer idVivienda, Integer piso){
        if (idDepartamento != null) Validaciones.id(idDepartamento, "idDepartamento");
        if (idEdificio != null) Validaciones.id(idEdificio, "idEdificio");
        if (idVivienda != null) Validaciones.id(idVivienda, "idVivienda");
        if (piso != null) Validaciones.rango(piso, "piso", 0, 65535);
        ObservableList<Map<String, Object>> datosEncontrados = FXCollections.observableArrayList();

        String sentencia = construirSentenciaBuscarDepartamentos(idDepartamento, idEdificio, idVivienda, piso);

        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sentencia)){
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

            try (ResultSet rs = ps.executeQuery()) {

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

            }

        }
        catch (SQLException e) {
            throw new DataAccessException("Error al consultar departamento", e);
        }
        return datosEncontrados;
    }

    public String construirSentenciaBuscarDepartamentos(Integer idDepartamento, Integer idEdificio, Integer idVivienda, Integer piso){
        if (idDepartamento != null) Validaciones.id(idDepartamento, "idDepartamento");
        if (idEdificio != null) Validaciones.id(idEdificio, "idEdificio");
        if (idVivienda != null) Validaciones.id(idVivienda, "idVivienda");
        if (piso != null) Validaciones.rango(piso, "piso", 0, 65535);
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

    public ObservableList<DepartamentoBD> obtenerDepartamento(){
        ObservableList<DepartamentoBD> departamentos = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM departamento";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL);
            ResultSet RS = PS.executeQuery()){
            while(RS.next()){
                int idDepartamento = RS.getInt("id_departamento");
                int idEdificio = RS.getInt("id_edificio");
                int idVivienda = RS.getInt("id_vivienda");
                int piso = RS.getInt("dep_piso");
                departamentos.add(new DepartamentoBD(idDepartamento, idEdificio, idVivienda, piso));
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL OBTENER DEPARTAMENTOS", e);
        }
        return departamentos;
    }
    private void verificarTipoVivienda(Connection c, int idVivienda) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT viv_tipo FROM vivienda WHERE id_vivienda = ?")) {
            ps.setInt(1, idVivienda);
            try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalArgumentException("La vivienda indicada no existe");
                    if (!"Departamento".equalsIgnoreCase(rs.getString("viv_tipo"))) {
                        throw new IllegalArgumentException("La vivienda debe ser de tipo Departamento para asociarla a un edificio");
                    }
            }
        }
    }

}
