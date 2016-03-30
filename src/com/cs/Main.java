package com.cs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Lettiery
 */
public class Main extends Application {
    
    
    private static ControllerTelas ct;
    
    
    
    public static void trocarDeTela(String tela){
        try {
            ct.mostrarTela(tela);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource(ControllerTelas.TELA_LOGIN));
        /*Parent root = FXMLLoader.load(getClass().getResource(ControllerTelas.TELA_PRINCIPAL));
            Facede f = Facede.getInstance();
        f.login("leo", "1234");
        
        */
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        scene.setRoot(root);
        stage.show();
        
        ct = new ControllerTelas(stage);
    }
    
    public static Throwable messagem_erro_fachada = null;
    
    public static void main(String[] args) {
        Thread t;
        t = new Thread(){
            @Override
            public void run (){
                try{
                    Facede.getInstance();
                }catch(ExceptionInInitializerError ee){
                    messagem_erro_fachada = new Exception("Erro ao Conectar com o Banco");
                }catch(Exception e){
                    messagem_erro_fachada = e;
                }
                try {
                    finalize();
                } catch (Throwable ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
        launch(args);
        
    }
    
}
