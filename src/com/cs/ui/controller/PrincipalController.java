package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.StackPane;

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
        }
        // TODO
    }    
    
    @FXML
    public void logoff(){
       
    }
    
    @FXML
    public void trocarDeUsuario(){
        
    }
    
    
    @FXML
    public void botaoMenu(ActionEvent e){
        Button bt = (Button) e.getSource();
        if(bt.getText().equalsIgnoreCase("venda")){
            Main.trocarDeTela(ControllerTelas.TELA_VENDA);
        }else{
            stack.getChildren().clear();
            stack.getChildren().add(getNode("fxml/"+bt.getText().toLowerCase()+".fxml"));
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
