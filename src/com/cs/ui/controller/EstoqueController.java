package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import com.cs.ui.controller.dialog.DialogController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class EstoqueController extends ControllerUI<Produto> {

    @FXML
    private TableColumn colunaDesc;
    @FXML
    private TableColumn colunaCod;
    @FXML
    private TableColumn colunaEst;
    @FXML
    private TableColumn colunaVal;

    @Override
    protected void preencherTabela() {
        colunaDesc.setCellValueFactory(new PropertyValueFactory<Cliente, String>("descricao"));
        colunaCod.setCellValueFactory(new PropertyValueFactory<Cliente, Double>("codigoDeBarras"));
        colunaEst.setCellValueFactory(new PropertyValueFactory<Cliente, String>("quantidadeEmEstoque"));
        colunaVal.setCellValueFactory(new PropertyValueFactory<Cliente, String>("valorDeVenda"));

        JavaFXUtil.colunValueModedaFormat(colunaVal);
        JavaFXUtil.colunValueQuantidadeFormat(colunaEst);
        
        atualizarLista();

    }
    
    @Override
    public void atualizarLista(){
        try {
            String text = pesquisa.getText();
            if(text.length() < 3 && !tabela.getItems().isEmpty()){
                return;
            }
            tabela.getItems().clear();
            observavel = f.buscarListaProdutoPorDescricaoOuCodigo(text, VariaveisDeConfiguracaoUtil.LIMITE_DE_REGISTROS_EXIBIDOS);
            tabela.setItems(FXCollections.observableList(observavel));
            configurarColunaEditar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @FXML
    public void digitado(KeyEvent event) {
        String text = pesquisa.getText();
        atualizarLista();
        if (observavel.size() == 1 && event.getCode().isLetterKey()) {
            int i = text.length();
            pesquisa.setText(observavel.get(0).getCodigoDeBarras().startsWith(text) ? observavel.get(0).getCodigoDeBarras() : observavel.get(0).getDescricao());
            pesquisa.selectRange(i, pesquisa.getText().length());
        }
    }

    @Override
    @FXML
    public void abrirModalEdit() {
        super.showDialog("dialogProduto", editar, DialogController.EDIT_MODAL);
    }

    @Override
    @FXML
    public void abrirModalCreate() {
        super.showDialog("dialogProduto", editar, DialogController.CREATE_MODAL);
    }

}
