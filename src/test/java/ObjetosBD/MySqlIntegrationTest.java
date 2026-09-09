package ObjetosBD;

import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Colonia.JDColonia;
import ObjetosBD.Departamento.JDDepartamento;
import ObjetosBD.Edificio.JDEdificio;
import ObjetosBD.Familia.JDFamilia;
import ObjetosBD.Habitante.JDHabitante;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Propietario.JDPropietario;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Clona sólo el esquema en una base exclusiva de esta ejecución. Nunca escribe datos originales. */
@EnabledIfSystemProperty(named="viviendas.test.mysql",matches="true")
class MySqlIntegrationTest {
    static final String[] TABLES={"colonia","familia","edificio","calle","persona","vivienda","departamento","propietario","habitantes"};
    static String database, originalUrl;
    static Connection admin;
    int colonia, calle, familia, persona, edificio;
    final ViviendaService service=new ViviendaService();

    @BeforeAll static void crearBaseAislada() throws Exception {
        originalUrl=System.getProperty("viviendas.db.url");
        admin=new Conexion().getConexion();
        List<String> ddl=new ArrayList<>();
        for(String table:TABLES) {
            try(var statement=admin.createStatement();var rs=statement.executeQuery("SHOW CREATE TABLE " + table)) {
                assertTrue(rs.next()); ddl.add(rs.getString(2));
            }
        }
        database="viviendas_test_"+UUID.randomUUID().toString().replace("-", "");
        try(var s=admin.createStatement()) { s.executeUpdate("CREATE DATABASE `"+database+"` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"); }
        String url=admin.getMetaData().getURL();
        int query=url.indexOf('?');
        String base=query<0?url:url.substring(0,query);
        String parameters=query<0?"":url.substring(query);
        System.setProperty("viviendas.db.url",base.substring(0,base.lastIndexOf('/')+1)+database+parameters);
        try(var c=new Conexion().getConexion();var s=c.createStatement()) {
            for(String sql:ddl) s.executeUpdate(sql);
            aplicarMigracionDeBorrado(c);
        }
    }

    static void aplicarMigracionDeBorrado(Connection connection) throws Exception {
        try (InputStream resource = MySqlIntegrationTest.class.getResourceAsStream("/sql/2026-09-09-bloquear-borrado-padres.sql")) {
            assertNotNull(resource, "La migración de bloqueo debe estar disponible como recurso");
            String migracion = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
            try (var statement = connection.createStatement()) {
                for (String sql : migracion.split(";")) {
                    if (!sql.isBlank()) statement.execute(sql);
                }
            }
        }
    }

    @AfterAll static void eliminarSoloBaseDePrueba() throws Exception {
        try {
            if(admin!=null && database!=null && database.matches("viviendas_test_[0-9a-f]{32}"))
                try(var s=admin.createStatement()) {s.executeUpdate("DROP DATABASE `"+database+"`");}
        } finally {
            if(admin!=null) admin.close();
            if(originalUrl==null) System.clearProperty("viviendas.db.url"); else System.setProperty("viviendas.db.url",originalUrl);
        }
    }

    @BeforeEach void preparar() throws Exception {
        try(var c=new Conexion().getConexion();var s=c.createStatement()) {
            aplicarMigracionDeBorrado(c);
            assertEquals(database,c.getCatalog());
            for(String table:new String[]{"habitantes","propietario","departamento","vivienda","persona","calle","familia","edificio","colonia"}) s.executeUpdate("DELETE FROM "+table);
        }
        colonia=new JDColonia().insertarColonia("Centro");
        familia=new JDFamilia().insertarFamilia("O'Connor");
        edificio=new JDEdificio().insertarEdificio("Edificio A");
        calle=new JDCalle().insertarCalle("Calle Álamo",colonia);
        persona=new JDPersona().insertarPersona("María",familia,31);
        assertTrue(colonia>0 && familia>0 && edificio>0 && calle>0 && persona>0,"Las altas deben devolver la clave generada");
    }

    int vivienda(String tipo) {return service.crear(tipo,0,10,0,calle,42.25f,persona,"Departamento".equals(tipo)?edificio:null,"Departamento".equals(tipo)?2:null);}
    int count(String table) throws Exception {try(var c=new Conexion().getConexion();var s=c.createStatement();var r=s.executeQuery("SELECT COUNT(*) FROM "+table)){r.next();return r.getInt(1);}}

