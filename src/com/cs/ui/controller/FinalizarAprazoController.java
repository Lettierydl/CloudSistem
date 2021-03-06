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
import com.cs.sis.model.exception.EstadoInvalidoDaVendaAtualException;
import com.cs.sis.model.exception.ParametrosInvalidosException;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class FinalizarAprazoController implements Initializable {

    private Facede f;
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
    private AutoCompleteTextField nomeCli;
    
    @FXML
    private TextField valorPago;
    @FXML
    private Button finalizarButton;
    @FXML
    CheckBox imprimir;

    @FXML
    private Label total;
    @FXML
    private Label subTotal;
    
    @FXML
    private Text razaoSocial;
    
    @FXML
    private TextField observacao;

    public FinalizarAprazoController() {
        f = Facede.getInstance();
    }

    @FXML
    public void voltar(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_VENDA);
    }
    
    private boolean finalizando = false;
    @FXML
    public void finalizar(ActionEvent event) {
        if(finalizarButton.isDisable()){
            return;
        }
        finalizarButton.setDisable(true);
        finalizarButton.setText("Finalizando...");
        Platform.runLater(() -> {
            finalizarButton.setDisable(true);
            finalizarButton.setText("Finalizando...");
        });
        
        
        Venda atual = f.getVendaAtual();
        if (atual.getTotal() <= 0) {
            JavaFXUtil.showDialog("Venda zerada",
                    "Por favor adicione algum item antes de filizar a venda",
            Alert.AlertType.ERROR);
            Main.trocarDeTela(ControllerTelas.TELA_VENDA);
            return;
        }
        
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        if (val >= atual.getTotal()) {
            JavaFXUtil.showDialog("Valor incorreto",
                    "O valor pago pelo Cliente deve ser inferior ao valor da venda",
                    Alert.AlertType.ERROR);
            valorPago.requestFocus();
            valorPago.selectAll();
            return;
        }
        Cliente c = null;
        try{
            c = f.buscarClientePorNome(nomeCli.getText());
        }catch(NoResultException | NonUniqueResultException ne){
            JavaFXUtil.showDialog("Cliente não cadastrado",
                    "Por favor digite um nome válido para o Cliente",
                    Alert.AlertType.ERROR);
            return;
        }
        
        
        
        double debito = 0;
        try {
            if(finalizando){
              return;  
            }else{
               finalizando = true; 
            }
            try{
                f.finalizarVendaAprazo(c, val, observacao.getText());
                debito = f.buscarClientePorId(c.getId()).getDebito();
            }catch(EstadoInvalidoDaVendaAtualException iee){
                debito = f.buscarClientePorId(c.getId()).getDebito();
            }catch(ParametrosInvalidosException pe){
                pe.printStackTrace();
                debito = f.buscarClientePorId(c.getId()).getDebito();
            }
            atual = f.buscarVendaPeloId(atual.getId());
            //atual.setObservacao(observacao.getText());
            //atual.setCliente(c);
            if (imprimir.isSelected() && !f.imprimirVenda(atual)) {
                JavaFXUtil.showDialog(
                        "Impressora não conectada",
                        "A impressora não esta respondendo",
                        "Por favor, contate o suporte",
                                Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            JavaFXUtil.showDialog(ex);
            finalizando = false;
            return;
        }
        Main.trocarDeTela(ControllerTelas.TELA_VENDA);
        JavaFXUtil.showDialog("Venda Finalizada",
                "Venda à prazo finalizada com sucesso",
                "Cliente: " + c.getNome()
                        +"\nValor não pago da venda: " + OperacaoStringUtil.formatarStringValorMoeda(atual.getValorNaoPago())
                        + "\nNovo Débito do Cliente: "+ OperacaoStringUtil.formatarStringValorMoeda(debito));
        
    }
    
    @FXML
    void nomeDigitado(KeyEvent event){
        
    }
    
    @FXML
    void entradaDigitado(KeyEvent event){
        String txt = valorPago.getText();
        double val = OperacaoStringUtil.converterStringValor(valorPago.getText());
        Venda atual = f.getVendaAtual();
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
        Venda atual = f.getVendaAtual();
        if (val >= atual.getTotal()) {
            JavaFXUtil.showDialog("Valor incorreto",
                    "O valor pago pelo Cliente deve ser inferior ao valor da venda",
                    Alert.AlertType.ERROR);
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
        JavaFXUtil.colunValueMoedaFormat(totalCol);
        JavaFXUtil.colunValueMoedaFormat(valorCol);
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
        nomeCli.setList(f.buscarNomeClientePorNomeQueInicia(""));
        JavaFXUtil.beginFoccusTextField(nomeCli);
        
        razaoSocial.setText(VariaveisDeConfiguracaoUtil.getNomeEstabelecimento());
    }

    public void preencherInformacoes() {
        observableList = FXCollections.observableList(getItensDaVenda());
        itens.setItems(observableList);
        total.setText(OperacaoStringUtil.formatarStringValorMoeda(f.getVendaAtual().getTotal()));
    }

    public List<ItemDeVenda> getItensDaVenda() {
        List<ItemDeVenda> it = f.buscarItensDaVendaPorIdDaVenda(f.getVendaAtual().getId());
        Collections.sort(it);
        return it;
    }

}
