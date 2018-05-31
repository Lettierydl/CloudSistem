package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.sis.model.financeiro.Divida;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import com.cs.ui.controller.dialog.DialogController;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
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
    private AutoCompleteTextField nomeP;
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
        iniciarPagePagamento();
        iniciarPageHistorico();
        iniciarPageDivida();
        iniciarPageVenda();
        iniciarPageVendaPrazo();
        JavaFXUtil.beginFoccusTextField(nomeP);

        nomeP.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {
            if (t.getCode() == KeyCode.F5) {
                try {
                    Cliente c = f.buscarClientePorNome(nomeP.getText());
                } catch (Exception e) {
                    return;
                }
                atualizarDebitoCliente();
            }
        });
    }

    public void atualizarDebitoCliente() {
        try {
            Cliente c = f.buscarClientePorNome(nomeP.getText());
            double oud_debito = c.getDebito();
            double new_debito = f.recalcularDebitoDoCliente(f.buscarClientePorNome(nomeP.getText()));
            if (oud_debito != new_debito) {
               JavaFXUtil.showDialog("Débito atualizado",
                        "Débito do Cliente atualizado com sucesso",
                        Alert.AlertType.WARNING);
            } else {
                JavaFXUtil.showDialog("Débito já atualizado",
                        "Débito do Cliente já esta atualizado");
            }
        } catch (Exception ex) {
            JavaFXUtil.showDialog(ex);
            Logger.getLogger(PagamentoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            f.atualizarStatusPagaveis(f.buscarClientePorNome(nomeP.getText()));
        } catch (EntidadeNaoExistenteException ex) {
            Logger.getLogger(PagamentoController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void iniciarPagePagamento() {
        MaskFieldUtil.monetaryField(valorP);
        MaskFieldUtil.upperCase(nomeP);
        JavaFXUtil.nextFielOnAction(nomeP, valorP);
        JavaFXUtil.nextFielOnAction(valorP, pagar);
        nomeP.setList(f.buscarNomeClientePorNomeQueInicia(""));
        preencherTabelaPagaveis();
        pagar.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                pagar(null);
            }
        });
    }

    @FXML
    void nomePDigitado(KeyEvent event) {
        String txt = nomeP.getText();
        if (txt == null || txt.isEmpty() || txt.length() < 3) {
            return;
        }
        try {
            Cliente c = f.buscarClientePorNome(txt);
            debitoP.setText(OperacaoStringUtil.formatarStringValorMoeda(c.getDebito()));
            if (c.getDebito() <= 0) {
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
            obs = JavaFXUtil.showDialogInput(
                    "Observação",
                    "Adicione observações sobre pagamento").toUpperCase();
        } catch (Exception ee) {
        }
    }

    @FXML
    public void pagar(ActionEvent e) {
        if (OperacaoStringUtil.converterStringValor(valorP.getText()) <= 0) {
            JavaFXUtil.showDialog(
                    "Valor Errado"
                    ,"O valor do pagamento não pode ser zero",
                    Alert.AlertType.ERROR);
            return;
        }
        try {
            double sumPagaveisSelected = 0.0;
            
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
            ButtonType bt = JavaFXUtil.showDialogOptions(
                    "Pagamento"
                    ,"Tem certeza que deseja adicionar o pagamento"
                    ,msg);
            if (bt.equals(ButtonType.YES)) {
                if(pags.isEmpty()){
                    f.adicionarPagamento(p);
                }else{
                    f.adicionarPagamento(p, pags);
                }
                c = f.buscarClientePorNome(nomeP.getText());
                JavaFXUtil.showDialog(
                        "Pagamento Salvo"
                        ,"Pagamento registrado com sucesso"
                        ,c.getDebito() == 0 ? "Cliente " + c.getNome() + " não possue mais débito"
                                        : "Cliente " + c.getNome() + " com "
                                        + OperacaoStringUtil.formatarStringValorMoeda(c.getDebito())
                                        + " de rebito restante"
                        );
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
            JavaFXUtil.showDialog("Erro ao registar o pagamento, entre em contato com o suporte",
                    ex);
        }
    }

    public void preencherTabelaPagaveis() {
        cliColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Cliente>("Cliente"));
        origColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Pagavel>("origem"));
        dataColP.setCellValueFactory(new PropertyValueFactory<Pagavel, String>("data"));
        restColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Double>("valorNaoPago"));
        valColP.setCellValueFactory(new PropertyValueFactory<Pagavel, Double>("total"));
        JavaFXUtil.colunValueMoedaFormat(restColP);
        JavaFXUtil.colunValueMoedaFormat(valColP);
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
            
            if(entity instanceof Venda && !controller.isOkClicked()){
                atualizarListaVendasAVista();
            }
                    
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
    private AutoCompleteTextField nomeD;
    @FXML
    private TableColumn ClienteH;
    @FXML
    private AutoCompleteTextField nomeH;
    @FXML
    private TableColumn funcionarioH;
    @FXML
    private TableColumn obsH;
    @FXML
    private TableColumn dataH;
    @FXML
    private TextField valorD;
    @FXML
    private Button salvarDivida;
    @FXML
    private TableView<Pagamento> historico;

    private void iniciarTabelaHistorico() {
        valorH.setCellValueFactory(new PropertyValueFactory<>("valor"));
        ClienteH.setCellValueFactory(new PropertyValueFactory<>("Cliente"));
        dataH.setCellValueFactory(new PropertyValueFactory<>("data"));
        funcionarioH.setCellValueFactory(new PropertyValueFactory<>("funcionario"));
        obsH.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        JavaFXUtil.colunValueMoedaFormat(valorH);
        JavaFXUtil.colunDataTimeFormat(dataH);
        JavaFXUtil.colunPessoaFormat(funcionarioH);
    }

    @FXML
    public void nomeHdigitado(KeyEvent event) {
        String txt = nomeH.getText();
        if (txt == null || txt.isEmpty() || txt.length() < 3) {
            historico.getItems().clear();
            return;
        }
        try {
            Cliente c = f.buscarClientePorNome(txt);
            ObservableList<Pagamento> observableList = FXCollections.observableList(f.getListaPagamentoDoCliente(c));
            historico.setItems(observableList);
        } catch (NonUniqueResultException | NoResultException ne) {
            historico.getItems().clear();
        }
    }

    private void iniciarPageHistorico() {
        MaskFieldUtil.upperCase(nomeH);
        nomeH.setList(f.buscarNomeClientePorNomeQueInicia(""));
        iniciarTabelaHistorico();
    }

    private void iniciarPageDivida() {
        MaskFieldUtil.upperCase(nomeD);
        nomeD.setList(f.buscarNomeClientePorNomeQueInicia(""));
        MaskFieldUtil.upperCase(descricaoD);
        MaskFieldUtil.monetaryField(valorD);
        salvarDivida.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                salvarDivida(null);
            }
        });
        JavaFXUtil.nextFielOnAction(nomeD, descricaoD);
        JavaFXUtil.nextFielOnAction(descricaoD, valorD);
        JavaFXUtil.nextFielOnAction(valorD, salvarDivida);

    }

    @FXML
    public void salvarDivida(ActionEvent event) {
        try {
            Cliente c = f.buscarClientePorNome(nomeD.getText());
            double va = OperacaoStringUtil.converterStringValor(valorD.getText());
            if (va <= 0) {
                JavaFXUtil.showDialog("Valor inválido",
                        "Valor inválido para a venda",
                        Alert.AlertType.ERROR);
                valorD.selectAll();
                valorD.requestFocus();
                return;
            } else {
                Divida d = new Divida();
                d.setTotal(va);
                d.setDia(Calendar.getInstance());
                d.setFuncionario(f.getFuncionarioLogado());
                if (!descricaoD.getText().isEmpty()) {
                    d.setDescricao(descricaoD.getText());
                }
                try {
                    f.adicionarDivida(d, c);
                    JavaFXUtil.showDialog("Dívida registrada"
                            ,"Divida salva com sucesso!"
                            ,"Cliente: " + c.getNome() + "\n"
                                    + "Total: " + valorD.getText() + "\n"
                                    + "Data: " + OperacaoStringUtil.formatDataTimeValor(d.getDia()))
                            ;
                    valorD.setText("");
                    nomeD.setText("");
                    descricaoD.setText("");
                    nomeD.requestFocus();
                } catch (Exception ex) {
                    JavaFXUtil.showDialog(ex);
                }
            }
        } catch (NonUniqueResultException | NoResultException ne) {
            JavaFXUtil.showDialog("Cliente não selecionado"
                    ,"Selecione o Cliente para a dívida",
                    Alert.AlertType.ERROR);
            nomeD.selectAll();
            nomeD.requestFocus();
        }
    }

    /*Venda a Vista*/
    @FXML
    private DatePicker dataV;
    @FXML
    private TableView<Venda> vendasAVista;
    
    @FXML
    private TableColumn colDataV;
    @FXML
    private TableColumn colFuncV;
    @FXML
    private TableColumn colValorV;
    @FXML
    private TableColumn colItensV;
    @FXML
    private TableColumn colVerV;

    private void iniciarPageVenda() {
        MaskFieldUtil.dateField(dataV.getEditor());
        dataV.setValue(JavaFXUtil.toLocalDate(Calendar.getInstance()));
        iniciarTabelaVendasAVista();
        atualizarListaVendasAVista();
    }

    private void atualizarListaVendasAVista() {
        try {
            Calendar dia = JavaFXUtil.toDate(dataV);
            vendasAVista.getItems().clear();
            List<Venda> avista = f.buscarVendaAVista(dia);
            ObservableList<Venda> observableList = FXCollections.observableList(avista);
            vendasAVista.setItems(observableList);
            configurarVerColunVenda();
        } catch (Exception e) {
        }
    }

    @FXML
    private void dataDigitadaV(ActionEvent event) {
        try {
            Calendar dia = OperacaoStringUtil.converterDataValor(dataV.getEditor().getText());
            atualizarListaVendasAVista();
        } catch (Exception e) {
        }
    }

    @FXML
    private void dataDigitadaPress(KeyEvent event) {
        try {
            Calendar dia = OperacaoStringUtil.converterDataValor(dataV.getEditor().getText());
            atualizarListaVendasAVista();
        } catch (Exception e) {
        }
    }

    private void iniciarTabelaVendasAVista() {
        colValorV.setCellValueFactory(new PropertyValueFactory<>("total"));
        colItensV.setCellValueFactory(new PropertyValueFactory<Venda, Integer>("quantidadeDeItens"));
        colDataV.setCellValueFactory(new PropertyValueFactory<>("data"));
        colFuncV.setCellValueFactory(new PropertyValueFactory<>("funcionario"));
        colVerV.setCellValueFactory(new PropertyValueFactory<Venda, Venda>(""));

        JavaFXUtil.colunValueMoedaFormat(colValorV);
        JavaFXUtil.colunPessoaFormat(colFuncV);
    }

    public void configurarVerColunVenda() {
        colVerV.setComparator(new Comparator<Venda>() {
            @Override
            public int compare(Venda p1, Venda p2) {
                return Double.compare(p1.getValorNaoPago(), p2.getValorNaoPago());
            }
        });
        colVerV.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Venda, Venda>, ObservableValue<Venda>>() {
            @Override
            public ObservableValue<Venda> call(TableColumn.CellDataFeatures<Venda, Venda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colVerV.setCellFactory(new Callback<TableColumn<Venda, Venda>, TableCell<Venda, Venda>>() {
            public TableCell<Venda, Venda> call(TableColumn<Venda, Venda> btnCol) {
                return new TableCell<Venda, Venda>() {
                    @Override
                    public void updateItem(final Venda obj, boolean empty) {
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

    
    /*Venda a prazo*/
    @FXML
    private DatePicker dataPR;
    @FXML
    private TableView<Venda> vendasAPrazo;
    @FXML
    private TableColumn colDataPR;
    @FXML
    private TableColumn colFuncPR;
    @FXML
    private TableColumn colClientePR;
    @FXML
    private TableColumn colValorPR;
    @FXML
    private TableColumn colItensPR;
    @FXML
    private TableColumn colVerPR;
    
    
    private void iniciarPageVendaPrazo() {
        MaskFieldUtil.dateField(dataPR.getEditor());
        dataPR.setValue(JavaFXUtil.toLocalDate(Calendar.getInstance()));
        iniciarTabelaVendasAPrazo();
        atualizarListaVendasAPrazo();
    }
    
    private void iniciarTabelaVendasAPrazo() {
        colValorPR.setCellValueFactory(new PropertyValueFactory<>("total"));
        colItensPR.setCellValueFactory(new PropertyValueFactory<Venda, Integer>("quantidadeDeItens"));
        colDataPR.setCellValueFactory(new PropertyValueFactory<>("data"));
        colFuncPR.setCellValueFactory(new PropertyValueFactory<>("funcionario"));
        colClientePR.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVerPR.setCellValueFactory(new PropertyValueFactory<Venda, Venda>(""));

        JavaFXUtil.colunValueMoedaFormat(colValorPR);
        JavaFXUtil.colunPessoaFormat(colFuncPR);
        JavaFXUtil.colunPessoaFormat(colClientePR);
    }
    
    
    private void atualizarListaVendasAPrazo() {
        try {
            Calendar dia = JavaFXUtil.toDate(dataPR);
            vendasAPrazo.getItems().clear();
            List<Venda> avista = f.buscarVendaAPrazo(dia);
            ObservableList<Venda> observableList = FXCollections.observableList(avista);
            vendasAPrazo.setItems(observableList);
            configurarVerColunVendaAPrazo();
        } catch (Exception e) {
        }
    }

    @FXML        
    private void dataDigitadaPR(ActionEvent event) {
        try {
            Calendar dia = OperacaoStringUtil.converterDataValor(dataPR.getEditor().getText());
            atualizarListaVendasAPrazo();
        } catch (Exception e) {
        }
    }
    @FXML
    private void dataDigitadaPressPR(KeyEvent event) {
        try {
            Calendar dia = OperacaoStringUtil.converterDataValor(dataPR.getEditor().getText());
            atualizarListaVendasAPrazo();
        } catch (Exception e) {
        }
    }
    
    public void configurarVerColunVendaAPrazo() {
        colVerPR.setComparator(new Comparator<Venda>() {
            @Override
            public int compare(Venda p1, Venda p2) {
                return Double.compare(p1.getValorNaoPago(), p2.getValorNaoPago());
            }
        });
        colVerPR.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Venda, Venda>, ObservableValue<Venda>>() {
            @Override
            public ObservableValue<Venda> call(TableColumn.CellDataFeatures<Venda, Venda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colVerPR.setCellFactory(new Callback<TableColumn<Venda, Venda>, TableCell<Venda, Venda>>() {
            public TableCell<Venda, Venda> call(TableColumn<Venda, Venda> btnCol) {
                return new TableCell<Venda, Venda>() {
                    @Override
                    public void updateItem(final Venda obj, boolean empty) {
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
    
    
}
