package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.sis.util.Registro;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import com.cs.ui.controller.dialog.DialogController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class ConfiguracaoSistemaController implements Initializable {

    private Facede f;
    
    @FXML
    private Button alterarConfigBanco;

    @FXML
    private Pane panelConfig;

    @FXML
    private TextField arquivos_backup;

    @FXML
    private Button registrar;
    
    @FXML
    private CheckBox zipSelect;
    
    @FXML
    private CheckBox limitSelect;

    @FXML
    private TableColumn<Funcionario, Funcionario> colunaEdit;

    @FXML
    private TextField ip_banco;

    @FXML
    private TextField idCaixa;

    @FXML
    private Button alterarReg;

    @FXML
    private TableColumn<?, ?> colunaFun;

    @FXML
    private TableColumn<?, ?> colunaCPF;

    @FXML
    private TableColumn<?, ?> colunaTel;

    @FXML
    private TableView<Funcionario> tabela;

    @FXML
    private ComboBox<String> box_estrategia;

    @FXML
    private TableColumn<?, ?> colunaNome;

    @FXML
    private TextField quantidadeCaixas;
    
    @FXML
    private TextField razao;
    
    @FXML
    private TextField endereco;
    
    @FXML
    private TextField proprietario;
    
    @FXML
    private TextField chave;
    
    @FXML
    private Label flag;
    
    @FXML
    private Label chave_pc;
    
    
    public ConfiguracaoSistemaController(){
        
    }
    

    @FXML
    void irHome(MouseEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_LOGIN);
    }

    @FXML
    void abrirModalCreate(ActionEvent event) {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/dialogFuncionario.fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<Funcionario> controller = loader.getController();
            controller.setTipe(DialogController.CREATE_MODAL);
            controller.setDialogStage(dialogStage);
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();

            tabela.getItems().clear();
            atualizarLista();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void removerFuncionario(ActionEvent event) {
        
        Action ct = Dialogs.create().title("Remover Funcionário")
                .masthead("Tem certeza que deseja remover um funcionário?")
                .showWarning();
        if(ct.equals(Dialog.Actions.YES)){
            Dialogs sn = Dialogs.create().title("Remover Funcionario")
                .masthead("Digite o nome do funcionário que deseja remover");
            sn.style(DialogStyle.UNDECORATED);
            String get = sn.showTextInput().get(); 
            try{
                Funcionario fun = f.buscarFuncionarioPorNome(get);
                f.removerFuncionario(fun);
                Dialogs.create().title("Funcionário Removido")
                .masthead("Funcionário Removido com Sucesso")
                .message(fun.getNome())
                .showInformation();
            }catch(Exception e){
                Dialogs.create().title("Remover Funcionário")
                .masthead("Funcionário não encontrado")
                .message(get)
                .showError();
            }
        }
    }

    @FXML
    void alterarConfiguracaoBanco(ActionEvent event) {
        String ip = ip_banco.getText();
        String arquivos = arquivos_backup.getText();
        boolean zipar = zipSelect.isSelected();
        boolean limitar = limitSelect.isSelected();
        String estrategia = box_estrategia.getValue();
        
        try {
            f.salvarConfiguracoesDeBackup(arquivos, zipar);
            f.inserirConfiguracaoSistema(VariaveisDeConfiguracaoUtil.IP_DO_BANCO, ip);
            f.inserirConfiguracaoSistema(VariaveisDeConfiguracaoUtil.ATIVAR_LIMITE_REGISTRO_MOSTRADOS, limitar);
            f.inserirConfiguracaoSistema(VariaveisDeConfiguracaoUtil.EXTRATEGIA_DE_CONEXAO, estrategia);
            Dialogs.create().title("Alterações realizada")
                    .masthead("Alterações realizada com sucesso")
                    .message("IP: "+ip+"\n" + "Arquivos: "+arquivos+"\n"
                            + "Estratégia: "+ estrategia+"\n"
                            + "Zipar: "+ (zipar? "Sim" : "Não")+"\n"
                            + "Limitar: "+ (limitar? "Sim" : "Não"))
                    .showInformation();
        } catch (IOException ex) {
            Dialogs.create().showException(ex);
        }
    }

    @FXML
    void alterarRegistro(ActionEvent event) {
        registrar(event);
    }

    @FXML
    void registrar(ActionEvent event) {
        String id = idCaixa.getText();
        String qtCaixa = quantidadeCaixas.getText();
        f.inserirConfiguracaoSistema(VariaveisDeConfiguracaoUtil.ID_DO_CAIXA, id);
        f.inserirConfiguracaoSistema(VariaveisDeConfiguracaoUtil.QUANTIDADE_CAIXA, Integer.valueOf(qtCaixa));
        
        Registro r = Registro.getIntance();
        //System.out.println(r.criarRegistro(razao.getText(), proprietario.getText(), r.chaveComputador()));
        boolean reg = r.registrar(chave.getText(), razao.getText(), endereco.getText(), proprietario.getText());
        flag.setVisible(reg);
        if(reg){
            Dialogs.create().title("Registro Realizado")
                    .masthead("Registro realizado com sucesso")
                    .message("Razão Socual: "+razao.getText()+"\n" 
                            + "Propietario: "+proprietario.getText()+"\n"
                            + "Endereco: "+ endereco.getText()+"\n" )
                    .showInformation();
        }else{
        Dialogs.create().title("Chave Inválida")
                    .masthead("Chave Inválida para as informações cadastradas")
                    .showError(); 
        }
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
             f = Facede.getInstance();
        }catch(Exception e){
            Dialogs.create().title("Conexão com o Banco")
                    .masthead("Não foi possível estabelecer uma conexão com o Banco")
                    .showError();
        }
        
        Dialogs sn = Dialogs.create().title("Senha de Acesso")
                .masthead("Digite a senha de acesso as configurações do sistema");
        sn.style(DialogStyle.UNDECORATED);
        MaskFieldUtil.numericField(quantidadeCaixas);
        MaskFieldUtil.upperCase(this.razao);
        MaskFieldUtil.upperCase(this.endereco);
        MaskFieldUtil.upperCase(this.proprietario);
        MaskFieldUtil.serialTextField(this.chave);
        
        
        String get = sn.showTextInput("Cloudsistem").get();
        if(OperacaoStringUtil.validarSenhaMestre(get, true)){
            panelConfig.setDisable(false);
            ObservableList<String> estrategias = FXCollections.observableArrayList();
            estrategias.add("Remoto");
            estrategias.add("Local");
            box_estrategia.setItems(estrategias);
            box_estrategia.setValue("Remoto");
            preencherCampos();
            
            colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colunaCPF.setCellValueFactory(new PropertyValueFactory<>("cpf"));
            colunaTel.setCellValueFactory(new PropertyValueFactory<>("telefones"));
            colunaFun.setCellValueFactory(new PropertyValueFactory<>("tipoDeFuncionario"));
            atualizarLista();
            
        }else{
            panelConfig.setDisable(true);
        }
    }
    
    
    public void atualizarLista() {
        try {
            tabela.getItems().clear();
            List<Funcionario> observavel = f.buscarFuncionarioPorNomeQueInicia("");
            configurarColunaEditar();
            tabela.setItems(FXCollections.observableList(observavel));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    protected void configurarColunaEditar() {
        colunaEdit.setComparator(new Comparator<Funcionario>() {
            @Override
            public int compare(Funcionario p1, Funcionario p2) {
                return p1.toString().compareToIgnoreCase(p2.toString());
            }
        });
        colunaEdit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Funcionario, Funcionario>, ObservableValue<Funcionario>>() {
            @Override
            public ObservableValue<Funcionario> call(TableColumn.CellDataFeatures<Funcionario, Funcionario> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colunaEdit.setCellFactory(new Callback<TableColumn<Funcionario, Funcionario>, TableCell<Funcionario, Funcionario>>() {
            @Override
            public TableCell<Funcionario, Funcionario> call(TableColumn<Funcionario, Funcionario> btnCol) {
                return new TableCell<Funcionario, Funcionario>() {
                    @Override
                    public void updateItem(final Funcionario obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final Button button = new Button("Editar");
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                abrirModalEdit(obj);
                            }
                        });
                    }
                };
            }
        });
    }
    
    
    
    void abrirModalEdit(Funcionario f) {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/dialogFuncionario.fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<Funcionario> controller = loader.getController();
            controller.setTipe(DialogController.EDIT_MODAL);
            controller.setEntity(f);
            controller.setDialogStage(dialogStage);
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();

            tabela.getItems().clear();
            atualizarLista();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void preencherCampos() {
        Map<String, Object> confg = null;
        try{
            confg = f.lerConfiguracoesSistema();
        }catch(NullPointerException ne){}
        
        try{
            ip_banco.setText(confg.get(VariaveisDeConfiguracaoUtil.IP_DO_BANCO).toString());
        }catch(Exception e){}
        try{
            box_estrategia.setValue(confg.get(VariaveisDeConfiguracaoUtil.EXTRATEGIA_DE_CONEXAO).toString());
        }catch(Exception e){}
        
        try{
            String d = "";
            for(File f: f.getArquivosDestinoBackup()){
                d+=f.getAbsolutePath()+";";
            }
            arquivos_backup.setText(d);
        }catch(Exception e){}
        try{
            zipSelect.setSelected(f.isCopactarBackup());
        }catch(Exception e){}
        
        try{
            limitSelect.setSelected((boolean) confg.get(VariaveisDeConfiguracaoUtil.ATIVAR_LIMITE_REGISTRO_MOSTRADOS));
        }catch(Exception e){}
        
        chave_pc.setText(Registro.getIntance().chaveComputador());
        
        
        try{
            idCaixa.setText(confg.get(VariaveisDeConfiguracaoUtil.ID_DO_CAIXA).toString());
        }catch(Exception e){}
        
        try{
            quantidadeCaixas.setText(confg.get(VariaveisDeConfiguracaoUtil.QUANTIDADE_CAIXA).toString());
        }catch(Exception e){}
        
        try{
            Registro g = Registro.getIntance();
            razao.setText(g.getRazao());
            endereco.setText(g.getEndereco());
            proprietario.setText(g.getProprietario());
            chave.setText(g.getChaveRegistro());
            registrar.setVisible(true);
            registrar.setDisable(false);
            alterarReg.setVisible(false);
            alterarReg.setDisable(true);
            if(g.isRegistrado()){
                flag.setVisible(true);
            }else{
                flag.setVisible(false);
            }
        }catch(Exception e){}
        
    }
}
