package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import com.cs.ui.controller.dialog.DialogController;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class PagamentoController implements Initializable {

    @FXML
    private StackPane stack;
    Facede f;
    String obs = "";
    List<Pagavel> pags = new ArrayList<>();
    static FadeTransition fade = new FadeTransition();

    @FXML
    private Label debitoP;
    @FXML
    private GridPane form;
    @FXML
    private Button pagar;
    @FXML
    private TableView<Pagavel> pagaveis;
    @FXML
    private TextField nomeP;
    @FXML
    private TextField valorP;
    @FXML
    public TableColumn cliColP;
    @FXML
    public TableColumn valColP;
    @FXML
    public TableColumn pagColP;
    @FXML
    public TableColumn restColP;
    @FXML
    public TableColumn origColP;
    @FXML
    public TableColumn dataColP;

    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        f = Facede.getInstance();
        MaskFieldUtil.monetaryField(valorP);
        MaskFieldUtil.upperCase(nomeP);
        MaskFieldUtil.upperCase(nomeH);
        JavaFXUtil.nextFielOnAction(nomeP, valorP);
        JavaFXUtil.nextFielOnAction(valorP, pagar);
        nomeP.requestFocus();
        nomeP.selectAll();
        preencherTabelaPagaveis();
        iniciarTabelaHistorico();
        pagar.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                pagar(null);
            }
        });
        
        salvarDivida.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                salvarDivida(null);
            }
        });
        JavaFXUtil.beginFoccusTextField(nomeP);
    }
    
    
    @FXML
    void nomePDigitado(KeyEvent event) {
        String txt = nomeP.getText();
        if (txt == null || txt.isEmpty() || txt.length() < 3)  {
            return;
        }
        try{
            TextFields.bindAutoCompletion(nomeP, f.buscarNomeClientePorNomeQueInicia(nomeP.getText()) );
        }catch(Exception e){}
        try {
            Cliente c = f.buscarClientePorNome(txt);
            debitoP.setText(OperacaoStringUtil.formatarStringValorMoeda(c.getDebito()));
            if(c.getDebito() >= 0){
                return;
            }
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDuration(VariaveisDeConfiguracaoUtil.DURACAO_FADE);
            fade.setNode(form);
            fade.play();
            form.setDisable(false);
        } catch (NonUniqueResultException | NoResultException ne) {
            if (form.getOpacity() != 0) {
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.setDuration(VariaveisDeConfiguracaoUtil.DURACAO_FADE);
                fade.setNode(form);
                fade.play();
                form.setDisable(true);
            }
        } finally {
            atualizarListaPagaveis();
        }
    }

    @FXML
    public void adicionarObservacao(ActionEvent e) {
        try {
            Dialogs dialog = Dialogs.create()
                    .title("Observação")
                    .masthead("Adicione observações sobre pagamento");
            Optional<String> text = dialog.showTextInput(obs.toUpperCase());
            obs = text.get();
        } catch (Exception ee) {
        }
    }

    @FXML
    public void pagar(ActionEvent e) {
        if(OperacaoStringUtil.converterStringValor(valorP.getText()) <=0){
            Dialogs.create()
                    .title("Valor Errado")
                    .masthead("O valor do pagamento não pode ser zero")
                    .actions(Dialog.Actions.YES)
                    .showError();
            return;
        }
        try {
            Pagamento p = new Pagamento();
            p.setCliente(f.buscarClientePorNome(nomeP.getText()));
            p.setFuncionario(f.getFuncionarioLogado());
            p.setObservacao(obs);
            p.setValor(OperacaoStringUtil.converterStringValor(valorP.getText()));
            double troco = 0;
            Cliente c = f.buscarClientePorNome(nomeP.getText());
            if (c.getDebito() < p.getValor()) {
                troco = p.getValor() - c.getDebito();
            }
            String msg = "Cliente: " + c.getNome() + "\n" + "Valor: " + valorP.getText() + "\n"
                    + (obs.isEmpty() ? "" : "Observação: " + obs + "\n")
                    + (troco > 0 ? "Troco: " + OperacaoStringUtil.formatarStringValorMoeda(troco) : "");
            Dialogs dialog = Dialogs.create()
                    .title("Pagamento")
                    .masthead("Tem certeza que deseja adicionar o pagamento")
                    .message(msg)
                    .actions(Dialog.Actions.YES, Dialog.Actions.CANCEL);
            dialog.style(DialogStyle.UNDECORATED);
            if (dialog.showConfirm().toString().equals("YES")) {
                f.adicionarPagamento(p);
                c = f.buscarClientePorNome(nomeP.getText());
                Dialogs.create()
                        .title("Pagamento Salvo")
                        .masthead("Pagamento registrado com sucesso")
                        .message(c.getDebito() == 0 ? "Cliente " + c.getNome() + " não possue mais débito"
                                        : "Cliente " + c.getNome() + " com "
                                        + OperacaoStringUtil.formatarStringValorMoeda(c.getDebito())
                                        + " de rebito restante"
                        ).showInformation();
                nomeP.setText("");
                valorP.setText("");
                debitoP.setText("");
                obs = "";
                pags.clear();
                atualizarListaPagaveis();
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.setDuration(VariaveisDeConfiguracaoUtil.DURACAO_FADE);
                fade.setNode(form);
                fade.play();
                form.setDisable(true);
            }
        } catch (Exception ex) {
            Dialogs.create().title("Erro ao registrar o pagamento")
                    .masthead("Erro ao registar o pagamento, entre em contato com o suporte")
                    .showException(ex);
        }
    }

    public void preencherTabelaPagaveis() {
        cliColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Cliente>("cliente"));
        origColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Pagavel>("origem"));
        dataColP.setCellValueFactory(new PropertyValueFactory<Pagavel, String>("data"));
        restColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Double>("valorNaoPago"));
        valColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Double>("total"));
        JavaFXUtil.colunValueModedaFormat(restColP);
        JavaFXUtil.colunValueModedaFormat(valColP);
        atualizarListaPagaveis();
    }

    public void addPagavel(Pagavel p) {
        pags.add(p);
        atualizarValorPagaveis();
    }

    public void removerPagavel(Pagavel p) {
        pags.remove(p);
        atualizarValorPagaveis();
    }

    private void atualizarValorPagaveis() {
        double sum = 0;
        for (Pagavel pp : pags) {
            sum += pp.getValorNaoPago();
        }
        valorP.setText(OperacaoStringUtil.formatarStringValorMoeda(sum));
    }

    public void atualizarListaPagaveis() {
        try {
            String text = nomeP.getText();
            pagaveis.getItems().clear();
            f.buscarClientePorNome(text);
            ObservableList<Pagavel> observableList = FXCollections.observableList(f.buscarPagaveisNaoPagoDoCliente(text));
            pagaveis.setItems(observableList);
            configurarPagarColun();
            configurarOrigemColun();
        } catch (Exception e) {
        }
    }

    public void configurarPagarColun() {
        pagColP.setComparator(new Comparator<Pagavel>() {
            @Override
            public int compare(Pagavel p1, Pagavel p2) {
                return Double.compare(p1.getValorNaoPago(), p2.getValorNaoPago());
            }
        });
        pagColP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pagavel, Pagavel>, ObservableValue<Pagavel>>() {
            @Override
            public ObservableValue<Pagavel> call(TableColumn.CellDataFeatures<Pagavel, Pagavel> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        pagColP.setCellFactory(new Callback<TableColumn<Pagavel, Pagavel>, TableCell<Pagavel, Pagavel>>() {
            @Override
            public TableCell<Pagavel, Pagavel> call(TableColumn<Pagavel, Pagavel> btnCol) {
                return new TableCell<Pagavel, Pagavel>() {
                    @Override
                    public void updateItem(final Pagavel obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final CheckBox button = new CheckBox();
                        button.setSelected(false);
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (button.isSelected()) {
                                    addPagavel(obj);
                                } else {
                                    removerPagavel(obj);
                                }
                            }
                        });
                    }
                };
            }
        });
    }

    public void configurarOrigemColun() {
        origColP.setComparator(new Comparator<Pagavel>() {
            @Override
            public int compare(Pagavel p1, Pagavel p2) {
                return Double.compare(p1.getValorNaoPago(), p2.getValorNaoPago());
            }
        });
        origColP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pagavel, Pagavel>, ObservableValue<Pagavel>>() {
            @Override
            public ObservableValue<Pagavel> call(TableColumn.CellDataFeatures<Pagavel, Pagavel> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        origColP.setCellFactory(new Callback<TableColumn<Pagavel, Pagavel>, TableCell<Pagavel, Pagavel>>() {
            public TableCell<Pagavel, Pagavel> call(TableColumn<Pagavel, Pagavel> btnCol) {
                return new TableCell<Pagavel, Pagavel>() {
                    @Override
                    public void updateItem(final Pagavel obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final Label button = new Label(obj.getOrigem());
                        button.setCursor(Cursor.HAND);
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                showDialogView(obj);
                            }
                        });
                    }
                };
            }
        });
    }

    public boolean showDialogView(Pagavel entity) {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/dialogPagavel.fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<Pagavel> controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    /*Historico*/
    @FXML
    private TableColumn<Pagamento, Double> valorH;
    @FXML
    private TextField descricaoD;
    @FXML
    private TextField nomeD;
    @FXML
    private TableColumn clienteH;
    @FXML
    private TextField nomeH;
    @FXML
    private TableColumn funcionarioH;
    @FXML
    private TableColumn dataH;
    @FXML
    private TextField valorV;
    @FXML
    private TableColumn obsH;
    @FXML
    private Button salvarDivida;
    @FXML
    private TableView historico;

    
    private void iniciarTabelaHistorico(){
        valorH.setCellValueFactory(new PropertyValueFactory<>("valor"));
        clienteH.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        dataH.setCellValueFactory(new PropertyValueFactory<>("data"));
        funcionarioH.setCellValueFactory(new PropertyValueFactory<>("funcionario"));
        obsH.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        JavaFXUtil.colunValueModedaFormat(valorH);
        JavaFXUtil.colunDataTimeFormat(dataH);
    }
    
    @FXML
    public void salvarDivida(ActionEvent event) {

    }

    @FXML
    public void nomeHdigitado(KeyEvent event){
        String txt = nomeH.getText();
        if (txt == null || txt.isEmpty() || txt.length() < 3)  {
            return;
        }
        try{
            //TextFields.bindAutoCompletion(nomeH, f.buscarNomeClientePorNomeQueInicia(nomeH.getText()) );
        }catch(Exception e){}
        try {
            Cliente c = f.buscarClientePorNome(txt);
            ObservableList<Pagamento> observableList = FXCollections.observableList(f.getListaPagamentoDoCliente(c));
            
            historico.setItems(observableList);           
        } catch (NonUniqueResultException | NoResultException ne) { } finally {
            historico.getItems().clear();
        }
    }
    
    
}
