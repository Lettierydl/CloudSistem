package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.sis.controller.configuracao.PermissaoFuncionario;
import com.cs.sis.model.estoque.CategoriaProduto;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.estoque.UnidadeProduto;
import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.ui.controller.fxml.dialog.DialogFXML;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class EstoqueDialogController extends DialogController<Produto> {

    @FXML
    private TextField estoque;
    @FXML
    private TextField codigo;
    @FXML
    private TextField descricao;
    @FXML
    private TextField valorV;
    @FXML
    private TextField valorC;
    @FXML
    private TextField limite;
    @FXML
    private Label erro;

    @FXML
    private Button entrada;
    @FXML
    private ComboBox<CategoriaProduto> categoria;
    @FXML
    private ComboBox<UnidadeProduto> unidade;

    public EstoqueDialogController() {
        super();
    }

    public void entradaDeProduto(){
        //abre modal entrada de produto
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DialogFXML.class.getResource("dialogEntradaProduto.fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<Produto> controller = loader.getController();
            controller.setTipe(tipe);
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();
           
            this.dialogStage.close();
            entity.setQuantidadeEmEstoque(f.buscarProdutoPorId(entity.getId()).getQuantidadeEmEstoque());
            estoque.setText(OperacaoStringUtil.formatarStringQuantidade(entity.getQuantidadeEmEstoque()));
            //System.out.println(entity.getQuantidadeEmEstoque());
       
            okClicado();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.upperCase(descricao);
        MaskFieldUtil.numericField(codigo);
        MaskFieldUtil.maxField(codigo, 13);
        MaskFieldUtil.quantityField(estoque);
        MaskFieldUtil.quantityField(limite);
        MaskFieldUtil.monetaryField(valorC);
        MaskFieldUtil.monetaryField(valorV);

        JavaFXUtil.nextFielOnAction(codigo, descricao);
        JavaFXUtil.nextFielOnAction(descricao, valorV);
        JavaFXUtil.nextFielOnAction(valorV, valorC);
        JavaFXUtil.nextFielOnAction(valorC, estoque);
        JavaFXUtil.nextFielOnAction(estoque, limite);
        JavaFXUtil.nextFielOnAction(limite, okButton);

        categoria.setItems(FXCollections.observableArrayList(CategoriaProduto.values()));
        unidade.setItems(FXCollections.observableArrayList(UnidadeProduto.values()));
        
        if(f.getValor(PermissaoFuncionario.ALTERAR_ESTORQUE_PRODUTO, f.getFuncionarioLogado().getTipoDeFuncionario())){
            estoque.setEditable(true);
        }else{
            estoque.setEditable(false);
        }
       
        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });

        //setar onAction de cada um dos imputs text para ir para o proximo campo
        //e verificar se for create se ja existe o produto com aquele codigo ou descricao
        //se for edit, verificar se ta editado certo e mostrar as menssagens
    }
    
    @FXML
    public void gerarCodigo(){
        String cd = f.gerarCodigo();
        codigo.setText(cd);
        codigo.selectAll();
    } 

    @Override
    public boolean isEntradaValida() {
        String msgErro = "";
        if (tipe == EDIT_MODAL) {// validar edicao
            try {
                if (codigo.getText().isEmpty()) {
                    msgErro += "Código de Barras é obrigatório\n";
                }
                Produto o = f.buscarProdutoPorCodigo(codigo.getText());
                if (o.getId() != entity.getId()) {// codigo de outro produto
                    msgErro += "Código de Barras já cadastrado\n";
                }
                o = f.buscarProdutoPorDescricao(descricao.getText());
                if (o.getId() != entity.getId()) {// codigo de outro produto
                    msgErro += "Descrição já cadastrada\n";
                }
                double vv = 0, vc = 0;
                try {
                    vv = OperacaoStringUtil.converterStringValor(valorV.getText());
                    vc = OperacaoStringUtil.converterStringValor(valorC.getText());
                    if (vv == 0) {
                        msgErro += "Valor de Venda não pode ser zero\n";
                    } else if (vv < vc) {
                        msgErro += "Valor de Venda menor do que Valor de Compra\n";
                    }
                } catch (NumberFormatException ne) {
                    msgErro += "Valor Errado\n";
                }
            } catch (Exception e) {
                if (msgErro.isEmpty()) {
                    return true;
                }
            }
        } else {// validar criacao
            try {
                try {
                    if (codigo.getText().isEmpty()) {
                        msgErro += "Código de Barras é obrigatório\n";
                    }
                    Produto o = f.buscarProdutoPorCodigo(codigo.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Código de Barras já cadastrado\n";
                    }
                } catch (Exception e) {
                }
                try {
                    Produto o = f.buscarProdutoPorDescricao(descricao.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Descrição já cadastrada\n";
                    }
                } catch (Exception e) {
                }
                double vv = 0, vc = 0;
                try {
                    vv = OperacaoStringUtil.converterStringValor(valorV.getText());
                    vc = OperacaoStringUtil.converterStringValor(valorC.getText());
                    if (vv == 0) {
                        msgErro += "Valor de Venda não pode ser zero\n";
                    } else if (vv < vc) {
                        msgErro += "Valor de Venda menor do que Valor de Compra\n";
                    }
                } catch (NumberFormatException ne) {
                    msgErro += "Valor Errado\n";
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
                entity.setDescricao(descricao.getText().toUpperCase());
                entity.setCodigoDeBarras(codigo.getText());
                entity.setValorDeVenda(OperacaoStringUtil.converterStringValor(valorV.getText()));
                entity.setValorDeCompra(OperacaoStringUtil.converterStringValor(valorC.getText()));
                entity.setLimiteMinimoEmEstoque(OperacaoStringUtil.converterStringValor(limite.getText()));
                entity.setQuantidadeEmEstoque(OperacaoStringUtil.converterStringValor(estoque.getText()));
                entity.setCategoria(categoria.getValue());
                entity.setDescricaoUnidade(unidade.getValue());
                try {
                    //merge
                    f.atualizarProduto(entity);
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(
                            "Funcionário não autorizado"
                            ,"Por favor, entre com um usuário diferente",
                            Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    Logger.getLogger(EstoqueDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialogStage.close();
            } else if (tipe == CREATE_MODAL) {
                entity.setDescricao(descricao.getText().toUpperCase());
                entity.setCodigoDeBarras(codigo.getText());
                entity.setValorDeVenda(OperacaoStringUtil.converterStringValor(valorV.getText()));
                entity.setValorDeCompra(OperacaoStringUtil.converterStringValor(valorC.getText()));
                entity.setLimiteMinimoEmEstoque(OperacaoStringUtil.converterStringValor(limite.getText()));
                entity.setQuantidadeEmEstoque(OperacaoStringUtil.converterStringValor(estoque.getText()));
                entity.setCategoria(categoria.getValue());
                entity.setDescricaoUnidade(unidade.getValue());
                //create
                try {
                    f.adicionarProduto(entity);
                    JavaFXUtil.showDialog(
                            "Produto Criado com Sucesso"
                            ,"Código de Barras: " + entity.getCodigoDeBarras() + "\n"
                                    + "Descrição: " + entity.getDescricao() + "\n"
                                    + "Valor de Venda: " + OperacaoStringUtil.formatarStringValorMoeda(entity.getValorDeVenda()) + "\n"
                                    + "Valor de Compra: " + OperacaoStringUtil.formatarStringValorMoeda(entity.getValorDeCompra()) + "\n"
                                    + "Estoque: " + OperacaoStringUtil.formatarStringQuantidade(entity.getQuantidadeEmEstoque()) + "\n");
                } catch (FuncionarioNaoAutorizadoException ex) {
                    JavaFXUtil.showDialog(
                            "Funcionário não autorizado"
                            ,"Por favor, entre com um usuário diferente",
                            Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    Logger.getLogger(EstoqueDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialogStage.close();
            }
        }
        okClicked = true;
    }

    @Override
    public void setEntity(Produto entity) {
        this.entity = entity;
        switch (tipe) {
            case EDIT_MODAL:
                page.setText("Editar Produto");
                estoque.setText(OperacaoStringUtil.formatarStringQuantidade(entity.getQuantidadeEmEstoque()));
                valorC.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getValorDeCompra()));
                valorV.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getValorDeVenda()));
                limite.setText(OperacaoStringUtil.formatarStringQuantidade(entity.getLimiteMinimoEmEstoque()));
                codigo.setText(entity.getCodigoDeBarras());
                descricao.setText(entity.getDescricao());
                categoria.setValue(entity.getCategoria());
                unidade.setValue(entity.getDescricaoUnidade());
                break;
            case CREATE_MODAL:
                super.entity = new Produto();
                page.setText("Criar Produto");
                estoque.setText("");
                valorC.setText("");
                valorV.setText("");
                limite.setText("0,000");
                codigo.setText("");
                descricao.setText("");
                categoria.setValue(CategoriaProduto.getDefalt());
                //categoria.setValue(CategoriaProduto.Roupa);
                unidade.setValue(UnidadeProduto.UND);
                entrada.setVisible(false);
                break;
            case VIEW_MODAL:
                page.setText("Produto ID: " + entity.getId());
                estoque.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getQuantidadeEmEstoque()));
                valorC.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getValorDeCompra()));
                valorV.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getValorDeVenda()));
                limite.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getLimiteMinimoEmEstoque()));
                codigo.setText(entity.getCodigoDeBarras());
                descricao.setText(entity.getDescricao());
                categoria.setValue(entity.getCategoria());
                unidade.setValue(entity.getDescricaoUnidade());
                unidade.setEditable(false);
                categoria.setEditable(false);
                descricao.setEditable(false);
                codigo.setEditable(false);
                limite.setEditable(false);
                valorV.setEditable(false);
                valorC.setEditable(false);
                estoque.setEditable(false);
                cancelButton.setVisible(false);
                entrada.setVisible(false);
                break;
        }
    }

    @FXML      
    public void excluirClicado(){
        
        ButtonType act = JavaFXUtil.showDialogOptions("Excluir Produto"
                ,"Tem certeza que deseja excluir o produto "+entity.getDescricao()+" do sistema?"
                ,"O produto será retirado de todo o histórico de vendas e balanços do sistema\n"
                        + "Essa operação não pode ser revertida!");
        if(act.equals(ButtonType.CANCEL)){
            return;
        }
        
        try {
            f.removerProduto(entity);
        } catch (EntidadeNaoExistenteException ex) {
            Logger.getLogger(EstoqueDialogController.class.getName()).log(Level.SEVERE, null, ex);
            JavaFXUtil.showDialog("Erro na operação"
                ,"Erro na operação de excuisão",
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
        JavaFXUtil.showDialog("Produto excuido"
                ,"Produto excuido com sucesso!"
                ,"Cadastro do produto "+entity.getDescricao()+ " excuido com sucesso!");
        this.cancelarClicado();
    }
    
    @Override
    public String getTitulo() {
        switch (tipe) {
            case EDIT_MODAL:
                return "Editar Produto";
            case CREATE_MODAL:
                return "Criar Produto";
            default:
                return "Produto";
        }
    }

}
