/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.ui.controller.dialog;

import com.cs.Facede;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author Lettiery
 */
public abstract class DialogController<T> implements Initializable {
    
    public static final int CREATE_MODAL = 1;
    public static final int EDIT_MODAL = 2;
    public static final int VIEW_MODAL = 3;
    
    protected Facede f;
    
    @FXML
    protected Button cancelButton;
    @FXML
    protected Button okButton;
    @FXML
    protected Label page;
    protected Stage dialogStage;
    protected boolean okClicked = false;
    protected int tipe = CREATE_MODAL;
    protected T entity;
        
    
    public DialogController(){
        f = Facede.getInstance();
    }
    
    @FXML
    protected abstract void okClicado();

    public abstract boolean isEntradaValida();

    public abstract void setEntity(T entity);
  
    public abstract String getTitulo();
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
    public void setTipe(final int tipe){
        this.tipe = tipe;
    }
    
    /**
     * Chamado quando o usuário clica Cancel.
     */
    @FXML
    protected void cancelarClicado() {
        dialogStage.close();
    }

    @Override
    public abstract void initialize(URL location, ResourceBundle resources);
    
}
