package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Calle.JDCalle;

import ObjetosBD.Departamento.DepartamentoBD;
import ObjetosBD.Departamento.JDDepartamento;
import ObjetosBD.Edificio.EdificioBD;
import ObjetosBD.Edificio.JDEdificio;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
import ObjetosBD.Propietario.JDPropietario;
import ObjetosBD.Propietario.PropietarioBD;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaBD;
import ObjetosBD.Vivienda.ViviendaService;
import Validation.Validaciones;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;

public class ViviendaController {

    private JDVivienda VDB;
    private final ViviendaService servicio = new ViviendaService();
    private JDCalle CDB;
    private JDEdificio EDB;
    private JDDepartamento DDB;
    private JDPersona PDB;
    private JDPropietario PRDB;

    @FXML private ComboBox<CalleBD> calle;
    @FXML private ComboBox<EdificioBD> edificio;
    @FXML private ComboBox<PersonaBD> propietario;
    @FXML private TextField piso;
    @FXML private ComboBox<String> vivienda;
    @FXML private TextField habitantes;
    @FXML private TextField mts_cuadrados;
    @FXML private TextField num_ext;
    @FXML private TextField num_int;

    @FXML private TableColumn<ViviendaBD, String> colCalle;
    @FXML private TableColumn<ViviendaBD, Integer> colHabitantes;
    @FXML private TableColumn<ViviendaBD, Integer> colIdVivienda;
    @FXML private TableColumn<ViviendaBD, BigDecimal> colMtsCuadrados;
    @FXML private TableColumn<ViviendaBD, Integer> colNumExt;
    @FXML private TableColumn<ViviendaBD, Integer> colNumInt;
    @FXML private TableColumn<ViviendaBD, String> colTipoVivienda;

    @FXML private ComboBox<String> criterio;
    @FXML private TextField parametro;
    @FXML private TableView<ViviendaBD> tabla_vivienda;

    @FXML private Text out_infoOperacion;

    private final Color colorAdvertencia = Color.YELLOW;
    private final Color colorExito = Color.GREEN;
    private final Color colorBlanco = Color.WHITE;
    private String mensajeOperacion;

    @FXML
    void registrar_vivienda(ActionEvent event) {validar();
    }

    private record DatosVivienda(String tipo, int habitantes, int exterior, int interior,
                                  int calle, BigDecimal metros, int propietario, Integer edificio, Integer piso) {}

    private DatosVivienda leerDatos() {
        String tipo = Validaciones.requerido(vivienda.getValue(), "Tipo de vivienda");
        if (!vivienda.getItems().contains(tipo)) {
            throw new IllegalArgumentException("Seleccione un tipo de vivienda válido");
        }
        int idCalle = Validaciones.requerido(calle.getValue(), "Calle").getId_calle();
        int idPropietario = Validaciones.requerido(propietario.getValue(), "Propietario").getIdPersona();
        int numHab = Validaciones.entero(habitantes.getText(), "Número de habitantes", 0, 65535);
        int exterior = Validaciones.entero(num_ext.getText(), "Número exterior", 0, Integer.MAX_VALUE);
        int interior = Validaciones.entero(num_int.getText(), "Número interior", 0, Integer.MAX_VALUE);
        BigDecimal metros = Validaciones.decimalExacto(mts_cuadrados.getText(), "Metros cuadrados");
        Integer idEdificio = null;
        Integer numPiso = null;
        if ("Departamento".equals(tipo)) {
            idEdificio = Validaciones.requerido(edificio.getValue(), "Edificio").getIdEdificio();
            numPiso = Validaciones.entero(piso.getText(), "Piso", 0, 65535);
        }
        return new DatosVivienda(tipo, numHab, exterior, interior, idCalle, metros, idPropietario, idEdificio, numPiso);
    }

    private void validar() {
        DatosVivienda d = leerDatos();
        int id = servicio.crear(d.tipo(), d.habitantes(), d.exterior(), d.interior(), d.calle(),
                d.metros(), d.propietario(), d.edificio(), d.piso());
        limpiarFormulario();
        tabla_vivienda.setItems(VDB.buscarViviendaTabla(id));
        informar("Vivienda registrada correctamente. ID: " + id, colorExito);
    }

    private void informar(String mensaje, Color color) {
        out_infoOperacion.setFill(color);
        out_infoOperacion.setText(mensaje);
        out_infoOperacion.setVisible(true);
    }

    private void limpiarFormulario() {
        tabla_vivienda.getSelectionModel().clearSelection();
        vivienda.setValue(null);
        calle.setValue(null);
        propietario.setValue(null);
        edificio.setValue(null);
        piso.clear();
        habitantes.clear();
        num_ext.clear();
        num_int.clear();
        mts_cuadrados.clear();
    }

    @FXML
    void buscar_vivienda(ActionEvent event) {
        if (criterio.getValue() == null || parametro.getText().isBlank()) {
            mensajeOperacion = "Debe seleccionar un criterio y escribir un parámetro";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }

        ObservableList<ViviendaBD> resultado = FXCollections.observableArrayList();

        if (criterio.getValue().equals("ID")) {
            int id = Validaciones.entero(parametro.getText(), "ID de vivienda", 1, Integer.MAX_VALUE);
            resultado = VDB.buscarViviendaTabla(id);
        }
        limpiarFormulario();

        tabla_vivienda.setItems(resultado);

        if (resultado.isEmpty()) {
            mensajeOperacion = "No se encontraron resultados";
            out_infoOperacion.setFill(colorAdvertencia);
        } else {
            mensajeOperacion = "Resultados encontrados: " + resultado.size();
            out_infoOperacion.setFill(colorBlanco);
        }
        out_infoOperacion.setText(mensajeOperacion);
    }


