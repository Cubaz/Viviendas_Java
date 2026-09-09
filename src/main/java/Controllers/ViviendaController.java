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

public class ViviendaController {

    private JDVivienda VDB;
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
    @FXML private TableColumn<ViviendaBD, Float> colMtsCuadrados;
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

    private void validar(){

        String tipo;
        int num_hab, numext, numint, num_piso = 0;
        float mtscuadrados;


        if(vivienda.getValue() == null){
            System.out.println("Es necesario seleccionar el tipo de vivienda");
            vivienda.requestFocus();
            return;
        }

        if(calle.getValue() == null){
            System.out.println("Es necesario seleccionar una calle");
            calle.requestFocus();
            return;
        }

        if(habitantes.getText().isBlank()){
            System.out.println("Es necesario especificar el número");
            habitantes.requestFocus();
            return;
        }

        if(num_ext.getText().isBlank()){
            System.out.println("Es necesario indicar el número exterior");
            num_ext.requestFocus();
            return;
        }

        if(num_int.getText().isBlank()){
            System.out.println("Es necesario indicar el número interior");
            num_int.requestFocus();
            return;
        }

        if(mts_cuadrados.getText().isBlank()){
            System.out.println("Es necesario indicar los metros cuadrados construidos");
            mts_cuadrados.requestFocus();
            return;
        }

        if(vivienda.getValue().equals("Departamento")){
            edificio.setDisable(true);
            piso.setDisable(true);

            if(edificio.getValue() == null){
                System.out.println("Es necesario indicar el edificio de la vivienda");
                edificio.requestFocus();
                return;
            }

            if(piso.getText().isBlank()){
                System.out.println("Es necesario indicar el piso de la vivienda");
                piso.requestFocus();
                return;
            }else {
                num_piso = Integer.parseInt(piso.getText());
            }
        }

        tipo = vivienda.getValue();
        CalleBD Calle = calle.getValue();
        int idCalle = Calle.getId_calle();

        num_hab = Integer.parseInt(habitantes.getText());
        numext = Integer.parseInt(num_ext.getText());
        numint = Integer.parseInt(num_int.getText());
        mtscuadrados = Float.parseFloat(mts_cuadrados.getText());


        int id = VDB.insertarVivienda(tipo, num_hab, numext, numint, idCalle, mtscuadrados);
        if (id != -1) {
            System.out.println("Registro exitoso \\nID de usuario: " + id);
            if(tipo.equals("Departamento")){
                EdificioBD Depa = edificio.getValue();
                int idEdificio = Depa.getIdEdificio();
                int ide = DDB.insertarDepartamento(idEdificio, id, num_piso);
                if(ide != -1){
                    System.out.println("Registro de departamento exitoso \\nID de edificio: " + ide);
                }else{
                    System.out.println("Error en el registro de DEPARTAMENTO");
                }
            }
        } else {
            System.out.println("Error en el registro de VIVIENDA");
        }

        PersonaBD Duenio = propietario.getValue();
        int idPropietario = Duenio.getIdPersona();
        int ideP = PRDB.insertarPropietario(id, idPropietario);
        if(ideP !=-1){
            System.out.println("Registro de propietario exitoso");
        }else{
            System.out.println("Error en el registro de PROPIETARIO");
        }
    }

