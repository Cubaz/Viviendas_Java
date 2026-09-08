package ObjetosBD.Vivienda;

import ObjetosBD.Conexion;
import ObjetosBD.DataAccessException;
import Validation.Validaciones;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;

/** La vivienda y sus relaciones se guardan completas o se revierten juntas. */
public class ViviendaService {
    @FunctionalInterface
    private interface Trabajo<T> { T ejecutar(Connection connection) throws SQLException; }

    private <T> T transaccion(Trabajo<T> trabajo) {
        try (var conexion = new Conexion(); var connection = conexion.getConexion()) {
            connection.setAutoCommit(false);
            try {
                T resultado = trabajo.ejecutar(connection);
                connection.commit();
                return resultado;
            } catch (SQLException | RuntimeException error) {
                try { connection.rollback(); } catch (SQLException rollback) { error.addSuppressed(rollback); }
                throw error;
            }
        } catch (SQLException error) {
            throw new DataAccessException("Guardar vivienda", error);
        }
    }

    private void validar(String tipo, int habitantes, int exterior, int interior, int calle,
                         BigDecimal metros, int propietario, Integer edificio, Integer piso) {
        if (!"Unifamiliar".equals(tipo) && !"Departamento".equals(tipo))
            throw new IllegalArgumentException("Seleccione un tipo de vivienda válido.");
        Validaciones.rango(habitantes, "Habitantes", 0, 65535);
        Validaciones.rango(exterior, "Número exterior", 0, Integer.MAX_VALUE);
        Validaciones.rango(interior, "Número interior", 0, Integer.MAX_VALUE);
        Validaciones.id(calle, "Calle");
        Validaciones.id(propietario, "Propietario");
        Validaciones.superficie(metros, "Superficie");
        if ("Departamento".equals(tipo)) {
            Validaciones.id(Validaciones.requerido(edificio, "Edificio"), "Edificio");
            Validaciones.rango(Validaciones.requerido(piso, "Piso"), "Piso", 0, 65535);
        }
    }

    public int crear(String tipo, int habitantes, int exterior, int interior, int calle,
                     float metros, int propietario, Integer edificio, Integer piso) {
        Validaciones.positivo(metros, "Superficie");
        return crear(tipo, habitantes, exterior, interior, calle, new BigDecimal(Float.toString(metros)), propietario, edificio, piso);
    }

    public boolean actualizar(int id, String tipo, int habitantes, int exterior, int interior,
                              int calle, float metros, int propietario, Integer edificio, Integer piso) {
        Validaciones.positivo(metros, "Superficie");
        return actualizar(id, tipo, habitantes, exterior, interior, calle, new BigDecimal(Float.toString(metros)), propietario, edificio, piso);
    }

    public int crear(String tipo, int habitantes, int exterior, int interior, int calle,
                     BigDecimal metros, int propietario, Integer edificio, Integer piso) {
        validar(tipo, habitantes, exterior, interior, calle, metros, propietario, edificio, piso);
        return transaccion(connection -> {
            int id;
            try (var ps = connection.prepareStatement("INSERT INTO vivienda (viv_tipo,viv_habitantes,viv_numExt,viv_numInt,id_calle,viv_mtscuadrados) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                campos(ps, tipo, habitantes, exterior, interior, calle, metros);
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se obtuvo el ID de vivienda.");
                    id = rs.getInt(1);
                }
            }
            insertarPropietario(connection, id, propietario);
            departamento(connection, id, tipo, edificio, piso);
            return id;
        });
    }

    public boolean actualizar(int id, String tipo, int habitantes, int exterior, int interior,
                              int calle, BigDecimal metros, int propietario, Integer edificio, Integer piso) {
        Validaciones.id(id, "Vivienda");
        validar(tipo, habitantes, exterior, interior, calle, metros, propietario, edificio, piso);
        return transaccion(connection -> {
            if (!bloquear(connection, id)) return false;
            Integer anterior = null;
            try (var ps = connection.prepareStatement("SELECT id_persona FROM propietario WHERE id_vivienda = ? FOR UPDATE")) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) anterior = rs.getInt(1);
                    if (rs.next()) throw new IllegalArgumentException("La vivienda tiene varios propietarios. No se puede reemplazarlos desde este formulario de propietario único.");
                }
            }
            try (var ps = connection.prepareStatement("UPDATE vivienda SET viv_tipo=?,viv_habitantes=?,viv_numExt=?,viv_numInt=?,id_calle=?,viv_mtscuadrados=? WHERE id_vivienda=?")) {
                campos(ps, tipo, habitantes, exterior, interior, calle, metros);
                ps.setInt(7, id);
                ps.executeUpdate();
            }
            if (anterior == null) insertarPropietario(connection, id, propietario);
            else {
                try (var ps = connection.prepareStatement("UPDATE propietario SET id_persona=? WHERE id_vivienda=? AND id_persona=?")) {
                    ps.setInt(1, propietario); ps.setInt(2, id); ps.setInt(3, anterior); ps.executeUpdate();
                }
            }
            departamento(connection, id, tipo, edificio, piso);
            return true;
        });
    }

    public boolean eliminar(int id) {
        Validaciones.id(id, "Vivienda");
        return transaccion(connection -> {
            if (!bloquear(connection, id)) return false;
            try (var ps = connection.prepareStatement("SELECT id_persona FROM habitantes WHERE id_vivienda=? LIMIT 1")) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) throw new IllegalArgumentException("La vivienda tiene habitantes asociados. Retire esas relaciones antes de eliminarla.");
                }
            }
            for (String tabla : new String[]{"propietario", "departamento", "vivienda"}) {
                try (var ps = connection.prepareStatement("DELETE FROM " + tabla + " WHERE id_vivienda=?")) {
                    ps.setInt(1, id); ps.executeUpdate();
                }
            }
            return true;
        });
    }

    private boolean bloquear(Connection connection, int id) throws SQLException {
        try (var ps = connection.prepareStatement("SELECT id_vivienda FROM vivienda WHERE id_vivienda=? FOR UPDATE")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private void insertarPropietario(Connection connection, int id, int persona) throws SQLException {
        try (var ps = connection.prepareStatement("INSERT INTO propietario (id_vivienda,id_persona) VALUES (?,?)")) {
            ps.setInt(1, id); ps.setInt(2, persona); ps.executeUpdate();
        }
    }

    private void departamento(Connection connection, int id, String tipo, Integer edificio, Integer piso) throws SQLException {
        if ("Departamento".equals(tipo)) {
            // id_vivienda es UNIQUE: la misma consulta sirve al crear o convertir el tipo.
            try (var ps = connection.prepareStatement("INSERT INTO departamento (id_vivienda,id_edificio,dep_piso) VALUES (?,?,?) ON DUPLICATE KEY UPDATE id_edificio=?,dep_piso=?")) {
                ps.setInt(1,id); ps.setInt(2,edificio); ps.setInt(3,piso); ps.setInt(4,edificio); ps.setInt(5,piso); ps.executeUpdate();
            }
        } else {
            try (var ps = connection.prepareStatement("DELETE FROM departamento WHERE id_vivienda=?")) {
                ps.setInt(1,id); ps.executeUpdate();
            }
        }
    }

    private void campos(java.sql.PreparedStatement ps, String tipo, int habitantes, int exterior,
                        int interior, int calle, BigDecimal metros) throws SQLException {
        ps.setString(1,tipo); ps.setInt(2,habitantes); ps.setInt(3,exterior); ps.setInt(4,interior); ps.setInt(5,calle);
        ps.setBigDecimal(6,metros);
    }
}
