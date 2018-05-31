package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.util.JavaFXUtil;
import com.cs.ui.controller.dialog.DialogController;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class FuncionarioController extends ControllerCRUD_UI<Funcionario> {

    @FXML
    private TableColumn colunaNome;
    @FXML
    private TableColumn colunaCPF;
    @FXML
    private TableColumn colunaTel;
    @FXML
    private TableColumn colunaFun;

    public FuncionarioController() {
        super();

    }

    @Override
    public void preencherTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("nome"));
        colunaCPF.setCellValueFactory(new PropertyValueFactory<Funcionario, Double>("cpf"));
        colunaTel.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("telefones"));
        colunaFun.setCellValueFactory(new PropertyValueFactory<Funcionario, String>("tipoDeFuncionario"));
        atualizarLista();
    }

    @Override
    @FXML
    public void digitado(KeyEvent event) {
        String text = pesquisa.getText();
        atualizarLista();
        if (observavel.size() == 1 && event.getCode().isLetterKey()) {
            int i = text.length();
            pesquisa.setText(observavel.get(0).getNome());
            pesquisa.selectRange(i, pesquisa.getText().length());
        }
    }

    @Override
    @FXML
    protected void abrirModalCreate() {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/dialogFuncionario.fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<Funcionario> controller = loader.getController();
            controller.setTipe(DialogController.CREATE_MODAL);
            controller.setDialogStage(dialogStage);
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();

            tabela.getItems().clear();
            atualizarLista();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @FXML
    public void abrirModalEdit() {
        if (f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
            super.showDialog("dialogFuncionario", editar, DialogController.EDIT_MODAL);
        } else {
            JavaFXUtil.showDialog("Funcionário não autorizado",
                    "Por favor, entre com um usuário diferente",
                    Alert.AlertType.ERROR);
        }
    }

    @Override
    public void atualizarLista() {
        try {
            String text = pesquisa.getText();

            tabela.getItems().clear();
            observavel = f.buscarFuncionarioPorNomeQueInicia(text);
            configurarColunaEditar();

            tabela.setItems(FXCollections.observableList(observavel));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