    @FXML
    void actualizar_vivienda(ActionEvent event) {
        ViviendaBD seleccionada = Validaciones.requerido(
                tabla_vivienda.getSelectionModel().getSelectedItem(), "Vivienda de la tabla");
        DatosVivienda d = leerDatos();
        boolean actualizado = servicio.actualizar(seleccionada.getId_vivienda(), d.tipo(), d.habitantes(),
                d.exterior(), d.interior(), d.calle(), d.metros(), d.propietario(), d.edificio(), d.piso());
        if (actualizado) {
            limpiarFormulario();
            tabla_vivienda.setItems(VDB.buscarViviendaTabla(seleccionada.getId_vivienda()));
            informar("Vivienda actualizada correctamente", colorExito);
        } else {
            informar("No se pudo actualizar la vivienda; ya no existe", colorAdvertencia);
        }
    }

    @FXML
    void eliminar_vivienda(ActionEvent event) {
        ViviendaBD seleccionada = Validaciones.requerido(
                tabla_vivienda.getSelectionModel().getSelectedItem(), "Vivienda de la tabla");
        if (servicio.eliminar(seleccionada.getId_vivienda())) {
            tabla_vivienda.getItems().remove(seleccionada);
            limpiarFormulario();
            informar("Vivienda eliminada correctamente", colorExito);
        } else {
            informar("No se pudo eliminar la vivienda; ya no existe", colorAdvertencia);
        }
    }

    @FXML
    void initialize() {
        VDB = new JDVivienda();
        CDB = new JDCalle();
        EDB = new JDEdificio();
        DDB = new JDDepartamento();
        PDB = new JDPersona();
        PRDB = new JDPropietario();

        edificio.setItems(EDB.obtenerEdificio());
        calle.setItems(CDB.obtenerCalle());
        propietario.setItems(PDB.obtenerPersona());
        vivienda.getItems().addAll("Unifamiliar", "Departamento");

        edificio.setDisable(true);
        piso.setDisable(true);

        vivienda.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esDepto = "Departamento".equals(newVal);
            edificio.setDisable(!esDepto);
            piso.setDisable(!esDepto);
            if (!esDepto) {
                edificio.setValue(null);
                piso.clear();
            }
        });

        criterio.getItems().addAll("ID");
        configurarTablaBusqueda();


        tabla_vivienda.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada == null) return;
            calle.setValue(null);
            propietario.setValue(null);
            edificio.setValue(null);
            piso.clear();
            vivienda.setValue(seleccionada.getTipo());
            habitantes.setText(String.valueOf(seleccionada.getNum_habitantes()));
            num_ext.setText(String.valueOf(seleccionada.getNum_ext()));
            num_int.setText(String.valueOf(seleccionada.getNum_int()));
            mts_cuadrados.setText(seleccionada.getMtsCuadradosExactos().toPlainString());
            calle.getItems().stream().filter(c -> c.getId_calle() == seleccionada.getIdCalle())
                    .findFirst().ifPresent(calle::setValue);
            llenarCamposRelacionados(seleccionada.getId_vivienda());
        });
    }

    private void configurarTablaBusqueda() {
         colIdVivienda.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getId_vivienda()));
         colTipoVivienda.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getTipo()));
         colHabitantes.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getNum_habitantes()));
         colNumExt.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getNum_ext()));
         colNumInt.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getNum_int()));
         colMtsCuadrados.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getMtsCuadradosExactos()));
         colCalle.setCellValueFactory(data-> new SimpleObjectProperty<>(data.getValue().getIdCalle()).asString());
    }

    @FXML
    void regresar(ActionEvent event) throws IOException {
        /// CARGA LA VISTA DE LA INTERFAZ DE LOGIN
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/MenuPrincipal.fxml"));
        Parent root = loader.load();

        /// OBTIENE LA VENTANA ACTUAL
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // ANIMACIÓN DE SALIDA
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {

            ///CAMBIA A LA ESCENA DE LOGIN
            Scene nuevaEscena = new Scene(root);
            stage.setScene(nuevaEscena);

            // ANIMACIÓN DE ENTRADA
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void llenarCamposRelacionados(int idVivienda) {

        PropietarioBD duenio = PRDB.buscarPropietarioID(idVivienda);
        if (duenio != null) {

            PersonaBD persona = PDB.buscarPersonaID(duenio.getId_persona());
            if (persona != null) {
                propietario.setValue(persona);
            }
        }


        if ("Departamento".equals(vivienda.getValue())) {
            DepartamentoBD departamento = DDB.buscarDepartamentoVivienda(idVivienda);
            if (departamento != null) {
                EdificioBD edificioBD = EDB.buscarEdificioID(departamento.getId_edificio());
                edificio.setValue(edificioBD);
                piso.setText(String.valueOf(departamento.getPiso()));
            }
        }
    }



}
