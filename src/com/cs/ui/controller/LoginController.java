package com.cs.ui.controller;

import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.exception.LoginIncorretoException;
import com.cs.sis.model.exception.SenhaIncorretaException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/**
 *
 * @author Lettiery
 */
public class LoginController implements Initializable {

    private Facede f;

    @FXML
    private Label menssagem;

    @FXML
    private TextField nome;
    @FXML
    private PasswordField senha;

    @FXML
    private Button entrar;

    @FXML
    private void login(ActionEvent event) {
        try {
            f = Facede.getInstance();
        } catch (Error | Exception er) {
            JavaFXUtil.showDialog("Erro ao Conectar com o banco",er);
        }
        try{
           if(f.isBloqueado()){
            JavaFXUtil.showDialog("Avaliação Encerrada",
                    "Sistema bloqueado!\nPeríodo de avaliação finalizado.",
                    "Entre em contato com o suporte para ativar o seu sistema.",
                    AlertType.ERROR);
            return;
            }
        }catch(NullPointerException ne){}
        String name = nome.getText();
        String pass = senha.getText();
        try {
            f.login(name, pass);
        } catch (LoginIncorretoException | SenhaIncorretaException ex) {
            menssagem.setText("Usuário ou senha inválido!");
            nome.requestFocus();
            nome.selectAll();
            return;
        }
        Main.trocarDeTela(ControllerTelas.TELA_PRINCIPAL);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        entrar.defaultButtonProperty().bind(entrar.focusedProperty());
        MaskFieldUtil.upperCase(nome);
        JavaFXUtil.nextFielOnAction(nome, senha);
        JavaFXUtil.nextFielOnAction(senha, entrar);

        nome.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.isShiftDown() && t.getCode().equals(KeyCode.ESCAPE)) {
                Main.trocarDeTela(ControllerTelas.TELA_CONFIGURACAO_SISTEMA);
            }
        });

        Timeline delayTimeline = new Timeline();
        delayTimeline.setCycleCount(Timeline.INDEFINITE);
        delayTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                    int timout = 10;
                    @Override
                    public void handle(ActionEvent event) {
                        if (Main.messagem_erro_fachada != null) {
                            delayTimeline.stop();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    JavaFXUtil.showDialog("Erro ao Conectar com o banco",
                                    Main.messagem_erro_fachada);
                            
                            }});
                            delayTimeline.stop();
                        }else if(timout-- <= 0){
                            delayTimeline.stop();
                        }
                    }
                })
        );
        delayTimeline.play();

    }

}
