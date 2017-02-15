package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.model.exception.SenhaIncorretaException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class FuncionarioDialogController extends DialogController<Funcionario> {

    @FXML
    private TextField nome;
    @FXML
    private TextField endereco;
    @FXML
    private TextField cpf;
    @FXML
    private TextField telefone;
    @FXML
    private TextField celular;
    @FXML
    private TextField login;
    @FXML
    private Label labelSenhaN;
    @FXML
    private Label labelSenha;

    @FXML
    private PasswordField senha;
    @FXML
    private PasswordField senha2;

    @FXML
    private RadioButton supervisor;
    @FXML
    private RadioButton gerente;
    @FXML
    private RadioButton caixa;

    public FuncionarioDialogController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        supervisor.setToggleGroup(group);
        caixa.setToggleGroup(group);
        gerente.setToggleGroup(group);
        caixa.setSelected(true);
        if (!f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
            gerente.setDisable(true);
            supervisor.setDisable(true);
            caixa.setDisable(true);
            okButton.setDisable(true);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Permissão negada");
                alert.setHeaderText("Você não tem permissão para criar um funcionário");
                alert.setContentText("Por favor, entre como um funcionário adequado\n"
                        + "Sem permissão");
                alert.showAndWait();
                dialogStage.close();
            }
            );
            //dialogStage.close();
        }

        MaskFieldUtil.upperCase(nome);
        MaskFieldUtil.upperCase(login);
        MaskFieldUtil.upperCase(endereco);
        MaskFieldUtil.cpfField(cpf);
        MaskFieldUtil.foneField(telefone);
        MaskFieldUtil.foneField(celular);

        JavaFXUtil.nextFielOnAction(nome, telefone);
        JavaFXUtil.nextFielOnAction(telefone, celular);
        JavaFXUtil.nextFielOnAction(celular, endereco);
        JavaFXUtil.nextFielOnAction(endereco, cpf);
        JavaFXUtil.nextFielOnAction(cpf, login);
        JavaFXUtil.nextFielOnAction(login, senha);
        JavaFXUtil.nextFielOnAction(senha, senha2);
        JavaFXUtil.nextFielOnAction(senha2, okButton);

        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });
    }

    @Override
    public boolean isEntradaValida() {
        String msgErro = "";
        if (tipe == EDIT_MODAL) {// validar edicao
            try {
                if (nome.getText().isEmpty()) {
                    msgErro += "Nome é obrigatório\n";
                }

                if (login.getText().isEmpty()) {
                    msgErro += "Login é obrigatório\n";
                }
                Funcionario o;
                try {
                    o = f.buscarFuncionarioPorNome(nome.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Nome já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }

                try {
                    o = f.buscarFuncionarioPorLogin(login.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Login já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }

                if (!(cpf.getText().replace(".", "").replace("-", "")).isEmpty()) {
                    try {
                        o = f.buscarFuncionarioPorCPF(cpf.getText().replace(".", "").replace("-", ""));
                        if (o.getId() != entity.getId()) {// codigo de outro produto
                            msgErro += "CPF já cadastrado\n";
                        }
                    } catch (NonUniqueResultException nu) {
                        msgErro += "CPF já cadastrado\n";
                    }
                }

                if (!senha.getText().isEmpty() && senha2.getText().isEmpty()) {
                    msgErro += "Digite a nova senha\n";
                }

            } catch (Exception e) {
                if (msgErro.isEmpty()) {
                    return true;
                }
            }
        } else {// validar criacao
            try {
                if (nome.getText().isEmpty()) {
                    msgErro += "Nome é obrigatório\n";
                }

                if (login.getText().isEmpty()) {
                    msgErro += "Login é obrigatório\n";
                }
                Funcionario o;
                try {
                    o = f.buscarFuncionarioPorNome(nome.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Nome já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }

                try {
                    o = f.buscarFuncionarioPorLogin(login.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Login já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }

                if (!(cpf.getText().replace(".", "").replace("-", "")).isEmpty()) {
                    try {
                        o = f.buscarFuncionarioPorCPF(cpf.getText().replace(".", "").replace("-", ""));
                        if (o.getId() != entity.getId()) {// codigo de outro produto
                            msgErro += "CPF já cadastrado\n";
                        }
                    } catch (NonUniqueResultException nu) {
                        msgErro += "CPF já cadastrado\n";
                    }
                }
                if (!senha.getText().isEmpty() && !senha2.getText().isEmpty() && !senha.getText().equals(senha2.getText())) {
                    msgErro += "Senhas distintas\n";
                } else if (senha.getText().isEmpty()) {
                    msgErro += "É reqerido uma senha\n";
                } else if (senha2.getText().isEmpty()) {
                    msgErro += "É confirme a senha digitada\n";
                }
            } catch (Exception e) {
                if (msgErro.isEmpty()) {
                    return true;
                }
            }
        }
        if (!msgErro.isEmpty()) {
            JavaFXUtil.showDialog(
                    "Campos Inválidos", "Por favor, corrija os campos inválidos", msgErro,
                    Alert.AlertType.ERROR
            );

        }
        return msgErro.isEmpty();
    }

    @Override
    @FXML
    public void okClicado() {
        if (isEntradaValida() && tipe != VIEW_MODAL) {
            if (tipe == EDIT_MODAL) {
                entity.setNome(nome.getText());
                entity.setEndereco(endereco.getText());
                entity.setTelefone(telefone.getText());
                entity.setCelular(celular.getText());
                entity.setCpf(cpf.getText());
                if (entity.getTipoDeFuncionario() != TipoDeFuncionario.Gerente && f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
                    entity.setLogin(login.getText());
                    TipoDeFuncionario t = TipoDeFuncionario.Caixa;
                    if (supervisor.isSelected()) {
                        t = TipoDeFuncionario.Supervisor;
                    } else if (gerente.isSelected()) {
                        t = TipoDeFuncionario.Gerente;
                    }

                    entity.setTipoDeFuncionario(t);
                }

                try {
                    f.atualizarFuncionario(entity);
                    if (!senha.getText().isEmpty() && !senha2.getText().isEmpty()) {
                        try {
                            f.alterarSenhaDoFuncionario(entity, senha.getText(), senha2.getText());
                            f.atualizarFuncionario(entity);
                            JavaFXUtil.showDialog(
                                    "Funcionário Editado", "Senha do funcionário " + entity.getNome() + " anterada com secesso");
                        } catch (SenhaIncorretaException se) {
                            JavaFXUtil.showDialog(
                                    "Senha incorreta", "Senha incorreta, insira a senha do funcionário " + entity.getNome(),
                                    Alert.AlertType.ERROR
                            );
                        }
                    }
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(
                            "Funcionário não autorizado", "Por favor, entre com um usuário diferente",
                            Alert.AlertType.ERROR
                    );
                } catch (Exception ex) {
                    Logger.getLogger(FuncionarioDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialogStage.close();
            } else if (tipe == CREATE_MODAL) {
                entity = new Funcionario();
                entity.setNome(nome.getText());
                entity.setEndereco(endereco.getText());
                entity.setTelefone(telefone.getText());
                entity.setCelular(celular.getText());
                entity.setCpf(cpf.getText());
                entity.setLogin(login.getText());
                try {
                    TipoDeFuncionario t = TipoDeFuncionario.Caixa;
                    if (supervisor.isSelected()) {
                        t = TipoDeFuncionario.Supervisor;
                    } else if (gerente.isSelected()) {
                        t = TipoDeFuncionario.Gerente;
                    }
                    //create
                    f.adicionarFuncionario(entity, senha.getText(), t);
                    JavaFXUtil.showDialog(
                            "Funcionário Criado com Sucesso", "Nome: " + entity.getNome() + "\n"
                            + "Login: " + entity.getLogin() + "\n"
                            + "CPF: " + entity.getCpf() + "\n"
                            + "Endereço: " + entity.getEndereco() + "\n"
                            + "Telefones: " + entity.getTelefones() + "\n"
                            + "Função: " + entity.getTipoDeFuncionario() + "\n"
                    );
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(
                            "Funcionário não autorizado", "Por favor, entre com um usuário diferente",
                            Alert.AlertType.ERROR
                    );
                } catch (Exception ex) {
                    Logger.getLogger(FuncionarioDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialogStage.close();
            }
        }
        okClicked = true;
    }

    @Override
    public void setEntity(Funcionario entity) {
        this.entity = entity;
        switch (tipe) {
            case EDIT_MODAL:
                page.setText("Editar Funcionário");
                labelSenhaN.setText("Nova Senha");
                nome.setText(entity.getNome());
                endereco.setText(entity.getEndereco());
                telefone.setText(entity.getTelefone());
                celular.setText(entity.getCelular());
                cpf.setText(entity.getCpf());
                login.setText(entity.getLogin());
                if (entity.getTipoDeFuncionario().equals(TipoDeFuncionario.Caixa)) {
                    caixa.setSelected(true);
                } else if (entity.getTipoDeFuncionario().equals(TipoDeFuncionario.Supervisor)) {
                    supervisor.setSelected(true);
                } else if (entity.getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
                    gerente.setSelected(true);
                }
                break;
            case CREATE_MODAL:
                super.entity = new Funcionario();
                page.setText("Criar Funcionário");
                labelSenhaN.setText("Confirmar Senha");
                nome.setText("");
                login.setText("");
                endereco.setText("");
                telefone.setText("");
                celular.setText("");
                cpf.setText("");
                break;
            case VIEW_MODAL:
                page.setText("Funcionário ID: " + entity.getId());
                nome.setText(entity.getNome());
                endereco.setText(entity.getEndereco());
                telefone.setText(entity.getTelefone());
                celular.setText(entity.getCelular());
                cpf.setText(entity.getCpf());
                login.setText(entity.getLogin());
                if (entity.getTipoDeFuncionario().equals(TipoDeFuncionario.Caixa)) {
                    caixa.setSelected(true);
                } else if (entity.getTipoDeFuncionario().equals(TipoDeFuncionario.Supervisor)) {
                    supervisor.setSelected(true);
                } else if (entity.getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
                    gerente.setSelected(true);
                }

                nome.setEditable(false);
                endereco.setEditable(false);
                telefone.setEditable(false);
                celular.setEditable(false);
                cpf.setEditable(false);
                login.setEditable(false);
                gerente.setDisable(true);
                supervisor.setDisable(true);
                caixa.setDisable(true);

                senha.setVisible(false);
                senha2.setVisible(false);
                labelSenhaN.setVisible(false);
                labelSenha.setVisible(false);
                cancelButton.setVisible(false);
                break;
        }
    }

    @Override
    public String getTitulo() {
        switch (tipe) {
            case EDIT_MODAL:
                return "Editar Funcionário";
            case CREATE_MODAL:
                return "Criar Funcionário";
            default:
                return "Funcionário";
        }
    }

}
