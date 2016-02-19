package com.cs;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Angie
 */
public class ControllerTelas {

    public static Stage stage;

    public static final String TELA_LOGIN = "ui/controller/fxml/login.fxml";
    public static final String TELA_PRINCIPAL = "ui/controller/fxml/principal.fxml";
    public static final String TELA_VENDA = "ui/controller/fxml/venda.fxml";
    public static final String TELA_MERCADORIAS = "ui/controller/fxml/mercadorias.fxml";
    public static final String TELA_FINALIZAR_A_VISTA = "ui/controller/fxml/finalizarAvista.fxml";
    public static final String TELA_FINALIZAR_A_PRAZO = "ui/controller/fxml/finalizarAprazo.fxml";
    public static final String TELA_CONFIGURACAO_SISTEMA = "ui/controller/fxml/configuracao_sistema.fxml";

    public ControllerTelas(Stage stage) {
        ControllerTelas.stage = stage;
    }

    public void mostrarTela(final String tela) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(tela));

        Scene scene = new Scene(root);

        scene.setRoot(root);
        stage.close();

        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(new javafx.scene.image.Image("com/cs/ui/img/logo.png"));
        stage.setTitle("CloudSistem");

        switch (tela) {
            case TELA_VENDA:
                adicionarTeclaDeAtalho(KeyCode.ESCAPE, TELA_PRINCIPAL, scene);
                adicionarTeclaDeAtalhoComShift(KeyCode.ESCAPE, TELA_MERCADORIAS, scene);
                adicionarTeclaDeAtalho(KeyCode.CONTROL, TELA_FINALIZAR_A_VISTA, scene);
                adicionarTeclaDeAtalho(KeyCode.ALT, TELA_FINALIZAR_A_PRAZO, scene);
                break;
            case TELA_MERCADORIAS:
                adicionarTeclaDeAtalho(KeyCode.ESCAPE, TELA_VENDA, scene);
                adicionarTeclaDeAtalhoComShift(KeyCode.P, TELA_PRINCIPAL, scene);
                break;
            case TELA_FINALIZAR_A_PRAZO:
                adicionarTeclaDeAtalho(KeyCode.ESCAPE, TELA_VENDA, scene);
                break;
            case TELA_FINALIZAR_A_VISTA:
                adicionarTeclaDeAtalho(KeyCode.ESCAPE, TELA_VENDA, scene);
                break;
            
                
        }
    }

    public void adicionarTeclaDeAtalho(KeyCode tecla, final String tela, Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.getCode() == tecla) {
                try {
                    mostrarTela(tela);
                } catch (IOException ex) {
                    Dialogs.create().showException(ex);
                }
            }
        });
    }

    public void adicionarTeclaDeAtalhoComShift(KeyCode tecla, final String tela, Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.isShiftDown() && t.getCode().equals(tecla)) {
                try {
                    mostrarTela(tela);
                } catch (IOException ex) {
                    Dialogs.create().showException(ex);
                }
            }
        });
    }

    public void adicionarTeclaDeAtalhoComCtr(KeyCode tecla, final String tela, Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.isControlDown() && t.getCode().equals(tecla)) {
                try {
                    mostrarTela(tela);
                } catch (IOException ex) {
                    Dialogs.create().showException(ex);
                }
            }
        });
    }

}
