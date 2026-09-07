package Controllers;

import ObjetosBD.Edificio.EdificioBD;
import ObjetosBD.Edificio.JDEdificio;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaBD;
import ObjetosBD.Departamento.DepartamentoBD;
import ObjetosBD.Departamento.JDDepartamento;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.*;

enum operacionDepartamento {
    CREAR("Crear"),
    BUSCAR("Buscar"),
    BORRAR("Borrar"),
    ACTUALIZAR("Seleccionar departamento");

    private final String texto;

    operacionDepartamento(String texto){
        this.texto = texto;
    }

    public String getTextoBoton(){
        return this.texto;
    }
}

public class DepartamentoController {
    // Componentes generales
    @FXML private Button btn_menuPrincipal;
    @FXML private Text out_infoOperacion;
    @FXML private Button btn_volver;

    // Paneles
    @FXML private AnchorPane pane_inicio;
    @FXML private AnchorPane pane_entrada;
    @FXML private AnchorPane pane_actualizar;
    @FXML private AnchorPane pane_resultadoBusqueda;
    @FXML private AnchorPane pane_confirmacion;

    // Campos de entrada (pane_entrada)
    @FXML private TextField in_idDepartamento;
    @FXML private TextField in_idEdificio;
    @FXML private TextField in_idVivienda;
    @FXML private TextField in_piso;
    @FXML private Button btn_entrada;
    @FXML private Text txt_idDepartamentoEntrada;
    @FXML private Text txt_edificioEntrada;
    @FXML private Text txt_viviendaEntrada;
    @FXML private Text txt_pisoEntrada;

    // Campos de actualizar (pane_actualizar)
    @FXML private TextField field_idDepartamentoActualizar;
    @FXML private TextField field_idEdificioActualizar;
    @FXML private TextField field_idViviendaActualizar;
    @FXML private TextField field_pisoActualizar;
    @FXML private Button btn_confirmacionActualizar;

    // Campos de confirmacion (pane_confirmacion)
    @FXML private TextField out_idDepartamentoRegistro;
    @FXML private TextField out_edificioRegistro;
    @FXML private TextField out_pisoRegistro;
    @FXML private TextField out_idViviendaRegistro;
    @FXML private TextField out_tipoVivRegistro;
    @FXML private Button btn_confirmacionFinal;

    // Tabla de busqueda
    @FXML private TableView<Map<String, Object>> tabla_busquedaDepartamento;
    @FXML private TableColumn<Map<String, Object>, Object> colIdDepartamento;
    @FXML private TableColumn<Map<String, Object>, Object> colIdEdificio;
    @FXML private TableColumn<Map<String, Object>, Object> colNombreEdificio;
    @FXML private TableColumn<Map<String, Object>, Object> colIdVivienda;
    @FXML private TableColumn<Map<String, Object>, Object> colTipoVivienda;
    @FXML private TableColumn<Map<String, Object>, Object> colPiso;

    // Variables de control
    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private operacionDepartamento opSeleccionada;
    private final Map<AnchorPane, AnchorPane> padrePane = new HashMap<>();
    private AnchorPane pane_actual;

    // Objetos BD
    private final JDDepartamento DBDepartamento = new JDDepartamento();
    private final JDEdificio DBEdificio = new JDEdificio();
    private final JDVivienda DBVivienda = new JDVivienda();

    private EdificioBD edificioBD;
    private ViviendaBD viviendaBD;
    private DepartamentoBD departamentoBD;

    @FXML
    public void initialize() {
        pane_actual = pane_inicio;
        initPadrePane();
        configurarTablaBusqueda();
    }

    private void initPadrePane() {
        padrePane.put(pane_inicio, pane_inicio);
        padrePane.put(pane_entrada, pane_inicio);
        padrePane.put(pane_confirmacion, pane_entrada);
        padrePane.put(pane_actualizar, pane_entrada);
        padrePane.put(pane_resultadoBusqueda, pane_entrada);
    }

    private void cambiarPane(AnchorPane origen, AnchorPane destino) {
        origen.setVisible(false);
        destino.setVisible(true);
        pane_actual = destino;
    }

    @FXML
    void elegirOpcionCrear(ActionEvent event) {
        opSeleccionada = operacionDepartamento.CREAR;
        prepararPaneEntrada();
    }

    @FXML
    void elegirOpcionBuscar(ActionEvent event) {
        opSeleccionada = operacionDepartamento.BUSCAR;
        prepararPaneEntrada();
    }

    @FXML
    void elegirOpcionActualizar(ActionEvent event) {
        opSeleccionada = operacionDepartamento.ACTUALIZAR;
        prepararPaneEntrada();
    }

