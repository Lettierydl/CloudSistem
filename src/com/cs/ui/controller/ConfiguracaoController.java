package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import com.cs.ui.controller.dialog.DialogController;
import com.cs.ui.controller.dialog.ProgressDialogController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class ConfiguracaoController implements Initializable {

    private Facede f;

    @FXML
    private Button realizarBut;

    @FXML
    private Button restaurarBut;
    
    @FXML
    private TextArea informacoes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        f = Facede.getInstance();
        informacoes.setText(VariaveisDeConfiguracaoUtil.INFORMACAO_SISTEMA);
    }

    @FXML
    public void restaurarBanco() {
        ButtonType ac = JavaFXUtil.showDialogOptions("Restaurar Sistema",
                "Deseja realmente restaurar o sistema?",
                "Essa operação não poderá ser revertida");
        if (ac == ButtonType.YES) {
            String senha = JavaFXUtil.showDialogInput("Teste de Permissão", "Digite a senha para permissão para essa operação!");
            if(!senha.equalsIgnoreCase("cloudsistem")){
                JavaFXUtil.showDialog("Senha Incorreta", "Senha Incorreta", "Você não tem autorização para essa operação!", Alert.AlertType.ERROR, true);
                return;
            }
            
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Restaurar Banco de Dados");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("SQL", "*.sql"),
                    new FileChooser.ExtensionFilter("Zip", "*.zip")
            );

            File file = fileChooser.showOpenDialog(restaurarBut.getScene().getWindow());
            if (file != null) {
                try {
                    boolean ret = f.restaurarBancoDeDados(file);
                    if (ret) {
                        JavaFXUtil.showDialog("Restauração Realizada com sucesso",
                                "Restauração do Sistema Realizada com sucesso");
                        f.logoff();
                        Main.trocarDeTela(ControllerTelas.TELA_LOGIN);
                    }
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(ex);
                } catch (IOException ex) {
                    JavaFXUtil.showDialog(ex);
                }
            }
        }
    }

    @FXML
    public void realiarBackups() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Backup");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        String name = "cloudsistem_" + new SimpleDateFormat("dd_MM_yyyy").format(new Date());
        name += f.isCopactarBackup() ? ".zip" : ".sql";
        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(f.isCopactarBackup() ? "Compactado" : "SQL", f.isCopactarBackup() ? "*.zip" : "*.sql")
        );

        File file = fileChooser.showSaveDialog(realizarBut.getScene().getWindow());
        if (file != null) {
            ProgressDialogController progress = DialogController.getProgressDialog();
            progress.setTitulo_msg("Realizando Backup do banco de dados.");
            progress.setWait_msg("Por favor espera a conclusão do backup");
            progress.show();
            //progress.start();

            new Thread() {
                @Override
                public void run() {
                    try {
                        String path = f.realizarBackupBancoDeDados(file);
                        progress.concluir("Backup Realizado com sucesso.", file + " \n" + path );
                    } catch (FuncionarioNaoAutorizadoException ex) {
                        JavaFXUtil.showDialog("Funcionário não autorizado",
                                "Funcionário não autorizado a realizar esta operação",
                                "Por favor, entre com outro usuário",Alert.AlertType.ERROR);
                    } catch (IOException ex) {
                        JavaFXUtil.showDialog(ex);
                    }
                }
            }.start();
        }
    }

}
