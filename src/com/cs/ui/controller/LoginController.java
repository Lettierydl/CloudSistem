package com.cs.ui.controller;



import com.cs.ControllerTelas;
import com.cs.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 *
 * @author Lettiery
 */
public class LoginController implements Initializable {
    
    @FXML
    private Label menssagem;
    
    @FXML
    private TextField nome;
    @FXML
    private PasswordField senha;
    
    @FXML
    private Button entrar;
    
    @FXML
    private Pane formulario;
    
    @FXML
    private void enterKey(ActionEvent event){
        if(event.getSource().equals(nome)){
            senha.requestFocus();
        }else if(event.getSource().equals(senha)){
            entrar.requestFocus();
        }
    }
    
    
    
    @FXML
    private void login(ActionEvent event) {
        
        String name = nome.getText();
        String pass = senha.getText();
        //verifica se consegue logar, se sim, passa para a proxima tela
        //se nao exibe a menssagem
        menssagem.setText("Usuário ou senha inválido!");
        nome.requestFocus();
        nome.selectAll();
        
        
        
        Main.trocarDeTela(ControllerTelas.TELA_PRINCIPAL);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        entrar.defaultButtonProperty().bind(entrar.focusedProperty());
        
    }    
    
}
