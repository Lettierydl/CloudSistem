package com.cs.ui.controller;



import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.pessoas.exception.LoginIncorretoException;
import com.cs.sis.model.pessoas.exception.SenhaIncorretaException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
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
import jidefx.scene.control.field.MaskTextField;

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
    private Pane formulario;
    
    
    
    @FXML
    private void login(ActionEvent event) {
        f = Facede.getInstance();
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
    }    
    
}
