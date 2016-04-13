package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ui.controller.ClienteController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class ProgressDialogController implements Initializable {

    @FXML
    ProgressIndicator bar;

    @FXML
    Label titulo;

    @FXML
    Label menssagem;

    @FXML
    Button okButton;

    int sleep_time = 1;
    double sleep_final = 0.90;
    private Stage dialogStage;

    private String wait_msg;
    private String titulo_msg;

    public ProgressDialogController() {
        super();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Stage getDialogStage() {
        return this.dialogStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //bar.setProgress(0.01);
        setProgress(-1.0);
    }

    public void indefinido() {
        bar.setProgress(-1.0);
    }

    public void addProgress(double d) {
        bar.setProgress(getProgress() + d);
    }

    public void setProgress(double d) {
        bar.setProgress(d);
    }

    public double getProgress() {
        return bar.getProgress();
    }

    public String getWait_msg() {
        return wait_msg;
    }

    public void setWait_msg(String wait_msg) {
        this.wait_msg = wait_msg;
        this.titulo.setText(wait_msg);
    }

    public String getTitulo_msg() {
        return titulo_msg;
    }

    public void setTitulo_msg(String titulo_msg) {
        this.titulo_msg = titulo_msg;
        this.titulo.setText(titulo_msg);
    }

    public void concluir(String tituloConclusao, String messagemConclusao) {
        Platform.runLater(() -> {
            menssagem.setText(messagemConclusao);
            titulo.setText(tituloConclusao);
            this.okButton.setDisable(false);
            this.okButton.setVisible(true);
            setProgress(1.0);
        }
        );
    }

    @FXML
    public void okClicado() {
        dialogStage.close();
    }

    public void setSleepTime(int segundos) {
        sleep_time = segundos;
    }

    public void setSleepWait(double espera) {
        sleep_final = espera;
    }

    public void show() {
        Platform.runLater(() -> {
            getDialogStage().showAndWait();
        }
        );
    }

    /*não funcina, trava a tela até finalizar tudo*/
    public void start() {

        Platform.runLater(() -> {

            setProgress(0.0);
            while (getProgress() < 1.0) {
                try {
                    Thread.sleep(7 * sleep_time);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClienteController.class.getName()).log(Level.SEVERE, null, ex);
                }
                addProgress(0.01);
                if (sleep_final <= getProgress()) {
                    return;
                }
            }
            setProgress(1.0);
        }
        );

    }

}