    @Test void catalogosCrudYBusquedaExacta() throws Exception {
        var colonias=new JDColonia(); var calles=new JDCalle(); var familias=new JDFamilia();
        assertTrue(colonias.actualizarColonia(colonia,"Centro",123.5f));
        assertTrue(colonias.actualizarColonia(colonia,"Centro nuevo"));
        assertEquals(123.5f,colonias.buscarColoniaID(colonia).getFirst().getSup_construida());
        assertEquals(123.5f,colonias.buscarColoniaNombre("Centro nuevo").getFirst().getSup_construida());
        assertEquals(123.5f,colonias.obtenerColonias().getFirst().getSup_construida());
        try(var c=new Conexion().getConexion();var ps=c.prepareStatement("SELECT col_supconstruida FROM colonia WHERE id_colonia=?")) {
            ps.setInt(1,colonia);try(var r=ps.executeQuery()){r.next();assertEquals(123.5f,r.getFloat(1));}
        }
        int exact=1000001, similar=11000001;
        try(var c=new Conexion().getConexion();var s=c.createStatement()) {
            s.executeUpdate("INSERT INTO colonia(id_colonia,col_nombre) VALUES ("+exact+",'Exacta'),("+similar+",'Similar')");
            s.executeUpdate("INSERT INTO familia(id_familia,fam_apellidos) VALUES ("+exact+",'Exacta'),("+similar+",'Similar')");
            s.executeUpdate("INSERT INTO calle(id_calle,cal_nombre,id_colonia) VALUES ("+exact+",'Exacta',"+colonia+"),("+similar+",'Similar',"+colonia+")");
        }
        assertEquals(1,colonias.buscarColoniaID(exact).size());
        assertEquals(1,familias.buscarFamiliaIDTABLA(exact).size());
        assertEquals(1,calles.buscarCalleIDTABLA(exact).size());
        assertNotNull(familias.buscarFamiliaID(exact));assertNotNull(calles.buscarCalleID(exact));
        assertTrue(calles.actualizarCalle(calle,"Nueva calle",colonia));
        assertTrue(familias.actualizarFamilia(familia,"Nuevo apellido"));
        assertFalse(calles.buscarCalleaNombre("Nueva").isEmpty());
        assertFalse(familias.buscarFamiliaApellidos("Nuevo").isEmpty());
        assertFalse(colonias.buscarColoniaNombre("Centro").isEmpty());
        assertTrue(calles.eliminarCalle(exact));
        assertTrue(familias.eliminarFamilia(exact));
        assertTrue(colonias.eliminarColonia(exact));
    }

    @Test void viviendaSeConvierteDeTipoYNoSeEliminaMientrasTieneRelaciones() throws Exception {
        int id=vivienda("Unifamiliar");
        assertTrue(service.actualizar(id,"Departamento",2,10,4,calle,64.75f,persona,edificio,3));
        assertEquals(3,new JDDepartamento().buscarDepartamentoVivienda(id).getPiso());
        assertTrue(service.actualizar(id,"Unifamiliar",2,10,0,calle,64.75f,persona,null,null));
        assertNull(new JDDepartamento().buscarDepartamentoVivienda(id));
        assertEquals(64.75f,new JDVivienda().buscarVivienda(id).getMts_cuadrados());
        assertThrows(DataAccessException.class, () -> service.eliminar(id));
        assertEquals(1,count("propietario"));
        assertEquals(1,count("vivienda"));
        assertTrue(new JDPropietario().eliminarPropietario(id, persona));
        assertTrue(service.eliminar(id));
        assertFalse(service.eliminar(id));
    }

    @Test void falloDeRelacionRevierteAltaYActualizacion() throws Exception {
        assertThrows(DataAccessException.class,()->service.crear("Departamento",0,1,0,calle,10.25f,persona,Integer.MAX_VALUE,0));
        assertEquals(0,count("vivienda"));assertEquals(0,count("propietario"));
        int id=vivienda("Unifamiliar");
        assertThrows(DataAccessException.class,()->service.actualizar(id,"Departamento",3,99,1,calle,15f,persona,Integer.MAX_VALUE,0));
        assertEquals("Unifamiliar",new JDVivienda().buscarVivienda(id).getTipo());
        assertEquals(10,new JDVivienda().buscarVivienda(id).getNum_ext());
    }