    @FXML
    void buscar_vivienda(ActionEvent event) {
        limpiar();
        if (criterio.getValue() == null || parametro.getText().isBlank()) {
            mensajeOperacion = "Debe seleccionar un criterio y escribir un parámetro";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }

        ObservableList<ViviendaBD> resultado = FXCollections.observableArrayList();

        if (criterio.getValue().equals("ID de la Vivienda")) {
            try {
                int id = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaTabla(id);
            } catch (NumberFormatException e) {
                mensajeOperacion = "El ID debe ser numérico";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        } else if (criterio.getValue().equals("ID de Calle")) {
            try{
                int calle = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaCalleID(calle);
            } catch (Exception e) {
                mensajeOperacion = "El ID debe ser numérico";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }

        }else if(criterio.getValue().equals("Nombre de la Calle")){
            try{
                String calle = parametro.getText();
                resultado = VDB.buscarViviendaPorNombreCalle(calle);
            } catch (Exception e) {
                mensajeOperacion = "El ID debe ser numérico";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }

        }else if(criterio.getValue().equals("Nombre del Propietario")){
            try{
                String propietario = parametro.getText();
                resultado = VDB.buscarViviendaPorPropietario(propietario);
            } catch (Exception e) {
                mensajeOperacion = "El ID debe ser numérico";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        } else if (criterio.getValue().equals("ID del Propietario")) {
            try{
                int propietario = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaPorIDPropietario(propietario);
            } catch (Exception e) {
                mensajeOperacion = "El ID debe ser numérico";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        }else if (criterio.getValue().equals("ID del Edificio")) {
            try{
                int edificio = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaPorIDEdificio(edificio);
            } catch (Exception e) {
                mensajeOperacion = "El ID debe ser numérico";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        }else if (criterio.getValue().equals("Nombre del Edificio")) {
            try{
                String edificio = parametro.getText();
                resultado = VDB.buscarViviendaPorNombreEdificio(edificio);
            } catch (Exception e) {
                mensajeOperacion = "El ID debe ser de tipo String";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        } else if (criterio.getValue().equals("Número de Piso")) {
            try{
                int piso = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaPorNumeroPiso(piso);
            } catch (Exception e) {
                mensajeOperacion = "El número de piso debe ser de tipo int";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        }else if (criterio.getValue().equals("Metros Cuadrados constuidos")) {
            try{
                float cuadrado = Float.parseFloat(parametro.getText());
                resultado = VDB.buscarViviendaPorMtsCuadrados(cuadrado);
            } catch (Exception e) {
                mensajeOperacion = "Los metros cuadrados deben ser de tipo flotante";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        }
        else if (criterio.getValue().equals("Número Exterior")) {
            try{
                int numExt = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaPorNumExt(numExt);
            } catch (Exception e) {
                mensajeOperacion = "El número exterior debe ser de tipo int";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        }else if (criterio.getValue().equals("Número Interior")) {
            try{
                int numInt = Integer.parseInt(parametro.getText());
                resultado = VDB.buscarViviendaPorNumInt(numInt);
            } catch (Exception e) {
                mensajeOperacion = "El número interior debe ser de tipo flotante";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
        }

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
        ViviendaBD seleccionada = tabla_vivienda.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una vivienda con doble clic";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }

        if (habitantes.getText().isBlank() || num_ext.getText().isBlank() ||
                num_int.getText().isBlank() || mts_cuadrados.getText().isBlank()) {
            mensajeOperacion = "Debe llenar todos los campos";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }

        String tipo = vivienda.getValue();
        int numHab = Integer.parseInt(habitantes.getText());
        int numExt = Integer.parseInt(num_ext.getText());
        int numInt = Integer.parseInt(num_int.getText());
        float metros = Float.parseFloat(mts_cuadrados.getText());

        // Validar propietario
        PersonaBD duenio = propietario.getValue();
        if (duenio == null) {
            mensajeOperacion = "Debe seleccionar un propietario";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }
        int idPropietario = duenio.getIdPersona();

        // Validar calle
        CalleBD calleSeleccionada = calle.getValue();
        if (calleSeleccionada == null) {
            mensajeOperacion = "Debe seleccionar una calle";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }
        int idCalle = calleSeleccionada.getId_calle();

        // Validar edificio y piso solo si es departamento
        int idEdificio = -1;
        int numPiso = 0;


        if ("Departamento".equals(tipo)) {
            EdificioBD edificioSeleccionado = edificio.getValue();
            if (edificioSeleccionado == null) {
                mensajeOperacion = "Debe seleccionar un edificio";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
            idEdificio = edificioSeleccionado.getIdEdificio();

            if (piso.getText().isBlank()) {
                mensajeOperacion = "Debe indicar el piso";
                out_infoOperacion.setFill(colorAdvertencia);
                out_infoOperacion.setText(mensajeOperacion);
                return;
            }
            numPiso = Integer.parseInt(piso.getText());
        }

        // Actualizar vivienda
        // Actualizar vivienda
        boolean actualizado = VDB.actualizarVivienda(seleccionada.getId_vivienda(), tipo, numHab, numExt, numInt, idCalle, metros);

// Verificar si la vivienda ya tiene propietario
        PropietarioBD duenioExistente = PRDB.buscarPropietarioID(seleccionada.getId_vivienda());
        boolean propietarioActualizado;

        if (duenioExistente != null) {
            // Ya existe propietario → actualizar
            propietarioActualizado = PRDB.actualizarPropietario(seleccionada.getId_vivienda(), idPropietario);
        } else {
            // No existe propietario → insertar nuevo
            propietarioActualizado = PRDB.insertarPropietario(seleccionada.getId_vivienda(), idPropietario) != -1;
        }

// Actualizar departamento si aplica
        boolean departamentoActualizado = true;
        if ("Departamento".equals(tipo)) {
            DepartamentoBD dep = DDB.buscarDepartamentoVivienda(seleccionada.getId_vivienda());
            if (dep != null) {
                departamentoActualizado = DDB.actualizarDepartamento(dep.getId_departamento(), idEdificio, seleccionada.getId_vivienda(), numPiso);
            } else {
                // Si no existía departamento, crear uno nuevo
                departamentoActualizado = DDB.insertarDepartamento(idEdificio, seleccionada.getId_vivienda(), numPiso) != -1;
            }
        }

// Resultado final
        if (actualizado && propietarioActualizado && departamentoActualizado) {
            mensajeOperacion = "Vivienda actualizada correctamente";
            out_infoOperacion.setFill(colorExito);
            buscar_vivienda(null);
        } else {
            mensajeOperacion = "No se pudo actualizar la vivienda";
            out_infoOperacion.setFill(colorAdvertencia);
        }
        out_infoOperacion.setText(mensajeOperacion);

    }




    @FXML
    void eliminar_vivienda(ActionEvent event) {
        ViviendaBD seleccionada = tabla_vivienda.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una vivienda para eliminar";
            out_infoOperacion.setFill(colorAdvertencia);
            out_infoOperacion.setText(mensajeOperacion);
            return;
        }

        boolean eliminado = VDB.eliminarVivienda(seleccionada.getId_vivienda());

        if (eliminado) {
            mensajeOperacion = "Vivienda eliminada correctamente";
            out_infoOperacion.setFill(colorExito);
            buscar_vivienda(null);
        } else {
            mensajeOperacion = "No se pudo eliminar la vivienda";
            out_infoOperacion.setFill(colorAdvertencia);
        }
        out_infoOperacion.setText(mensajeOperacion);
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
        });

        criterio.getItems().addAll("ID de la Vivienda", "ID de Calle", "Nombre de la Calle", "ID del Propietario", "Nombre del Propietario", "ID del Edificio", "Nombre del Edificio", "Número de Piso", "Metros Cuadrados constuidos", "Número Exterior", "Número Interior");
        configurarTablaBusqueda();


        tabla_vivienda.setOnMouseClicked(event -> {
            limpiar();
            if (event.getClickCount()==1) {
                ViviendaBD seleccionada = tabla_vivienda.getSelectionModel().getSelectedItem();

                if (seleccionada != null) {
                    vivienda.setValue(seleccionada.getTipo());
                    habitantes.setText(String.valueOf(seleccionada.getNum_habitantes()));
                    num_ext.setText(String.valueOf(seleccionada.getNum_ext()));
                    num_int.setText(String.valueOf(seleccionada.getNum_int()));
                    mts_cuadrados.setText(String.valueOf(seleccionada.getMts_cuadrados()));

                    for (CalleBD c : calle.getItems()) {
                        if (c.getId_calle() == seleccionada.getIdCalle()) {
                            calle.setValue(c);
                            break;
                        }
                    }
                    llenarCamposRelacionados(seleccionada.getId_vivienda());

                }
            }
        });
    }

    private void configurarTablaBusqueda() {
         colIdVivienda.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getId_vivienda()));
         colTipoVivienda.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getTipo()));
         colHabitantes.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getNum_habitantes()));
         colNumExt.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getNum_ext()));
         colNumInt.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getNum_int()));
         colMtsCuadrados.setCellValueFactory(data->new SimpleObjectProperty<>(data.getValue().getMts_cuadrados()));
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
                // Buscar en la lista por ID
                PersonaBD existente = null;
                for (PersonaBD p : propietario.getItems()) {
                    if (p.getIdPersona() == persona.getIdPersona()) {
                        existente = p;
                        break;
                    }
                }
                if (existente != null) {
                    propietario.setValue(existente);
                } else {
                    propietario.getItems().add(persona);
                    propietario.setValue(persona);
                }
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



    public void limpiar(){
        vivienda.setValue(null);
        edificio.setValue(null);
        habitantes.setText("");
        num_ext.setText("");
        num_int.setText("");
        mts_cuadrados.setText("");
        propietario.setValue(null);
        calle.setValue(null);
        piso.setText("");
    }

}
