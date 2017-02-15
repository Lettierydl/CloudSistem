package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.financeiro.Divida;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class PagavelDialogController extends DialogController<Pagavel> {

    @FXML
    private TableColumn<ItemDeVenda, Double> colQt;
    @FXML
    private TableColumn<ItemDeVenda, Double> colTot;
    @FXML
    private TableColumn<ItemDeVenda, String> colProd;
    @FXML
    private TableColumn<ItemDeVenda, Double> colVal;
    @FXML
    private TableView<ItemDeVenda> itens;

    @FXML
    private Button excluirButton;
    
    @FXML
    private Label obs;
    @FXML
    private Label cli;
    @FXML
    private Label data;
    @FXML
    private Label total;
    @FXML
    private Label valPg;
    @FXML
    private Label func;

    @FXML
    private Button btPDF;
    @FXML
    private Button btImprimirECF;
    @FXML
    private Button btImprimir;

    public PagavelDialogController() {
        super();
    }

    @FXML
    void imprimir(ActionEvent event) {

    }

    @FXML
    void imprimirECF(ActionEvent event) {
        boolean imprimir = f.imprimirVenda((Venda) entity);
        if (!imprimir) {
            JavaFXUtil.showDialog(
                    "Impressora não conectada",
                    "Impressora não conectada"
                    ,"Por favor entre em contato com o suporte",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void gerarPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar PDF");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        String name = "Venda_" + entity.getId() + "_" + new SimpleDateFormat("dd_MM_yyyy").format(new Date()) + ".pdf";
        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(btPDF.getScene().getWindow());
        if (file != null) {
            try {
                String path = f.gerarPdfDaVendaVenda((Venda) entity, f.buscarItensDaVendaPorIdDaVenda(entity.getId()), file);
                JavaFXUtil.showDialog("PDF salvo com sucesso"
                        ,"PDF salvo com sucesso"
                        ,file.getAbsolutePath());
            } catch (FuncionarioNaoAutorizadoException ex) {
                JavaFXUtil.showDialog(
                        "Funcionário não autorizado"
                        ,"Funcionário não autorizado a gerar PDFs"
                        ,"Por favor entre com um funcionário autorizado",
                        Alert.AlertType.ERROR);
            } catch (IOException ex) {
                JavaFXUtil.showDialog(ex);
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colVal.setCellValueFactory(new PropertyValueFactory<>("valorProduto"));
        colTot.setCellValueFactory(new PropertyValueFactory<>("total"));
        colQt.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("descricaoProduto"));

        JavaFXUtil.colunValueMoedaFormat(colVal);
        JavaFXUtil.colunValueMoedaFormat(colTot);
        JavaFXUtil.colunValueQuantidadeFormat(colQt);

        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });
    }

    @Override
    public boolean isEntradaValida() {
        return true;
    }

    @Override
    @FXML
    public void okClicado() {
        dialogStage.close();
        okClicked = true;
    }
    
    
    @FXML
    public void excluirClicado() {
        if (!f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
            JavaFXUtil.showDialog("Operação não autorizada"
                    ,"Você não tem permissão"
                    ,"Entre com um funcionário autorizado",
                    Alert.AlertType.ERROR);
            return;
        }
        ButtonType res = JavaFXUtil.showDialogOptions("Operação de Risco",
                "Tem certeza que deseja excluir esta venda?"
                ,"Os produtos serão repostos nos estoques\nEsta Operação não pode ser desfeita",
                Alert.AlertType.ERROR);
        
        if(res == ButtonType.YES){
            try {
                f.removerVendaAVista((Venda) entity);
                JavaFXUtil.showDialog("Venda excluida"
                    ,"Venda excluida com sucesso."
                    ,"Produtos repostos no estoque");
            } catch (EntidadeNaoExistenteException ex) {
                Logger.getLogger(PagavelDialogController.class.getName()).log(Level.SEVERE, null, ex);
                JavaFXUtil.showDialog("Erro ao realizar a operação",ex);
                return;
            } catch (Exception ex) {
                Logger.getLogger(PagavelDialogController.class.getName()).log(Level.SEVERE, null, ex);
                JavaFXUtil.showDialog("Erro ao realizar a operação",ex);
            }
            dialogStage.close();
        }
        //okClicked = true;
    }

    @Override
    public void setEntity(Pagavel entity) {
        this.entity = entity;
        if (entity instanceof Venda) {
            if (!f.getFuncionarioLogado().getTipoDeFuncionario().equals(TipoDeFuncionario.Gerente)) {
                excluirButton.setDisable(true);
                excluirButton.setVisible(false);
            }
            
            func.setText(entity.getFuncionario().getNome());
            try {
                cli.setText(entity.getCliente().getNome());
            } catch (NullPointerException n) {
                cli.setText("NÃO INFORMADO");
            }
            data.setText(OperacaoStringUtil.formatDataTimeValor(entity.getDia()));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getTotal()));
            valPg.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getPartePaga()));
            obs.setText(entity.getDescricao());
            f.buscarItensDaVendaPorIdDaVenda(entity.getId());

            itens.getItems().clear();
            ObservableList<ItemDeVenda> observableList = FXCollections.observableList(f.buscarItensDaVendaPorIdDaVenda(entity.getId()));
            itens.setItems(observableList);
        } else if (entity instanceof Divida) {
            try {
                func.setText(entity.getFuncionario().getNome());
            } catch (NullPointerException ne) {
                func.setText("Não informado");
            }

            cli.setText(entity.getCliente().getNome());
            data.setText(OperacaoStringUtil.formatDataTimeValor(entity.getDia()));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getTotal()));
            valPg.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getPartePaga()));
            obs.setText(entity.getDescricao());

            itens.setVisible(false);
            btImprimir.setVisible(false);
            btImprimirECF.setVisible(false);
            btPDF.setVisible(false);
        }

    }

    @Override
    public String getTitulo() {
        return entity.getOrigem();
    }

}
