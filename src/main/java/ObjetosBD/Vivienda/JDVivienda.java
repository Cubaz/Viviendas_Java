package ObjetosBD.Vivienda;

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

public class JDVivienda {
    private final Conexion CN = new Conexion();

    public int insertarVivienda(String tipo, int habitantes, int num_ext, int num_int, int id_calle, float mts_cuadrados){
        tipo = Validaciones.texto(tipo, "tipo", 30);
        Validaciones.rango(habitantes, "habitantes", 0, 65535);
        Validaciones.rango(num_ext, "num_ext", 0, Integer.MAX_VALUE);
        Validaciones.rango(num_int, "num_int", 0, Integer.MAX_VALUE);
        Validaciones.id(id_calle, "id_calle");
        Validaciones.positivo(mts_cuadrados, "Superficie");
        String sql = "INSERT INTO vivienda (viv_tipo, viv_habitantes, viv_numExt, viv_numInt, id_calle, viv_mtscuadrados) VALUES (?,?,?,?,?,?)";
        try(Connection c = CN.getConexion(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, tipo);
            ps.setInt(2, habitantes);
            ps.setInt(3, num_ext);
            ps.setInt(4, num_int);
            ps.setInt(5, id_calle);
            ps.setFloat(6, mts_cuadrados);
            int filas = ps.executeUpdate();
            if(filas >0){
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }

        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL INSERTAR VIVIENDA", e);
        }
        return -1;
    }

    public ViviendaBD buscarVivienda(int idVivienda){
        Validaciones.id(idVivienda, "idVivienda");
        String SQL="SELECT * FROM vivienda WHERE id_vivienda = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    java.math.BigDecimal metroscuadrados = RS.getBigDecimal("viv_mtscuadrados");

                    return new ViviendaBD(idVivienda, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados);
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR VIVIENDA", e);
        }
        return null;
    }

    public ObservableList<ViviendaBD> buscarViviendaTabla(int idVivienda){
        Validaciones.id(idVivienda, "idVivienda");
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL="SELECT * FROM vivienda WHERE id_vivienda = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    java.math.BigDecimal metroscuadrados = RS.getBigDecimal("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL BUSCAR VIVIENDA", e);
        }
        return lista;
    }

    public boolean actualizarVivienda(int idVivienda, String tipo, int habitantes, int numExterior, int numInterior, int idCalle, float metros){
        Validaciones.id(idVivienda, "idVivienda");
        tipo = Validaciones.texto(tipo, "tipo", 30);
        Validaciones.rango(habitantes, "habitantes", 0, 65535);
        Validaciones.rango(numExterior, "numExterior", 0, Integer.MAX_VALUE);
        Validaciones.rango(numInterior, "numInterior", 0, Integer.MAX_VALUE);
        Validaciones.id(idCalle, "idCalle");
        Validaciones.positivo(metros, "Superficie");
        String SQL = "UPDATE vivienda SET viv_tipo = ?, viv_habitantes = ?, viv_numExt = ?, viv_numInt = ?, id_calle = ?, viv_mtscuadrados = ? WHERE id_vivienda = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setString(1, tipo);
            PS.setInt(2, habitantes);
            PS.setInt(3, numExterior);
            PS.setInt(4, numInterior);
            PS.setInt(5, idCalle);
            PS.setFloat(6, metros);
            PS.setInt(7, idVivienda);

            int filas = PS.executeUpdate();

            return filas > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ACTUALIZAR", e);
        }
    }

    public boolean eliminarVivienda(int idVivienda){
        Validaciones.id(idVivienda, "idVivienda");
        String SQL = "DELETE FROM vivienda WHERE id_vivienda = ?";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            PS.setInt(1, idVivienda);

            return PS.executeUpdate() > 0;
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL ELIMINAR VIVIENDA", e);
        }
    }

    public ObservableList<ViviendaBD> obtenerVivienda(){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM vivienda";
        try(Connection c = CN.getConexion(); PreparedStatement PS = c.prepareStatement(SQL)){
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    java.math.BigDecimal metroscuadrados = RS.getBigDecimal("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("ERROR AL OBTENER VIVIENDAS", e);
        }
        return lista;
    }
}