    @Test void habitantesRespetanClaveCompuestaYDecimales() throws Exception {
        int primera=vivienda("Unifamiliar"), segunda=vivienda("Unifamiliar");
        var dao=new JDHabitante();
        assertTrue(dao.insertarHabitante(persona,primera,"Madre"));assertTrue(dao.insertarHabitante(persona,segunda,"Otro"));
        var rows=dao.buscarHabitantes(persona,null,null);
        assertEquals(2,rows.size());assertEquals(42.25f,((Number)rows.get(0).get("MetrosCuadrados")).floatValue());
        assertThrows(IllegalArgumentException.class,()->dao.borrarHabitante(persona));
        assertThrows(DataAccessException.class,()->service.eliminar(primera));
        assertTrue(dao.actualizarHabitante(persona,primera,primera,"Padre"));
        assertEquals("Otro",dao.buscarHabitante(persona,segunda).getRol());
        assertTrue(dao.borrarHabitante(persona,primera));assertFalse(dao.borrarHabitante(persona,primera));
        assertNotNull(dao.buscarHabitante(persona,segunda));
    }

    @Test void todasLasCombinacionesDeFiltrosSonSQLValido() {
        int id=vivienda("Departamento");
        int dep=new JDDepartamento().buscarDepartamentoVivienda(id).getId_departamento();
        new JDHabitante().insertarHabitante(persona,id,"Madre");
        for(int mask=0;mask<16;mask++) {
            assertEquals(1,new JDPersona().buscarPersonas((mask&1)>0?persona:null,(mask&2)>0?"María":null,(mask&4)>0?familia:null,(mask&8)>0?31:null).size());
            assertEquals(1,new JDDepartamento().buscarDepartamentos((mask&1)>0?dep:null,(mask&2)>0?edificio:null,(mask&4)>0?id:null,(mask&8)>0?2:null).size());
        }
        for(int mask=0;mask<8;mask++) assertEquals(1,new JDHabitante().buscarHabitantes((mask&1)>0?persona:null,(mask&2)>0?id:null,(mask&4)>0?"Madre":null).size());
        for(int mask=0;mask<4;mask++) assertEquals(1,new JDEdificio().buscarEdificios((mask&1)>0?edificio:null,(mask&2)>0?"Edificio":null).size());
        assertTrue(new JDPersona().buscarPersonas(Integer.MAX_VALUE,null,null,null).isEmpty());
        assertTrue(new JDDepartamento().buscarDepartamentos(Integer.MAX_VALUE,null,null,null).isEmpty());
        assertTrue(new JDHabitante().buscarHabitantes(Integer.MAX_VALUE,null,null).isEmpty());
        assertTrue(new JDEdificio().buscarEdificios(Integer.MAX_VALUE,null).isEmpty());
    }

    @Test void consultasConApostrofesNoSeInterpretanComoSQL() {
        assertTrue(new JDPersona().buscarPersonas(null,"' OR 1=1 --",null,null).isEmpty());
        assertEquals(1,new JDFamilia().buscarFamiliaApellidos("O'Connor").size());
    }

    @Test void erroresDeRelacionNoSeSilencianNiDanExito() {
        var personas=new JDPersona();
        assertThrows(DataAccessException.class,()->personas.insertarPersona("Inválida",Integer.MAX_VALUE,30));
        assertThrows(DataAccessException.class,()->new JDFamilia().eliminarFamilia(familia));
        assertFalse(personas.actualizarPersona(Integer.MAX_VALUE,"Ausente",familia,10));
        assertFalse(new JDHabitante().actualizarHabitante(persona,Integer.MAX_VALUE,Integer.MAX_VALUE,"Otro"));
    }

    @Test void padresNoSeEliminanHastaQueSeEliminanSusHijos() {
        var colonias = new JDColonia();
        var calles = new JDCalle();
        var familias = new JDFamilia();
        var edificios = new JDEdificio();
        var personas = new JDPersona();
        int vivienda = vivienda("Departamento");

        assertThrows(DataAccessException.class, () -> colonias.eliminarColonia(colonia));
        assertThrows(DataAccessException.class, () -> calles.eliminarCalle(calle));
        assertThrows(DataAccessException.class, () -> familias.eliminarFamilia(familia));
        assertThrows(DataAccessException.class, () -> edificios.eliminarEdificio(edificio));
        assertThrows(DataAccessException.class, () -> personas.eliminarPersona(persona));

        var departamento = new JDDepartamento().buscarDepartamentoVivienda(vivienda);
        assertTrue(new JDPropietario().eliminarPropietario(vivienda, persona));
        assertTrue(new JDDepartamento().eliminarDepartamento(departamento.getId_departamento()));
        assertTrue(service.eliminar(vivienda));
        assertTrue(personas.eliminarPersona(persona));
        assertTrue(calles.eliminarCalle(calle));
        assertTrue(edificios.eliminarEdificio(edificio));
        assertTrue(familias.eliminarFamilia(familia));
        assertTrue(colonias.eliminarColonia(colonia));
    }

