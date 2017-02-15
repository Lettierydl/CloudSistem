/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.ui.controller.dialog;

import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class EntradaProdutoDialogController extends DialogController<Produto> {

    @FXML
    private Label estoque;

    @FXML
    private Label codigo;   

    @FXML
    private TextField entrada;

    @FXML
    private Label descricao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        MaskFieldUtil.quantityField(entrada);
        JavaFXUtil.nextFielOnAction(entrada, okButton);
        
    }

    @Override
    protected void okClicado() {
        double newEntrada = OperacaoStringUtil.converterStringValor(entrada.getText());
        Produto p = f.buscarProdutoPorId(entity.getId());
        p.acrescentarQuantidadeDeEstoque(newEntrada);
        try {
            f.atualizarProduto(p);
            JavaFXUtil.showDialog(
                            "Quantidade Alterada"
                            ,"Entrada de produto registrada com sucesso!"
                            ,"Nova quantidade em estoque: "+
                                    OperacaoStringUtil.formatarStringQuantidade(p.getQuantidadeEmEstoque()))
                            ;
        } catch (FuncionarioNaoAutorizadoException ex) {
             JavaFXUtil.showDialog(ex);
        } catch (Exception ex) {
            Logger.getLogger(EntradaProdutoDialogController.class.getName()).log(Level.SEVERE, null, ex);
             JavaFXUtil.showDialog("Erro ao atualizar Produto",ex);
        }
        dialogStage.close();
        okClicked = true;
    }

    @Override
    public boolean isEntradaValida() {
        //
        return true;
    }

    @Override
    public void setEntity(Produto entity) {
        this.entity = entity;
        this.codigo.setText(entity.getCodigoDeBarras());
        this.descricao.setText(entity.getDescricao());
        this.estoque.setText(OperacaoStringUtil.formatarStringQuantidade(entity.getQuantidadeEmEstoque()));
    }

    @Override
    public String getTitulo() {
        return "";
    }

}
