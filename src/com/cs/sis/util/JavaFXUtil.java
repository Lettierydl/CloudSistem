/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.util;

import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.model.pessoas.Pessoa;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 *
 * @author Lettiery
 */
public class JavaFXUtil {
    
    public static void showDialog(String thead) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(thead);
        alert.showAndWait();
    }
    
    //information defalt
    public static void showDialog(String thead, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(thead);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showDialog(String titulo, String thead, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showDialog(String titulo, String thead, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);
        alert.setContentText(message);
        if(type == Alert.AlertType.ERROR){
            alert.initStyle(StageStyle.UNDECORATED);
        }
        alert.showAndWait();
    }
    
    public static void showDialog(String titulo, String thead, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);
        if(type == Alert.AlertType.ERROR){
            alert.initStyle(StageStyle.UNDECORATED);
        }
        alert.showAndWait();
    }
    
    public static void showDialog(String titulo, String thead, String message, Alert.AlertType type, boolean undecorated) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);
        alert.setContentText(message);

        if (undecorated) {
            alert.initStyle(StageStyle.UNDECORATED);
        }
        alert.showAndWait();
    }
    
    public static void showDialog(FuncionarioNaoAutorizadoException ex) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Funcionário não autorizado");
        alert.setHeaderText("Funcionário não autorizado");
        alert.setContentText("Por favor entre com um funcionário autorizado a realizar essa ação!");

        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();
    }
    
    public static void showDialog(String thead,Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(thead);
        alert.setContentText(ex.getMessage());

        alert.initStyle(StageStyle.UNDECORATED);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    public static void showDialog(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(ex.getLocalizedMessage());
        alert.setContentText(ex.getMessage());

        alert.initStyle(StageStyle.UNDECORATED);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    public static void showDialog(String titulo, String thead, Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);

        alert.initStyle(StageStyle.UNDECORATED);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    
    public static ButtonType showDialogOptions(String thead) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(thead);
        alert.initStyle(StageStyle.UNDECORATED);
        
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType sim = new ButtonType("Sim",ButtonBar.ButtonData.OK_DONE);
        
        alert.getButtonTypes().setAll(nao,sim, cancelar);
        

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == sim) {
            return ButtonType.YES;
        } else {
            return ButtonType.CANCEL;
        }
    }
    
    //String titulo, String thead, String message, Alert.AlertType type, boolean undecorated
    public static String showDialogSelectOptions(String titulo, String thead, String message, List<String> options) {
        if(options.isEmpty()){
            return "";
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle(titulo);
        dialog.setHeaderText(thead);
        dialog.setContentText(message);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }else{
            return "";
        }

    }
    
    public static ButtonType showDialogOptions(String thead, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(thead);
        alert.setContentText(message);
        
        alert.initStyle(StageStyle.UNDECORATED);
            
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType sim = new ButtonType("Sim",ButtonBar.ButtonData.OK_DONE);
        
        alert.getButtonTypes().setAll(nao,sim, cancelar);
        

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == sim) {
            return ButtonType.YES;
        } else {
            return ButtonType.CANCEL;
        }
    }
    
    //WARNING defalt
    public static ButtonType showDialogOptions(String titulo, String thead, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);

        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType sim = new ButtonType("Sim",ButtonBar.ButtonData.YES);
        
        alert.getButtonTypes().setAll(nao,sim, cancelar);
        

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == sim) {
            return ButtonType.YES;
        } else {
            return ButtonType.CANCEL;
        }
    }
    
    public static ButtonType showDialogOptions(String titulo, String thead, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(thead);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);

        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType sim = new ButtonType("Sim",ButtonBar.ButtonData.OK_DONE);
        
        alert.getButtonTypes().setAll(nao,sim, cancelar);
        

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == sim) {
            return ButtonType.YES;
        } else {
            return ButtonType.CANCEL;
        }
    }
    
    public static String showDialogInput(String thead) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setHeaderText(thead);
        
        // Traditional way to get the response value.
        dialog.showAndWait();
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }
    
    public static String showDialogInput(String thead, String message) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setHeaderText(thead);
        dialog.setContentText(message);

        // Traditional way to get the response value.
        dialog.showAndWait();
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }

    public static String showDialogInput(String titulo, String thead, String message) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle(titulo);
        dialog.setHeaderText(thead);
        dialog.setContentText(message);
        

        // Traditional way to get the response value.
        dialog.showAndWait();
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }
    
    
    // serve para o sistema chamar o programa padrao que abre esse tipo de arquivo
    public static void abrirArquivoDoSistema(File file) throws IOException{
        Desktop.getDesktop().open(file);    
    } 
    
    // serve para o sistema chamar o programa padrao que abre esse tipo de arquivo
    public static void imprimirArquivoDoSistema(File file) throws IOException{
        Desktop.getDesktop().print(file);
    } 
    
    
    
    public static void colunValueMoedaFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Double, Number>() {
                    @Override
                    public void updateItem(Number price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatarStringValorMoeda(price.doubleValue()));
                        }
                    }
                });
    }

    public static void colunValueQuantidadeFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Double, Number>() {
                    @Override
                    public void updateItem(Number price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatarStringQuantidade(price.doubleValue()));
                        }
                    }
                });
    }
    
    public static void colunDataTimeFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Calendar, Calendar>() {
                    @Override
                    public void updateItem(Calendar price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatDataTimeValor(price));
                        }
                    }
                });
    }
    
    public static void nextFielOnAction(TextField action, Node foccus){
        action.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                foccus.requestFocus();
                if(foccus instanceof TextField){
                    ((TextField) foccus).selectAll();
                }else if(foccus instanceof Button){
                    ((Button) foccus).defaultButtonProperty().bind(((Button) foccus).focusedProperty());
                }
            }
        });
    }
    
    
    public static Calendar toDate(DatePicker datePicker) {
        if(datePicker.getValue() == null){
            return null;
        }
        LocalDate ld = datePicker.getValue();
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(Date.from(instant));
        return gc;
    }

    /**
     * Converte Date para LocalDate
     *
     * @param d
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Calendar d) {
        Instant instant = Instant.ofEpochMilli(d.getTime().getTime());
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static void beginFoccusTextField(TextField text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
                text.selectAll();
            }
        });
    }

    public static void colunPessoaFormat(TableColumn colunaPessoa) {
        colunaPessoa.setCellFactory(col
                -> new TableCell<Pessoa, Pessoa>() {
                    @Override
                    public void updateItem(Pessoa price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            try{
                                setText(price.getNome());
                            }catch(NullPointerException ne){
                                setText(null);
                            }
                        }
                    }
                });
    }
    
    public static void colunMapKeyStringFormat(TableColumn colunaPessoa) {
        colunaPessoa.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry, String> param) {
                String key = (String) param.getValue().getKey();
                return new SimpleObjectProperty<String>(key);
            }
        });
    }
    
    public static void colunMapValueStringFormat(TableColumn colunaPessoa) {
        colunaPessoa.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry, String> param) {
                String value = (String) param.getValue().getValue();
                return new SimpleStringProperty(value);
            }
        });
    }
    
    public static void colunMapKeyDoubleFormat(TableColumn colunaPessoa) {
        colunaPessoa.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry, String> param) {
                double value = (double) param.getValue().getKey();
                return new SimpleStringProperty(OperacaoStringUtil.formatarStringValorMoeda(value));
            }
        });
    }
    
    public static void colunMapValueDoubleFormat(TableColumn colunaPessoa) {
        colunaPessoa.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry, String> param) {
                double value = (double) param.getValue().getValue();
                return new SimpleStringProperty(OperacaoStringUtil.formatarStringValorMoeda(value));
            }
            
        });
        
    }

}
