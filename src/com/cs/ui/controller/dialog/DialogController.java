/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.ui.controller.dialog;

import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.ui.controller.ControllerCRUD_UI;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.tools.DiagnosticCollector;

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

    public DialogController() {
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

    public void setTipe(final int tipe) {
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

    public static ProgressDialogController getProgressDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ControllerCRUD_UI.class.getResource("fxml/dialog/dialogProgress.fxml"));
        GridPane page = null;
        try {
            page = (GridPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(DiagnosticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Cria o palco dialogStage.
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ControllerTelas.stage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Define a pessoa no controller.
        ProgressDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.setResizable(false);
       
        return controller;
    }
}
