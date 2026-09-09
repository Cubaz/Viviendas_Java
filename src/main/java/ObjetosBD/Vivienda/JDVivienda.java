package ObjetosBD.Vivienda;

import ObjetosBD.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDVivienda {
    private Conexion CN = new Conexion();

    public int insertarVivienda(String tipo, int habitantes, int num_ext, int num_int, int id_calle, float mts_cuadrados){
        String sql = "INSERT INTO vivienda (viv_tipo, viv_habitantes, viv_numExt, viv_numInt, id_calle, viv_mtscuadrados) VALUES (?,?,?,?,?,?)";
        try(PreparedStatement ps= CN.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, tipo);
            ps.setInt(2, habitantes);
            ps.setInt(3, num_ext);
            ps.setInt(4, num_int);
            ps.setInt(5, id_calle);
            ps.setFloat(6, mts_cuadrados);
            int filas = ps.executeUpdate();
            if(filas >0){
                var rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int idGenerado = rs.getInt(1);
                    System.out.println("REGISTRO DE VIVIENDA EXITOSO");
                    System.out.println("Su ID de vivienda es" + idGenerado);
                    return idGenerado;
                }
            }

        }catch(SQLException e){
            System.out.println("ERROR AL INSERTAR VIVIENDA" + e.getMessage());
        }
        return -1;
    }

    public ViviendaBD buscarVivienda(int idVivienda){
        String SQL="SELECT * FROM vivienda WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    return new ViviendaBD(idVivienda, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados);
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA: " + e.getMessage());
        }
        return null;
    }

    public ObservableList<ViviendaBD> buscarViviendaTabla(int idVivienda){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL="SELECT * FROM vivienda WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);
            try(ResultSet RS = PS.executeQuery()){
                if(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaCalleID(int calle){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, " +
                "v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre " +
                "FROM vivienda v JOIN calle c ON v.id_calle = c.id_calle " +
                "WHERE c.id_calle = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, calle);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorNombreCalle(String nombreCalle){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, " +
                "v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre " +
                "FROM vivienda v JOIN calle c ON v.id_calle = c.id_calle " +
                "WHERE c.cal_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            // 🔹 Esto permite coincidencia parcial, incluso por una sola letra
            PS.setString(1, "%" + nombreCalle + "%");
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR NOMBRE DE CALLE: " + e.getMessage());
        }
        return lista;
    }


    public ObservableList<ViviendaBD> buscarViviendaPorPropietario(String nombrePropietario){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, " +
                "v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre " +
                "FROM propietario p " +
                "JOIN persona x ON x.id_persona = p.id_persona " +
                "JOIN vivienda v ON v.id_vivienda = p.id_vivienda " +
                "JOIN calle c ON v.id_calle = c.id_calle " +
                "WHERE x.per_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + nombrePropietario + "%"); // coincidencia parcial
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR PROPIETARIO: " + e.getMessage());
        }
        return lista;
    }


    public ObservableList<ViviendaBD> buscarViviendaPorIDPropietario(int idPropietario){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, " +
                "v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre " +
                "FROM propietario p " +
                "JOIN persona x ON x.id_persona = p.id_persona " +
                "JOIN vivienda v ON v.id_vivienda = p.id_vivienda " +
                "JOIN calle c ON v.id_calle = c.id_calle " +
                "WHERE p.id_persona = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idPropietario);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR PROPIETARIO: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorIDEdificio(int idEdificio){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre "+
        "FROM edificio e "+
        "JOIN departamento d ON d.id_edificio = e.id_edificio "+
        "JOIN vivienda v ON d.id_vivienda = v.id_vivienda "+
        "JOIN calle c ON v.id_calle = c.id_calle "+
        "WHERE v.viv_tipo = \"Departamento\" AND e.id_edificio LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idEdificio);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR ID DE EDIFICIO: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorNombreEdificio(String nombreEdificio){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre "+
                "FROM edificio e "+
                "JOIN departamento d ON d.id_edificio = e.id_edificio "+
                "JOIN vivienda v ON d.id_vivienda = v.id_vivienda "+
                "JOIN calle c ON v.id_calle = c.id_calle "+
                "WHERE v.viv_tipo = \"Departamento\" AND e.edi_nombre LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, "%" + nombreEdificio + "%");
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR ID DE EDIFICIO: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorNumeroPiso(int numeroPiso){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre "+
                "FROM edificio e "+
                "JOIN departamento d ON d.id_edificio = e.id_edificio "+
                "JOIN vivienda v ON d.id_vivienda = v.id_vivienda "+
                "JOIN calle c ON v.id_calle = c.id_calle "+
                "WHERE v.viv_tipo = \"Departamento\" AND d.dep_piso LIKE ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, numeroPiso);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR NUMERO DE PISO: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorMtsCuadrados(float mtsCuadrados){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre "+
                "FROM edificio e "+
                "JOIN departamento d ON d.id_edificio = e.id_edificio "+
                "JOIN vivienda v ON d.id_vivienda = v.id_vivienda "+
                "JOIN calle c ON v.id_calle = c.id_calle "+
                "WHERE v.viv_mtscuadrados = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setFloat(1, mtsCuadrados);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR METROS CUADRADOS: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorNumExt(int numExt){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre "+
                "FROM edificio e "+
                "JOIN departamento d ON d.id_edificio = e.id_edificio "+
                "JOIN vivienda v ON d.id_vivienda = v.id_vivienda "+
                "JOIN calle c ON v.id_calle = c.id_calle "+
                "WHERE v.viv_numExt = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, numExt);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR NÚMERO EXTERIOR: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<ViviendaBD> buscarViviendaPorNumInt(int numInt){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT v.id_vivienda, v.viv_tipo, v.viv_habitantes, v.viv_numExt, v.viv_numInt, v.id_calle, v.viv_mtscuadrados, c.cal_nombre "+
                "FROM edificio e "+
                "JOIN departamento d ON d.id_edificio = e.id_edificio "+
                "JOIN vivienda v ON d.id_vivienda = v.id_vivienda "+
                "JOIN calle c ON v.id_calle = c.id_calle "+
                "WHERE v.viv_numInt = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, numInt);
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL BUSCAR VIVIENDA POR NÚMERO EXTERIOR: " + e.getMessage());
        }
        return lista;
    }


    public boolean actualizarVivienda(int idVivienda, String tipo, int habitantes, int numExterior, int numInterior, int idCalle, float metros){
        String SQL = "UPDATE vivienda SET viv_tipo = ?, viv_habitantes = ?, viv_numExt = ?, viv_numInt = ?, id_calle = ?, viv_mtscuadrados = ? WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setString(1, tipo);
            PS.setInt(2, habitantes);
            PS.setInt(3, numExterior);
            PS.setInt(4, numInterior);
            PS.setInt(5, idCalle);
            PS.setFloat(6, metros);
            PS.setInt(7, idVivienda);

            int filas = PS.executeUpdate();
            System.out.println("Filas actualizadas: " + filas); // 🔹 para depuración
            return filas > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ACTUALIZAR: " + e.getMessage());
            return false;
        }
    }


    public boolean eliminarVivienda(int idVivienda){
        String SQL = "DELETE FROM vivienda WHERE id_vivienda = ?";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            PS.setInt(1, idVivienda);

            return PS.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println("ERROR AL ELIMINAR VIVIENDA: " + e.getMessage());
            return false;
        }
    }

    public ObservableList<ViviendaBD> obtenerVivienda(){
        ObservableList<ViviendaBD> lista = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM vivienda";
        try(PreparedStatement PS = CN.getConexion().prepareStatement(SQL)){
            try(ResultSet RS = PS.executeQuery()){
                while(RS.next()){
                    int id = RS.getInt("id_vivienda");
                    String tipo = RS.getString("viv_tipo");
                    int habitantes = RS.getInt("viv_habitantes");
                    int numExterior = RS.getInt("viv_numExt");
                    int numInterior = RS.getInt("viv_numInt");
                    int idCalle = RS.getInt("id_calle");
                    float metroscuadrados = RS.getFloat("viv_mtscuadrados");

                    lista.add(new ViviendaBD(id, tipo, habitantes, numExterior, numInterior, idCalle, metroscuadrados));
                }
            }
        }catch (SQLException e){
            System.out.println("ERROR AL OBTENER VIVIENDAS: " + e.getMessage());
        }
        return lista;
    }
}