    @Test void copropietariosNoSeSobrescriben() throws Exception {
        int id=vivienda("Unifamiliar");
        int otro=new JDPersona().insertarPersona("Otro",familia,40);
        assertTrue(new JDPropietario().insertarPropietario(id,otro)>0);
        assertThrows(IllegalArgumentException.class,()->service.actualizar(id,"Unifamiliar",0,20,0,calle,30f,otro,null,null));
        assertEquals(2,count("propietario"));
        assertEquals(10,new JDVivienda().buscarVivienda(id).getNum_ext());
    }

    @Test void daoPersonaEdificioDepartamentoYPropietarioCrud() {
        var p=new JDPersona();var e=new JDEdificio();var d=new JDDepartamento();var owner=new JDPropietario();
        assertTrue(p.actualizarPersona(persona,"Nombre editado",familia,45));
        assertNotNull(p.buscarPersonaNombre("editado"));
        assertTrue(e.actualizarEdificio(edificio,"Edificio editado"));assertNotNull(e.buscarEdificioNombre("editado"));
        int id=new JDVivienda().insertarVivienda("Departamento",0,1,0,calle,5.25f);
        int dep=d.insertarDepartamento(edificio,id,0);assertTrue(dep>0);
        assertTrue(d.actualizarDepartamento(dep,edificio,id,3));assertEquals(3,d.buscarDepartamento(dep).getPiso());
        assertTrue(owner.insertarPropietario(id,persona)>0);assertNotNull(owner.buscarPropietarioNombre("editado"));
        int otro=p.insertarPersona("Nuevo dueño",familia,40);
        assertTrue(owner.actualizarPropietario(id,otro));assertEquals(otro,owner.buscarPropietarioID(id).getId_persona());
        assertTrue(owner.eliminarPropietario(id,otro));assertTrue(d.eliminarDepartamento(dep));
        assertTrue(new JDVivienda().eliminarVivienda(id));
        assertTrue(p.eliminarPersona(otro));
        assertTrue(e.eliminarEdificio(edificio));
    }

    @Test void superficieExactaNoSePierdeAlLeerNiEditar() {
        for(String valor:new String[]{"131072.01","262144.01","9999999999.99"}) {
            var metros=new java.math.BigDecimal(valor);
            int id=service.crear("Unifamiliar",0,1,0,calle,metros,persona,null,null);
            var leida=new JDVivienda().buscarVivienda(id);
            assertEquals(metros,leida.getMtsCuadradosExactos());
            assertTrue(service.actualizar(id,"Unifamiliar",0,2,0,calle,leida.getMtsCuadradosExactos(),persona,null,null));
            assertEquals(metros,new JDVivienda().buscarVivienda(id).getMtsCuadradosExactos());
        }
        assertThrows(IllegalArgumentException.class,()->service.crear("Unifamiliar",0,1,0,calle,0.001f,persona,null,null));
    }

    @Test void nuevePantallasCarganConDatos() throws Exception {
        Assumptions.assumeTrue(Boolean.getBoolean("viviendas.test.ui"));
        vivienda("Unifamiliar");
        CompletableFuture<Void> ready=new CompletableFuture<>();
        try {javafx.application.Platform.startup(()->ready.complete(null));}
        catch(IllegalStateException initialized){javafx.application.Platform.runLater(()->ready.complete(null));}
        ready.get(15,TimeUnit.SECONDS);
        CompletableFuture<Void> result=new CompletableFuture<>();
        javafx.application.Platform.runLater(()->{
            try {
                javafx.application.Platform.setImplicitExit(false);
                for(String screen:new String[]{"MenuPrincipal","Calle","Colonia","Familia","Persona","Edificio","Departamento","Habitante","Vivienda"}) {
                    javafx.fxml.FXMLLoader loader=new javafx.fxml.FXMLLoader(getClass().getResource("/Interfaces/"+screen+".fxml"));
                    javafx.scene.Parent root=loader.load();assertNotNull(root,screen);
                    new javafx.scene.Scene(root);root.applyCss();root.layout();
                }
                result.complete(null);
            }catch(Throwable t){result.completeExceptionally(t);}
        });
        result.get(30,TimeUnit.SECONDS);
    }
}
