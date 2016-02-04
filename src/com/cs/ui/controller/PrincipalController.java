package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class PrincipalController implements Initializable {
    
    @FXML
    private StackPane stack;
    
    @FXML
    private SplitMenuButton user;
    
    private Facede f;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            f = Facede.getInstance();
            String login = f.getFuncionarioLogado().getLogin();
            user.setText(login);
        }catch(Exception e){
            e.printStackTrace();
        }
    }    
    
    @FXML
    public void logoff(){
        f.logoff();
        Platform.exit();
    }
    
    @FXML
    public void trocarDeUsuario(){
        f.logoff();
        Main.trocarDeTela(ControllerTelas.TELA_LOGIN);
    }
    
    
    @FXML
    public void botaoMenu(ActionEvent e){
        Button bt = (Button) e.getSource();
        String label = (bt.getId() != null? bt.getId(): bt.getText()).toLowerCase();
        
        if(label.equalsIgnoreCase("venda")){
            Main.trocarDeTela(ControllerTelas.TELA_VENDA);
        }else{
            stack.getChildren().clear();
            stack.getChildren().add(getNode("fxml/"+label+".fxml"));
        }
    }
    
    
    private Node getNode(String node){
        Node no = null;
        try {
            no = FXMLLoader.load(getClass().getResource(node));
        } catch (Exception e) {
        }
        return no;
        
    }
    
}
