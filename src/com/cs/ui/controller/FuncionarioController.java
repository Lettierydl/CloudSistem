package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.ui.controller.dialog.DialogController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class FuncionarioController extends ControllerUI<Funcionario> {

    @FXML
    private TableColumn colunaNome;
    @FXML
    private TableColumn colunaCPF;
    @FXML
    private TableColumn colunaTel;
    @FXML
    private TableColumn colunaFun;
    
    public FuncionarioController(){
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
        if (f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
            super.showDialog("dialogFuncionario", editar, DialogController.CREATE_MODAL);
        } else {
            Dialogs.create()
                    .title("Funcionário não autorizado")
                    .masthead("Por favor, entre com um usuário diferente")
                    .showError();
        }
    }

    @Override
    @FXML
    public void abrirModalEdit() {
        if (f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
            super.showDialog("dialogFuncionario", editar, DialogController.EDIT_MODAL);
        } else {
            Dialogs.create()
                    .title("Funcionário não autorizado")
                    .masthead("Por favor, entre com um usuário diferente")
                    .showError();
        }
    }

    @Override
    public void atualizarLista() {
        try{
            String text = pesquisa.getText();
        
            tabela.getItems().clear();
            observavel = f.buscarFuncionarioPorNomeQueInicia(text);
            configurarColunaEditar();
        
            tabela.setItems(FXCollections.observableList(observavel));
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

}
