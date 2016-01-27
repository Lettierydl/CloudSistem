package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.financeiro.Divida;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.dialog.Dialogs;

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

    public PagavelDialogController() {
        super();
    }

    @FXML
    void imprimir(ActionEvent event) {

    }

    @FXML
    void imprimirECF(ActionEvent event) {
        boolean imprimir = f.imprimirVenda((Venda) entity);
        if(!imprimir){
            Dialogs.create()
                    .title("Impressora não conectada")
                    .masthead("Impressora não conectada")
                    .message("Por favor entre em contato com o suporte")
                    .showError();
        }
    }

    @FXML
    void gerarPDF(ActionEvent event) {
        try {
            f.gerarPdfDaVendaVenda((Venda) entity, f.buscarItensDaVendaPorIdDaVenda(entity.getId()));
        } catch (FuncionarioNaoAutorizadoException ex) {
            Dialogs.create()
                    .title("Funcionário não autorizado")
                    .masthead("Funcionário não autorizado a gerar PDFs")
                    .message("Por favor entre com um funcionário autorizado")
                    .showError();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colVal.setCellValueFactory(new PropertyValueFactory<>("valorProduto"));
        colTot.setCellValueFactory(new PropertyValueFactory<>("total"));
        colQt.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("descricaoProduto"));

        JavaFXUtil.colunValueModedaFormat(colVal);
        JavaFXUtil.colunValueModedaFormat(colTot);
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

    @Override
    public void setEntity(Pagavel entity) {
        this.entity = entity;
        if(entity instanceof Venda){
            func.setText(entity.getFuncionario().getNome());
            cli.setText(entity.getCliente().getNome());
            data.setText(OperacaoStringUtil.formatDataTimeValor(entity.getDia()));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getTotal()));
            valPg.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getPartePaga()));
            obs.setText(entity.getDescricao());
            f.buscarItensDaVendaPorIdDaVenda(entity.getId());

            itens.getItems().clear();
            ObservableList<ItemDeVenda> observableList = FXCollections.observableList(f.buscarItensDaVendaPorIdDaVenda(entity.getId()));
            itens.setItems(observableList);
        }else if(entity instanceof Divida){
            try{
                func.setText(entity.getFuncionario().getNome());
            }catch(NullPointerException ne){
                func.setText("Não informado");
            }
            cli.setText(entity.getCliente().getNome());
            data.setText(OperacaoStringUtil.formatDataTimeValor(entity.getDia()));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getTotal()));
            valPg.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getPartePaga()));
            obs.setText(entity.getDescricao());
            itens.setVisible(false);
        }
        
        
    }

    @Override
    public String getTitulo() {
        return entity.getOrigem();
    }

}
