package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class ClienteDialogController extends DialogController<Cliente> {

    @FXML
    private TextField nome;
    @FXML
    private TextField endereco;
    @FXML
    private TextField cpf;
    @FXML
    private TextField telefone;
    @FXML
    private DatePicker nascimento;
    @FXML
    private TextField celular;

    public ClienteDialogController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.upperCase(nome);
        MaskFieldUtil.upperCase(endereco);
        MaskFieldUtil.cpfField(cpf);
        MaskFieldUtil.foneField(telefone);
        MaskFieldUtil.foneField(celular);
        MaskFieldUtil.dateField(nascimento.getEditor());

        JavaFXUtil.nextFielOnAction(nome, cpf);
        JavaFXUtil.nextFielOnAction(cpf, nascimento);
        JavaFXUtil.nextFielOnAction(nascimento.getEditor(), endereco);
        JavaFXUtil.nextFielOnAction(endereco, telefone);
        JavaFXUtil.nextFielOnAction(telefone, celular);
        JavaFXUtil.nextFielOnAction(celular, okButton);

        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });

        //setar onAction de cada um dos imputs text para ir para o proximo campo
        //e verificar se for create se ja existe o produto com aquele codigo ou descricao
        //se for edit, verificar se ta editado certo e mostrar as menssagens
    }

    @Override
    public boolean isEntradaValida() {
        String msgErro = "";
        if (tipe == EDIT_MODAL) {// validar edicao
            try {
                if (nome.getText().isEmpty()) {
                    msgErro += "Nome é obrigatório\n";
                }
                Cliente o;
                try {
                    o = f.buscarClientePorNome(nome.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Nome já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }
                if (!(cpf.getText().replace(".", "").replace("-", "")).isEmpty()) {
                    try {
                        o = f.buscarClientePorCPF(cpf.getText().replace(".", "").replace("-", ""));
                        if (o.getId() != entity.getId()) {// codigo de outro produto
                            msgErro += "CPF já cadastrado\n";
                        }
                    } catch (NonUniqueResultException nu) {
                        msgErro += "CPF já cadastrado\n";
                    }
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
                Cliente o;
                try {
                    o = f.buscarClientePorNome(nome.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Nome já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }

                if (!(cpf.getText().replace(".", "").replace("-", "")).isEmpty()) {
                    try {
                        o = f.buscarClientePorCPF(cpf.getText().replace(".", "").replace("-", ""));
                        if (o.getId() != entity.getId()) {// codigo de outro produto
                            msgErro += "CPF já cadastrado\n";
                        }
                    } catch (NonUniqueResultException nu) {
                        msgErro += "CPF já cadastrado\n";
                    }
                }
            } catch (Exception e) {
                if (msgErro.isEmpty()) {
                    return true;
                }
            }
        }
        if (!msgErro.isEmpty()) {
            JavaFXUtil.showDialog(
                    "Campos Inválidos"
                    ,"Por favor, corrija os campos inválidos"
                    ,msgErro,
                    Alert.AlertType.ERROR);

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
                entity.setDataDeNascimento(OperacaoStringUtil.converterDataValor(nascimento.getEditor().getText()));
                try {
                    //merge
                    f.atualizarCliente(entity);
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(
                            "Funcionário não autorizado"
                            ,"Por favor, entre com um usuário diferente",
                            Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    Logger.getLogger(ClienteDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialogStage.close();
            } else if (tipe == CREATE_MODAL) {
                entity.setNome(nome.getText());
                entity.setEndereco(endereco.getText());
                entity.setTelefone(telefone.getText());
                entity.setCelular(celular.getText());
                entity.setCpf(cpf.getText());
                entity.setDataDeNascimento(OperacaoStringUtil.converterDataValor(nascimento.getEditor().getText()));
                try {
                    //create
                    f.adicionarCliente(entity);
                    String date = "";
                    try {
                        date = OperacaoStringUtil.formatDataValor(entity.getDataDeNascimento());
                    } catch (NullPointerException ne) {
                    }
                    JavaFXUtil.showDialog(
                            "Cliente Criado com Sucesso"
                            ,"Nome: " + entity.getNome() + "\n"
                                    + "CPF: " + entity.getCpf() + "\n"
                                    + "Endereço: " + entity.getEndereco() + "\n"
                                    + "Telefones: " + entity.getTelefones() + "\n"
                                    + "Data de nascimento: " + date + "\n")
                            ;
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(ex);
                } catch (Exception ex) {
                    Logger.getLogger(ClienteDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialogStage.close();
            }
        }
        okClicked = true;
    }

    @Override
    public void setEntity(Cliente entity) {
        this.entity = entity;
        switch (tipe) {
            case EDIT_MODAL:
                page.setText("Editar Cliente");
                nome.setText(entity.getNome());
                endereco.setText(entity.getEndereco());
                telefone.setText(entity.getTelefone());
                celular.setText(entity.getCelular());
                cpf.setText(entity.getCpf());
                try {
                    nascimento.setValue(JavaFXUtil.toLocalDate(entity.getDataDeNascimento()));
                } catch (NullPointerException ne) {
                }
                break;
            case CREATE_MODAL:
                super.entity = new Cliente();
                page.setText("Criar Cliente");
                nome.setText("");
                endereco.setText("");
                telefone.setText("");
                celular.setText("");
                nascimento.getEditor().setText("");
                cpf.setText("");
                break;
            case VIEW_MODAL:
                page.setText("Cliente ID: " + entity.getId());
                nome.setText(entity.getNome());
                endereco.setText(entity.getEndereco());
                telefone.setText(entity.getTelefone());
                celular.setText(entity.getCelular());
                cpf.setText(entity.getCpf());
                try {
                    nascimento.getEditor().setText(OperacaoStringUtil.formatDataValor(entity.getDataDeNascimento()));
                } catch (NullPointerException ne) {
                }

                nome.setEditable(false);
                endereco.setEditable(false);
                telefone.setEditable(false);
                celular.setEditable(false);
                cpf.setEditable(false);
                nascimento.setEditable(false);

                cancelButton.setVisible(false);
                break;
        }
    }
    
    @FXML
    public void excluirClicado(){
        
        ButtonType act = JavaFXUtil.showDialogOptions("Excluir Cliente"
                ,"Tem certeza que deseja excluir todas as informações do Cliente "+entity.getNome()+"?"
                ,"Todas as vendas e pagamentos desse Cliente serão apagadas\n"
                        + "Essa operação não pode ser revertida!");
        if(act.equals(ButtonType.CANCEL)){
            return;
        }
        
        try {
            List<Pagavel> buscarPagaveisDoCliente = f.buscarPagaveisDoCliente(entity);
            for(Pagavel p: buscarPagaveisDoCliente){
                f.removerPagavel(p);
            }
            
            List<Pagamento> listaPagamentoDoCliente = f.getListaPagamentoDoCliente(entity);
            for(Pagamento p: listaPagamentoDoCliente){
                f.removerPagamento(p);
            }
            f.removerCliente(entity);
        } catch (EntidadeNaoExistenteException ex) {
            Logger.getLogger(ClienteDialogController.class.getName()).log(Level.SEVERE, null, ex);
            JavaFXUtil.showDialog("Erro na operação"
                ,"Erro na operação de exclusão",
            Alert.AlertType.ERROR);
            return;
        } catch (FuncionarioNaoAutorizadoException ex) {
            JavaFXUtil.showDialog(ex);
            return;
        }catch(Exception ee){
            ee.printStackTrace();
            JavaFXUtil.showDialog(ee);
            return;
        }
        JavaFXUtil.showDialog("Cliente excuido"
                ,"Cliente excuido com sucesso!"
                ,"Cadastro do Cliente "+entity.getNome()+ " excuido com sucesso!",
                Alert.AlertType.ERROR);
        this.cancelarClicado();
    }

    @Override
    public String getTitulo() {
        switch (tipe) {
            case EDIT_MODAL:
                return "Editar Cliente";
            case CREATE_MODAL:
                return "Criar Cliente";
            default:
                return "Cliente";
        }
    }

}
