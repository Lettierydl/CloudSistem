package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;
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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class FinalizarAprazoController implements Initializable {

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
    private TextField nomeCli;
    
    @FXML
    private TextField valorPago;
    @FXML
    private Button finalizarButton;
    @FXML
    CheckBox imprimir;

    @FXML
    private Text nomeSupermercado;

    @FXML
    private Label total;
    @FXML
    private Label subTotal;
    
    @FXML
    private TextField observacao;

    public FinalizarAprazoController() {
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
        
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        if (val >= atual.getTotal()) {
            Dialogs.create()
                    .title("Valor incorreto")
                    .masthead("O valor pago pelo cliente deve ser inferior ao valor da venda")
                    .showError();
            valorPago.requestFocus();
            valorPago.selectAll();
            return;
        }
        Cliente c = null;
        try{
            c = f.buscarClientePorNome(nomeCli.getText());
        }catch(NoResultException | NonUniqueResultException ne){
            Dialogs.create()
                    .title("Cliente não cadastrado")
                    .masthead("Por favor digite um nome válido para o cliente")
                    .showError();
            return;
        }
        
        double debito = 0;
        try {
            debito = f.finalizarVendaAprazo(atual, c, val);
            if (imprimir.isSelected() && !f.imprimirVenda(atual)) {
                Dialogs dialog = Dialogs.create()
                        .title("Impressora não conectada")
                        .masthead("A impressora não esta respondendo")
                        .message("Por favor, contate o suporte");
                dialog.showError();
            }
        } catch (Exception ex) {
            Dialogs.create().showException(ex);
            return;
        }
        Main.trocarDeTela(ControllerTelas.TELA_VENDA);
        Dialogs dialog = Dialogs.create()
                .title("Venda Finalizada")
                .masthead("Venda à prazo finalizada com sucesso")
                .message("Cliente: " + c.getNome()
                        +"\nValor não pago da venda: " + OperacaoStringUtil.formatarStringValorMoeda(atual.getValorNaoPago())
                        + "\nNovo Débito do cliente: "+ OperacaoStringUtil.formatarStringValorMoeda(debito));
        dialog.style(DialogStyle.UNDECORATED);
        dialog.showInformation();
    }
    
    @FXML
    void nomeDigitado(KeyEvent event){
        
    }
    
    @FXML
    void entradaDigitado(KeyEvent event){
        String txt = valorPago.getText();
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        if(atual.getTotal() - val > 0){
            subTotal.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal() - val));
        }else{
          subTotal.setText("0,00");
        }
    }
    
    @FXML
    void enterKeyEntrada(ActionEvent event) {
        String txt = valorPago.getText();
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        if (val >= atual.getTotal()) {
            Dialogs.create()
                    .title("Valor incorreto")
                    .masthead("O valor pago pelo cliente deve ser inferior ao valor da venda")
                    .showError();
            valorPago.requestFocus();
            valorPago.selectAll();
            return;
        }
        finalizarButton.requestFocus();
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
        MaskFieldUtil.upperCase(nomeCli);
        MaskFieldUtil.upperCase(observacao);
        JavaFXUtil.colunValueModedaFormat(totalCol);
        JavaFXUtil.colunValueModedaFormat(valorCol);
        JavaFXUtil.colunValueQuantidadeFormat(qtCol);
        preencherInformacoes();
        
        JavaFXUtil.nextFielOnAction(nomeCli, valorPago);
        JavaFXUtil.nextFielOnAction(valorPago, observacao);
        JavaFXUtil.nextFielOnAction(observacao, finalizarButton);
        
        finalizarButton.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                finalizar(null);
            }
        });
        
        JavaFXUtil.beginFoccusTextField(nomeCli);
    }

    public void preencherInformacoes() {
        atual = f.getVendaAtual();
        observableList = FXCollections.observableList(getItensDaVenda());
        itens.setItems(observableList);
        total.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal()));
    }

    public List<ItemDeVenda> getItensDaVenda() {
        List<ItemDeVenda> it = f.buscarItensDaVendaPorIdDaVenda(f.getVendaAtual().getId());
        Collections.sort(it);
        return it;
    }

}