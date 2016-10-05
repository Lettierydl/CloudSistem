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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

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
        Action ac = Dialogs.create().title("Restaurar Sistema")
                .masthead("Deseja realmente restaurar o sistema?")
                .title("Essa operação não poderá ser revertida")
                .actions(Dialog.Actions.YES, Dialog.Actions.CANCEL)
                .showError();
        if (ac == Dialog.Actions.YES) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Restalrar Banco de Dados");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("SQL", "*.sql"),
                    new FileChooser.ExtensionFilter("Zip", "*.zip")
            );
            if (true) {
                return;
            }

            File file = fileChooser.showOpenDialog(restaurarBut.getScene().getWindow());
            if (file != null) {
                try {
                    boolean ret = f.restaurarBancoDeDados(file);
                    if (ret) {
                        Dialogs.create().title("Restauração Realizada com sucesso")
                                .masthead("Restauração do Sistema Realizada com sucesso")
                                .style(DialogStyle.UNDECORATED)
                                .showInformation();
                        f.logoff();
                        Main.trocarDeTela(ControllerTelas.TELA_LOGIN);
                    }
                } catch (FuncionarioNaoAutorizadoException ex) {
                    Dialogs.create().title("Funcionário não autorizado")
                            .masthead("Funcionário não autorizado a realizar esta operação")
                            .message("Por favor, entre com outro usuário")
                            .showError();
                } catch (IOException ex) {
                    Dialogs.create().showException(ex);
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
                        Dialogs.create().title("Funcionário não autorizado")
                                .masthead("Funcionário não autorizado a realizar esta operação")
                                .message("Por favor, entre com outro usuário")
                                .showError();
                    } catch (IOException ex) {
                        Dialogs.create().showException(ex);
                    }
                }
            }.start();
        }
    }

}