    @FXML
    void elegirOpcionBorrar(ActionEvent event) {
        opSeleccionada = operacionDepartamento.BORRAR;
        prepararPaneEntrada();
    }

    private void prepararPaneEntrada() {
        btn_entrada.setText(opSeleccionada.getTextoBoton());
        limpiarCamposEntrada();

        boolean esBuscar = opSeleccionada == operacionDepartamento.BUSCAR;
        boolean esCrear = opSeleccionada == operacionDepartamento.CREAR;

        txt_idDepartamentoEntrada.setVisible(true);
        in_idDepartamento.setVisible(true);

        if (esCrear) {
            in_idDepartamento.setDisable(true);
            in_idDepartamento.setText("Auto");
        } else {
            in_idDepartamento.setDisable(false);
        }

        txt_edificioEntrada.setVisible(esCrear || esBuscar);
        in_idEdificio.setVisible(esCrear || esBuscar);

        txt_viviendaEntrada.setVisible(esCrear || esBuscar);
        in_idVivienda.setVisible(esCrear || esBuscar);

        txt_pisoEntrada.setVisible(esCrear || esBuscar);
        in_piso.setVisible(esCrear || esBuscar);

        cambiarPane(pane_inicio, pane_entrada);
        btn_volver.setVisible(true);
        out_infoOperacion.setVisible(false);
    }

    private void limpiarCamposEntrada() {
        in_idDepartamento.clear();
        in_idEdificio.clear();
        in_idVivienda.clear();
        in_piso.clear();
    }

    @FXML
    void ejecutarOperacionEntrada(ActionEvent event) {
        out_infoOperacion.setVisible(false);
        switch (opSeleccionada) {
            case CREAR:
                if (validarCamposCrear()) {
                    cargarDatosConfirmacion();
                    btn_confirmacionFinal.setText("Crear departamento");
                    cambiarPane(pane_entrada, pane_confirmacion);
                }
                break;
            case BUSCAR:
                operacionBuscar();
                break;
            case ACTUALIZAR:
                if (validarId(in_idDepartamento, "departamento")) {
                    departamentoBD = DBDepartamento.buscarDepartamento(Integer.parseInt(in_idDepartamento.getText()));
                    if (departamentoBD != null) {
                        field_idDepartamentoActualizar.setText(String.valueOf(departamentoBD.getId_departamento()));
                        field_idEdificioActualizar.setText(String.valueOf(departamentoBD.getId_edificio()));
                        field_idViviendaActualizar.setText(String.valueOf(departamentoBD.getId_vivienda()));
                        field_pisoActualizar.setText(String.valueOf(departamentoBD.getPiso()));
                        cambiarPane(pane_entrada, pane_actualizar);
                    } else {
                        mostrarInfoOperacion("El departamento no existe", colorAdvertencia);
                    }
                }
                break;
            case BORRAR:
                if (validarId(in_idDepartamento, "departamento")) {
                    departamentoBD = DBDepartamento.buscarDepartamento(Integer.parseInt(in_idDepartamento.getText()));
                    if (departamentoBD != null) {
                        edificioBD = DBEdificio.buscarEdificioID(departamentoBD.getId_edificio());
                        viviendaBD = DBVivienda.buscarVivienda(departamentoBD.getId_vivienda());
                        cargarDatosConfirmacion();
                        btn_confirmacionFinal.setText("Borrar departamento");
                        cambiarPane(pane_entrada, pane_confirmacion);
                    } else {
                        mostrarInfoOperacion("El departamento no existe", colorAdvertencia);
                    }
                }
                break;
        }
    }

    private void cargarDatosConfirmacion() {
        if (opSeleccionada == operacionDepartamento.CREAR) {
            out_idDepartamentoRegistro.setText("Auto");
        } else {
            out_idDepartamentoRegistro.setText(departamentoBD != null ? String.valueOf(departamentoBD.getId_departamento()) : in_idDepartamento.getText());
        }
        out_edificioRegistro.setText(edificioBD != null ? edificioBD.getNombre() : "N/A");
        out_pisoRegistro.setText(departamentoBD != null ? String.valueOf(departamentoBD.getPiso()) : in_piso.getText());
        out_idViviendaRegistro.setText(viviendaBD != null ? String.valueOf(viviendaBD.getId_vivienda()) : in_idVivienda.getText());
        out_tipoVivRegistro.setText(viviendaBD != null ? viviendaBD.getTipo() : "N/A");
    }

