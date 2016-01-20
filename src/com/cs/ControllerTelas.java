package com.cs;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Angie
 */
public class ControllerTelas{
    public static Stage stage;
    
    public static final String TELA_LOGIN = "ui/controller/fxml/login.fxml";
    public static final String TELA_PRINCIPAL = "ui/controller/fxml/principal.fxml";
    public static final String TELA_VENDA = "ui/controller/fxml/venda.fxml";
    
    public ControllerTelas(Stage stage) {
       ControllerTelas.stage = stage;
    }

    
    public void mostrarTela(String tela) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(tela));
        
        Scene scene = new Scene(root);
        
        stage.centerOnScreen();

        
        scene.setRoot(root);
        stage.setScene(scene);
        stage.show();
    }
    
  
}

