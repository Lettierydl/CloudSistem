/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.ui.controller;

import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.ui.controller.dialog.DialogController;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 *
 * @author Lettiery
 * @param <T>
 */
public abstract class ControllerUI<T> implements Initializable {

    protected Facede f;
    protected boolean pesquisa_ativa = false;
    protected T editar = null;

    protected List<T> observavel;

    @FXML
    protected TextField pesquisa;
    @FXML
    protected TableView<T> tabela;

    @FXML
    protected TableColumn colunaEdit;

    protected ControllerUI() {
        f = Facede.getInstance();
    }

    /*
     Esse método é responsável por iniciar as propriedades das colunas
     e preencher os valores inicias da lista observavel
     */
    protected abstract void preencherTabela();

    /*
     Método responsável por abrir e configurar modal que edita entidade T
     */
    @FXML
    protected abstract void abrirModalEdit();

    /*
     Método responsável por abrir e configurar modal que cria a entidade T
     */
    @FXML
    protected abstract void abrirModalCreate();

    /**
     * Método que atuliza a lista de entidades pela pesquisa
     */
    protected abstract void atualizarLista();

    @FXML
    protected abstract void digitado(KeyEvent event);

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preencherTabela();
        tabela.setItems(FXCollections.observableList(observavel));
        configurarColunaEditar();
        MaskFieldUtil.upperCase(pesquisa);
        FadeTransition fade = new FadeTransition();
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDuration(new Duration(300));
        fade.setNode(pesquisa);
        fade.play();
        JavaFXUtil.beginFoccusTextField(pesquisa);
        
        pesquisa.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    digitado(event);
                }
            }
        });
        
    }

    @FXML
    protected void pesquisa(ActionEvent event) {
        Duration duracao = new Duration(300);
        if (pesquisa_ativa) {
            FadeTransition fade = new FadeTransition();
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDuration(duracao);
            fade.setNode(pesquisa);
            fade.play();
            pesquisa.requestFocus();
            pesquisa.setText("");
        } else {
            FadeTransition fade = new FadeTransition();
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setDuration(duracao);
            fade.setNode(pesquisa);
            fade.play();
            pesquisa.setText("");
            atualizarLista();
        }
        pesquisa_ativa = !pesquisa_ativa;
    }

    @FXML
    protected void configurarColunaEditar() {
        colunaEdit.setComparator(new Comparator<T>() {
            @Override
            public int compare(T p1, T p2) {
                return p1.toString().compareToIgnoreCase(p2.toString());
            }
        });
        colunaEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, T>, ObservableValue<T>>() {
            @Override
            public ObservableValue<T> call(TableColumn.CellDataFeatures<T, T> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colunaEdit.setCellFactory(new Callback<TableColumn<T, T>, TableCell<T, T>>() {
            @Override
            public TableCell<T, T> call(TableColumn<T, T> btnCol) {
                return new TableCell<T, T>() {
                    @Override
                    public void updateItem(final T obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final Button button = new Button("Editar");
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                editar = obj;
                                abrirModalEdit();
                            }
                        });
                    }
                };
            }
        });
    }

    public boolean showDialog(String nameDialog, T entity, int tipe) {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/" + nameDialog + ".fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<T> controller = loader.getController();
            controller.setTipe(tipe);
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();

            tabela.getItems().clear();
            atualizarLista();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