    private boolean validarCamposCrear() {
        if (!validarId(in_idEdificio, "edificio")) return false;
        if (!validarId(in_idVivienda, "vivienda")) return false;
        if (in_piso.getText().isBlank()) {
            mostrarInfoOperacion("El campo piso es necesario", colorAdvertencia);
            return false;
        }

        edificioBD = DBEdificio.buscarEdificioID(Integer.parseInt(in_idEdificio.getText()));
        if (edificioBD == null) {
            mostrarInfoOperacion("El edificio no existe", colorAdvertencia);
            return false;
        }

        viviendaBD = DBVivienda.buscarVivienda(Integer.parseInt(in_idVivienda.getText()));
        if (viviendaBD == null) {
            mostrarInfoOperacion("La vivienda no existe", colorAdvertencia);
            return false;
        }

        return true;
    }

    private boolean validarId(TextField field, String entidad) {
        if (field.getText().isBlank()) {
            mostrarInfoOperacion("El Id de " + entidad + " es necesario", colorAdvertencia);
            return false;
        }
        if (!field.getText().matches("[0-9]+")) {
            mostrarInfoOperacion("El Id de " + entidad + " debe ser un número", colorAdvertencia);
            return false;
        }
        return true;
    }

    private void operacionBuscar() {
        Integer idDep = null;
        if (!in_idDepartamento.getText().isBlank() && !in_idDepartamento.getText().equals("Auto")) {
            if (in_idDepartamento.getText().matches("[0-9]+")) idDep = Integer.parseInt(in_idDepartamento.getText());
        }
        Integer idEdi = in_idEdificio.getText().isBlank() ? null : Integer.parseInt(in_idEdificio.getText());
        Integer idViv = in_idVivienda.getText().isBlank() ? null : Integer.parseInt(in_idVivienda.getText());
        Integer piso = in_piso.getText().isBlank() ? null : Integer.parseInt(in_piso.getText());

        ObservableList<Map<String, Object>> resultados = DBDepartamento.buscarDepartamentos(idDep, idEdi, idViv, piso);
        if (resultados != null) {
            tabla_busquedaDepartamento.setItems(resultados);
            cambiarPane(pane_entrada, pane_resultadoBusqueda);
            mostrarInfoOperacion("Resultados encontrados: " + resultados.size(), colorExito);
        } else {
            mostrarInfoOperacion("No se encontraron departamentos", colorAdvertencia);
        }
    }

    @FXML
    void ejecutarOperacionConfirmacion(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                int idGenerado = DBDepartamento.insertarDepartamento(
                        Integer.parseInt(in_idEdificio.getText()),
                        Integer.parseInt(in_idVivienda.getText()),
                        Integer.parseInt(in_piso.getText())
                );
                if (idGenerado != -1) {
                    mostrarInfoOperacion("Departamento creado. ID: " + idGenerado, colorExito);
                    limpiarCamposEntrada();
                } else {
                    mostrarInfoOperacion("Error al crear departamento", colorAdvertencia);
                }
                cambiarPane(pane_confirmacion, pane_entrada);
                break;
            case ACTUALIZAR:
                boolean ok = DBDepartamento.actualizarDepartamento(
                        Integer.parseInt(field_idDepartamentoActualizar.getText()),
                        Integer.parseInt(field_idEdificioActualizar.getText()),
                        Integer.parseInt(field_idViviendaActualizar.getText()),
                        Integer.parseInt(field_pisoActualizar.getText())
                );
                if (ok) {
                    mostrarInfoOperacion("Departamento actualizado correctamente", colorExito);
                } else {
                    mostrarInfoOperacion("Error al actualizar departamento", colorAdvertencia);
                }
                cambiarPane(pane_actualizar, pane_entrada);
                break;
            case BORRAR:
                boolean borrado = DBDepartamento.eliminarDepartamento(departamentoBD.getId_departamento());
                if (borrado) {
                    mostrarInfoOperacion("Departamento eliminado correctamente", colorExito);
                } else {
                    mostrarInfoOperacion("Error al eliminar departamento", colorAdvertencia);
                }
                cambiarPane(pane_confirmacion, pane_entrada);
                break;
        }
    }

    private void configurarTablaBusqueda() {
        colIdDepartamento.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("IdDepartamento")));
        colIdEdificio.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("IdEdificio")));
        colNombreEdificio.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("NombreEdificio")));
        colIdVivienda.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("IdVivienda")));
        colTipoVivienda.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("TipoVivienda")));
        colPiso.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("Piso")));
    }

    @FXML
    void volverMenuPrincipal(ActionEvent event) {
        // Implementar según navegación del sistema
    }

    @FXML
    void volverVentanaAnterior(ActionEvent event) {
        cambiarPane(pane_actual, padrePane.get(pane_actual));
        out_infoOperacion.setVisible(false);
        if (pane_actual == pane_inicio) btn_volver.setVisible(false);
    }

    public void mostrarInfoOperacion(String mensaje, Color color) {
        out_infoOperacion.setFill(color);
        out_infoOperacion.setText(mensaje);
        out_infoOperacion.setVisible(true);
    }
}
