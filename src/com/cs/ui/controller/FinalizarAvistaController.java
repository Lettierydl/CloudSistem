package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.controller.configuracao.PermissaoFuncionario;
import com.cs.sis.model.exception.ParametrosInvalidosException;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class FinalizarAvistaController implements Initializable {

    private Facede f;
    private Venda atual;
    private ObservableList<ItemDeVenda> observableList;

    @FXML
    private TableView<ItemDeVenda> itens;

    @FXML
    private TableColumn<?, ?> qtCol;
    @FXML
    private TableColumn<?, ?> totalCol;
    @FXML
    private TableColumn<?, ?> produtoCol;
    @FXML
    private TableColumn<?, ?> valorCol;

    @FXML
    private TextField valorPago;
    @FXML
    private TextField desconto;
    
    @FXML
    private Button finalizarButton;
    @FXML
    CheckBox imprimir;

    @FXML
    private Text nomeSupermercado;

    @FXML
    private Label total;
    @FXML
    private Label troco;

    public FinalizarAvistaController() {
        f = Facede.getInstance();
    }

    @FXML
    public void voltar(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_VENDA);
    }

    @FXML
    public void finalizar(ActionEvent event) {
        if (atual.getTotal() <= 0) {
            Dialogs.create()
                    .title("Venda zerada")
                    .masthead("Por favor adicione algum item antes de filizar a venda")
                    .showError();
            Main.trocarDeTela(ControllerTelas.TELA_VENDA);
            return;
        }
        String txt = valorPago.getText();
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        double desc = OperacaoStringUtil.converterStringValor(desconto.getText());
        desc = (atual.getTotal() * desc)/100;
                
        if ((val+desc) < atual.getTotal()) {
            Dialogs.create()
                    .title("Valor incorreto")
                    .masthead("O valor pago pelo Cliente é inferior ao valor da venda")
                    .showError();
            valorPago.requestFocus();
            valorPago.selectAll();
            return;
        }
        try {
            if(desc > 0){
                try{
                    f.finalizarVendaAVista(atual, desc);
                }catch(ParametrosInvalidosException ee){
                    Dialogs dialog = Dialogs.create()
                        .title("Desconto com valor superior à venda")
                        .masthead("Desconto com valor superior à venda")
                        .message("Por favor, altera o valor do desconto");
                    dialog.showError();
                    desconto.requestFocus();
                    return;
                }
            }else{
                f.finalizarVendaAVista(atual);
            }
            if (imprimir.isSelected() && !f.imprimirVenda(atual)) {
                Dialogs dialog = Dialogs.create()
                        .title("Impressora não conectada")
                        .masthead("A impressora não esta respondendo")
                        .message("Por favor, contate o suporte");
                dialog.showError();
            }
        } catch (Exception ex) {
            Dialogs.create().showException(ex);
        }
        Main.trocarDeTela(ControllerTelas.TELA_VENDA);
        Dialogs dialog = Dialogs.create()
                .title("Venda Finalizada")
                .masthead("Venda à vista finalizada com sucesso")
                .message("Valor: " + OperacaoStringUtil.formatarStringValorMoeda(val)
                        + "\nTroco: " + troco.getText());
        dialog.style(DialogStyle.UNDECORATED);
        dialog.showInformation();
    }

    @FXML
    void enterKey(ActionEvent event) {
        String txt = valorPago.getText();
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        double desc = OperacaoStringUtil.converterStringValor(desconto.getText());
        desc = (atual.getTotal() * desc)/100;
        
        if ((val+desc) < atual.getTotal()) {
            Dialogs.create()
                    .title("Valor incorreto")
                    .masthead("O valor pago pelo Cliente é inferior ao valor da venda")
                    .showError();
            valorPago.requestFocus();
            valorPago.selectAll();
            return;
        }
        finalizarButton.requestFocus();
    }

    @FXML
    void valDigitado(KeyEvent event) {
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        double desc = OperacaoStringUtil.converterStringValor(desconto.getText());
        desc = (atual.getTotal() * desc)/100;
        
        if ((val+desc) > atual.getTotal()) {
            troco.setText(OperacaoStringUtil.formatarStringValorMoeda((val+desc) - atual.getTotal()));
        } else {
            troco.setText("0,00");
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        valorCol.setCellValueFactory(new PropertyValueFactory<>("valorProduto"));
        qtCol.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        produtoCol.setCellValueFactory(new PropertyValueFactory<>("DescricaoProduto"));

        MaskFieldUtil.monetaryField(valorPago);
        MaskFieldUtil.monetaryField(desconto);
        JavaFXUtil.colunValueMoedaFormat(totalCol);
        JavaFXUtil.colunValueMoedaFormat(valorCol);
        JavaFXUtil.colunValueQuantidadeFormat(qtCol);
        preencherInformacoes();
        
        if(!f.getValor(PermissaoFuncionario.DESCONTO_NA_VENDA, f.getFuncionarioLogado().getTipoDeFuncionario())){
            desconto.setDisable(true);
        }
        
        finalizarButton.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                finalizar(null);
            }
        });
        
        JavaFXUtil.beginFoccusTextField(valorPago);
    }

    public void preencherInformacoes() {
        atual = f.getVendaAtual();
        observableList = FXCollections.observableList(getItensDaVenda());
        itens.setItems(observableList);
        total.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal()));
        valorPago.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal()));
    }

    public List<ItemDeVenda> getItensDaVenda() {
        List<ItemDeVenda> it = f.buscarItensDaVendaPorIdDaVenda(f.getVendaAtual().getId());
        Collections.sort(it);
        return it;
    }

}
