package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import com.cs.ui.controller.dialog.DialogController;
import com.cs.ui.controller.dialog.ProgressDialogController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
//criar uma classe abstrata controller ui e colocar os metodos que se repetem
public class ClienteController extends ControllerUI<Cliente> {

    @FXML
    private TableColumn colunaNome;
    @FXML
    private TableColumn colunaDebt;
    @FXML
    private TableColumn colunaTel;
    @FXML
    private TableColumn colunaEnd;
    @FXML
    private TableColumn colunaCpf;

    public ClienteController() {
        super();
    }

    @Override
    public void preencherTabela() {

        colunaNome.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nome"));
        colunaDebt.setCellValueFactory(new PropertyValueFactory<Cliente, Double>("debito"));
        colunaTel.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefones"));
        colunaEnd.setCellValueFactory(new PropertyValueFactory<Cliente, String>("endereco"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<Cliente, String>("cpf"));

        JavaFXUtil.colunValueMoedaFormat(colunaDebt);

        atualizarLista();
    }

    @FXML
    protected void digitado(KeyEvent event) {
        String text = pesquisa.getText();
        atualizarLista();
        if (observavel.size() == 1 && event.getCode().isLetterKey()) {
            int i = text.length();
            pesquisa.setText(observavel.get(0).getNome());
            pesquisa.selectRange(i, observavel.get(0).getNome().length());
        }
    }

    @Override
    protected void abrirModalEdit() {
        super.showDialog("dialogCliente", editar, DialogController.EDIT_MODAL);
    }

    @Override
    protected void abrirModalCreate() {
        super.showDialog("dialogCliente", editar, DialogController.CREATE_MODAL);
    }

    @Override
    public void atualizarLista() {
        try {
            String text = pesquisa.getText();
            tabela.getItems().clear();
            observavel = f.buscarClientePorCPFOuNomeQueIniciam(text, VariaveisDeConfiguracaoUtil.LIMITE_DE_REGISTROS_EXIBIDOS);
            tabela.setItems(FXCollections.observableList(observavel));
            configurarColunaEditar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
