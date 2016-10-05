package com.cs;

import com.cs.ui.img.IMG;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
    
    public static Map<String , Scene> preCarregados = new HashMap<String , Scene>();

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
        
        System.out.println("\n\n\n\nChamou... "+tela);
        long antes = Calendar.getInstance().getTimeInMillis();
        
        Scene scene;
        if(preCarregados.containsKey(tela)){
            scene = preCarregados.get(tela);
        }else{
            Parent root = FXMLLoader.load(getClass().getResource(tela));
            scene = new Scene(root);
            preCarregados.put(tela, scene);
        }
        
        long depois = Calendar.getInstance().getTimeInMillis();
        System.out.println(depois-antes);
        System.out.println("load fxml...");
        
        stage.close();

        stage.setScene(scene);
        stage.show();
        
        System.out.println("Set Screne...");
        
        stage.getIcons().add(new javafx.scene.image.Image(IMG.class.getResource("logo_relatorio.png").openStream()));
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
        System.out.println("Finalizou...");
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
