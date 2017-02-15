package com.cs.ui.controller;

import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.ui.controller.dialog.DialogController;
import java.io.File;
import java.io.IOException;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class BalancoController implements Initializable {

    private Facede f = Facede.getInstance();

    /*Relatorio de Vendas*/
    @FXML
    private PieChart vGrafico;
    @FXML
    private DatePicker vDe;
    @FXML
    private DatePicker vAte;
    @FXML
    private Label vAVista;
    @FXML
    private Label vAPrazo;
    @FXML
    private Label vDivida;
    @FXML
    private Label vTotalVendido;
    @FXML
    private Label vAVistaU;
    @FXML
    private Label vAPrazoU;
    @FXML
    private Label vDividaU;

    /*Entrada de Caixa*/
    @FXML
    private PieChart eGrafico;
    @FXML
    private DatePicker eDe;
    @FXML
    private DatePicker eAte;
    @FXML
    private Label eTotalCaixa;
    @FXML
    private Label ePagamentos;
    @FXML
    private Label eAVista;

    /*Histórico do Cliente*/
    @FXML
    private PieChart hGrafico;
    @FXML
    private TableView<Pagavel> hTable;
    @FXML
    private TableColumn<?, ?> colDataH;
    @FXML
    private TableColumn<?, ?> colTotalH;
    @FXML
    private TableColumn<?, ?> colPagaH;
    @FXML
    private TableColumn colVerH;

    @FXML
    private DatePicker hDe;
    @FXML
    private DatePicker hAte;
    @FXML
    private AutoCompleteTextField hNome;

    /*Geral*/
    @FXML
    private DatePicker gDe;
    @FXML
    private DatePicker gAte;
    
    @FXML
    private CheckBox gEstoqueNegativo;

    /*Saida de Produtos*/
    @FXML
    private StackedBarChart<String, Number> sGrafico;
    @FXML
    private DatePicker sDe;
    @FXML
    private DatePicker sAte;
    @FXML
    private Label sLucro;
    @FXML
    private Label sVendido;

    /*Debitos dos Clientes*/
    //@FXML
    public TextField dDebito;
    @FXML
    public Label dSomaDebito;
    @FXML
    public Label dTotalClientes;

    @FXML
    private TableView<String> dTable;
    @FXML
    private TableColumn<String, Object[]> colNomeD;
    @FXML
    private TableColumn<String, Object[]> colDebitoD;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hNome.setList(f.buscarNomeClientePorNomeQueInicia(""));

        colDataH.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colTotalH.setCellValueFactory(new PropertyValueFactory<>("total"));
        colPagaH.setCellValueFactory(new PropertyValueFactory<>("partePaga"));
        colVerH.setCellValueFactory(new PropertyValueFactory<>(""));
        
        MaskFieldUtil.upperCase(hNome);
        JavaFXUtil.nextFielOnAction(hNome, hDe);

        MaskFieldUtil.monetaryField(dDebito);
        try {
            configurarColunasDebito();
            double sum = f.getRelatorioSomaTotalDeDebitos();
            dSomaDebito.setText(OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(sum));
            List<Object[]> data = f.getRelatorioClientesComDebitoMaiorQue(0);
            dTotalClientes.setText(OperacaoStringUtil.formatarStringValorInteger(data.size()));
            
            ObservableList tableData = FXCollections.observableArrayList(data);
            dTable.setItems(tableData);
        } catch (FuncionarioNaoAutorizadoException ex) {
        }

    }

    @FXML
    void gerarDebito() {
        double deb = OperacaoStringUtil.converterStringValor(dDebito.getText());
        try {
            double sum = f.getRelatorioSomaTotalDeDebitosMaiorQue(deb);
            dSomaDebito.setText(OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(sum));
            
            dTable.getItems().clear();
            
            List<Object[]> data;
            if(deb == 0){
                data = f.getRelatorioClientesComDebitoMaiorQue(deb);
            }else{
                data = f.getRelatorioDebitoDosClientes();
            }
            
            dTotalClientes.setText(OperacaoStringUtil.formatarStringValorInteger(data.size()));
            
            ObservableList tableData = FXCollections.observableArrayList(data);
            dTable.setItems(tableData);
        } catch (FuncionarioNaoAutorizadoException ex) {
            JavaFXUtil.showDialog("Funcionário não autorizado",
                    "Funcionário não autorizado a gerar PDFs",
                    "Por favor entre com um funcionário autorizado",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void gerarPDFDebito() {
        double deb = OperacaoStringUtil.converterStringValor(dDebito.getText());
        try {
            double sum = f.getRelatorioSomaTotalDeDebitosMaiorQue(deb);
            dSomaDebito.setText(OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(sum));
            
            dTable.getItems().clear();
            List<Object[]> data = f.getRelatorioClientesComDebitoMaiorQue(deb);
            
            dTotalClientes.setText(OperacaoStringUtil.formatarStringValorInteger(data.size()));
            ObservableList tableData = FXCollections.observableArrayList(data);
            dTable.setItems(tableData);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar PDF");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            String name = "Relatório_Debito_Clientes"
                    + new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime()) 
                    + ".pdf";

            fileChooser.setInitialFileName(name);
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PDF", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(dSomaDebito.getScene().getWindow());
            if (file != null) {
                f.gerarPdfRelatorioDebitoClientes(deb, file);
                JavaFXUtil.showDialog("PDF salvo com sucesso",
                        "PDF salvo com sucesso",
                        file.getAbsolutePath());
            }
        } catch (FuncionarioNaoAutorizadoException ex) {
            JavaFXUtil.showDialog("Funcionário não autorizado",
                    "Funcionário não autorizado a gerar PDFs",
                    "Por favor entre com um funcionário autorizado",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void gerarHistorico(ActionEvent event) {
        Cliente c;

        try {
            c = f.buscarClientePorNome(hNome.getText());
        } catch (Exception e) {
            JavaFXUtil.showDialog("Cliente não cadastrado",
                    "Cliente " + hNome.getText() + " não encontrado",
                    Alert.AlertType.ERROR);
            return;
        }

        List<Pagavel> pagaveis = new ArrayList<>();
        try {
            pagaveis = f.buscarPagaveisDoCliente(c,
                    JavaFXUtil.toDate(hDe).getTime(),
                    JavaFXUtil.toDate(hAte).getTime());
        } catch (Exception e) {
            if (JavaFXUtil.toDate(hDe) == null || JavaFXUtil.toDate(hAte) == null) {
                JavaFXUtil.showDialog("Data não preenchida",
                        "Por favor preencha a data desejada",
                        Alert.AlertType.ERROR);
                return;
            }
        }

        JavaFXUtil.colunValueMoedaFormat(colPagaH);
        JavaFXUtil.colunValueMoedaFormat(colTotalH);
        JavaFXUtil.colunDataTimeFormat(colDataH);
        configurarVerColunVenda();

        hTable.setItems(FXCollections.observableList(pagaveis));

        List<Pagamento> pagamentos = new ArrayList<Pagamento>();
        try {
            pagamentos = f.getListaPagamentoDoCliente(c,
                    JavaFXUtil.toDate(hDe).getTime(),
                    JavaFXUtil.toDate(hAte).getTime());
        } catch (Exception e) {
        }

        double vendido = 0;
        for (Pagavel p : pagaveis) {
            vendido += p.getTotal();
        }

        double pago = 0;
        for (Pagamento p : pagamentos) {
            pago += p.getValor();
        }

        ObservableList<PieChart.Data> dados
                = FXCollections.observableArrayList(
                        new PieChart.Data("Vendido", vendido),
                        new PieChart.Data("Pago", pago));
        hGrafico.setData(dados);
        hGrafico.setTitle("Pagamento X Compra");
        hGrafico.setLabelsVisible(true);
    }

    @FXML
    void gerarEntradaCaixa(ActionEvent event) {

        double[] val = {0.0, 0.0};
        try {
            val = f.getRelatorioDeEntradaDeCaixa(JavaFXUtil.toDate(eDe).getTime(), JavaFXUtil.toDate(eAte).getTime());
        } catch (Exception e) {
            if (JavaFXUtil.toDate(eDe) == null || JavaFXUtil.toDate(eAte) == null) {
                JavaFXUtil.showDialog("Data não preenchida",
                        "Por favor preencha a data desejada",
                        Alert.AlertType.ERROR);
                return;
            }
        }

        eAVista.setText(OperacaoStringUtil.formatarStringValorMoeda(val[0]));
        ePagamentos.setText(OperacaoStringUtil.formatarStringValorMoeda(val[1]));
        eTotalCaixa.setText(OperacaoStringUtil.formatarStringValorMoeda(val[0] + val[1]));

        ObservableList<PieChart.Data> dados
                = FXCollections.observableArrayList(
                        new PieChart.Data("Vendas à Vista", val[0]),
                        new PieChart.Data("Pagamentos", val[1]));
        eGrafico.setData(dados);
        eGrafico.setTitle("Receitas");
        eGrafico.setLabelsVisible(true);
    }

    @FXML
    void gerarRelatorioVendas(ActionEvent event) {
        double[] val = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        try {
            val = f.getRelatorioDeVendas(JavaFXUtil.toDate(vDe).getTime(), JavaFXUtil.toDate(vAte).getTime());
        } catch (Exception e) {
            if (JavaFXUtil.toDate(vDe) == null || JavaFXUtil.toDate(vAte) == null) {
                JavaFXUtil.showDialog("Data não preenchida",
                        "Por favor preencha a data desejada",
                        Alert.AlertType.ERROR);
                return;
            }
        }

        vAVista.setText(OperacaoStringUtil.formatarStringValorMoeda(val[0]));
        vAPrazo.setText(OperacaoStringUtil.formatarStringValorMoeda(val[1]));
        vDivida.setText(OperacaoStringUtil.formatarStringValorMoeda(val[2]));

        vTotalVendido.setText(OperacaoStringUtil.formatarStringValorMoeda(val[0] + val[1] + val[2]));

        vAVistaU.setText(OperacaoStringUtil.formatarStringQuantidadeInteger(val[3]));
        vAPrazoU.setText(OperacaoStringUtil.formatarStringQuantidadeInteger(val[4]));
        vDividaU.setText(OperacaoStringUtil.formatarStringQuantidadeInteger(val[5]));

        ObservableList<PieChart.Data> dados
                = FXCollections.observableArrayList(
                        new PieChart.Data("Vendas à Vista", val[0]),
                        new PieChart.Data("Vendas à Prazo", val[1]),
                        new PieChart.Data("Dívidas", val[2]));
        vGrafico.setData(dados);
        vGrafico.setTitle("Vendas");
        vGrafico.setLabelsVisible(true);

    }

    @FXML
    void gerarSaidaProduto(ActionEvent event) {
        double[] val = {0.0, 0.0};
        try {
            val = f.getRelatorioDeProduto(JavaFXUtil.toDate(sDe).getTime(), JavaFXUtil.toDate(sAte).getTime());
        } catch (Exception e) {
            if (JavaFXUtil.toDate(sDe) == null || JavaFXUtil.toDate(sAte) == null) {
                JavaFXUtil.showDialog("Data não preenchida",
                        "Por favor preencha a data desejada",
                        Alert.AlertType.ERROR);
                return;
            }
        }

        sVendido.setText(OperacaoStringUtil.formatarStringValorMoeda(val[0]));
        sLucro.setText(OperacaoStringUtil.formatarStringValorMoeda(val[1]));

        if (val[1] < 0) {
            val[1] = 0;
            sLucro.setText("0");
        }

        sGrafico.setTitle("Produtos");

        XYChart.Series vend = new XYChart.Series();
        vend.setName("Vendido");
        vend.getData().add(new XYChart.Data("Venda", val[0]));

        XYChart.Series lucr = new XYChart.Series();
        lucr.setName("Lucro");
        lucr.getData().add(new XYChart.Data("Lucro", val[1]));

        sGrafico.getData().addAll(vend, lucr);

    }

    @FXML
    void gerarPDFGeral(ActionEvent event) {
        if (JavaFXUtil.toDate(gDe) == null || JavaFXUtil.toDate(gAte) == null) {
            JavaFXUtil.showDialog("Data não preenchida",
                    "Por favor preencha a data desejada",
                    Alert.AlertType.ERROR);
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar PDF");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        String name = "Relatório_Produtos"
                + new SimpleDateFormat("dd_MM_yyyy").format(JavaFXUtil.toDate(gDe).getTime()) + "_"
                + new SimpleDateFormat("dd_MM_yyyy").format(JavaFXUtil.toDate(gAte).getTime())
                + ".pdf";

        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(gAte.getScene().getWindow());
        if (file != null) {
            try {
                f.gerarPdfRelatorioBalancoProdutos(JavaFXUtil.toDate(gDe).getTime(), JavaFXUtil.toDate(gAte).getTime(),
                        file);
                JavaFXUtil.showDialog("PDF salvo com sucesso",
                        "PDF salvo com sucesso",
                        file.getAbsolutePath());
            } catch (FuncionarioNaoAutorizadoException ex) {
                JavaFXUtil.showDialog(ex);
            }

        }
    }

    @FXML
    void gerarPlanilhaGeral(ActionEvent event) {
        if (JavaFXUtil.toDate(gDe) == null || JavaFXUtil.toDate(gAte) == null) {
            JavaFXUtil.showDialog("Data não preenchida",
                    "Por favor preencha a data desejada",
                    Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Planilha");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        String name = "Relatório_Produtos"
                + new SimpleDateFormat("dd_MM_yyyy").format(JavaFXUtil.toDate(gDe).getTime()) + "_"
                + new SimpleDateFormat("dd_MM_yyyy").format(JavaFXUtil.toDate(gAte).getTime())
                + ".xls";

        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Planilha", "*.xls")
        );

        File file = fileChooser.showSaveDialog(gAte.getScene().getWindow());
        if (file != null) {
            try {
                f.gerarPlanilhaRelatorioBalancoProdutos(JavaFXUtil.toDate(gDe).getTime(), JavaFXUtil.toDate(gAte).getTime(),
                        file);
                JavaFXUtil.showDialog("Planilha salva com sucesso",
                        "Planilha salva com sucesso",
                        file.getAbsolutePath());
            } catch (FuncionarioNaoAutorizadoException ex) {
                JavaFXUtil.showDialog(ex);
            }

        }
    }
    
    @FXML
    void  gerarPDFEstoqueGeral(){
   
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar PDF");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        String name = "Relatório_Produtos_em_Estoque"
                + new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime())
                + ".pdf";

        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(gAte.getScene().getWindow());
        if (file != null) {
            try {
                f.gerarPdfRelatorioEstoqueProdutos(gEstoqueNegativo.isSelected(),
                        file);
                JavaFXUtil.showDialog("PDF salvo com sucesso",
                        "PDF salvo com sucesso",
                        file.getAbsolutePath());
            } catch (FuncionarioNaoAutorizadoException ex) {
                JavaFXUtil.showDialog(ex);
            }

        }
    }
    
    @FXML
    void  gerarPlanilhaEstoqueGeral(){
         FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Planilha");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        String name = "Relatório_Produtos_em_Estoque"
                + new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime()) + "_"
                + ".xls";

        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Planilha", "*.xls")
        );

        File file = fileChooser.showSaveDialog(gAte.getScene().getWindow());
        if (file != null) {
            try {
                System.out.println(gEstoqueNegativo.isSelected());
                f.gerarPlanilhaRelatorioEstoqueProdutos(gEstoqueNegativo.isSelected(),
                        file);
                JavaFXUtil.showDialog("Planilha salva com sucesso",
                        "Planilha salva com sucesso",
                        file.getAbsolutePath());
            } catch (FuncionarioNaoAutorizadoException ex) {
                JavaFXUtil.showDialog("Funcionário não autorizado",
                        "Funcionário não autorizado a gerar PDFs",
                        "Por favor entre com um funcionário autorizado",
                        Alert.AlertType.ERROR);
            }

        }
    }
    
    public void configurarVerColunVenda() {
        colVerH.setComparator(new Comparator<Venda>() {
            @Override
            public int compare(Venda p1, Venda p2) {
                return Double.compare(p1.getValorNaoPago(), p2.getValorNaoPago());
            }
        });
        colVerH.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Venda, Venda>, ObservableValue<Venda>>() {
            @Override
            public ObservableValue<Venda> call(TableColumn.CellDataFeatures<Venda, Venda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colVerH.setCellFactory(new Callback<TableColumn<Venda, Venda>, TableCell<Venda, Venda>>() {
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
    
    private void configurarColunasDebito() {
        colNomeD.setComparator(new Comparator<Object[]>() {
            @Override
            public int compare(Object[] p1, Object[] p2) {
                return p1[0].toString().compareTo(p2[0].toString());
            }
        });
        colNomeD.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String, Object[]>, ObservableValue<Object[]>>() {
            @Override
            public ObservableValue<Object[]> call(TableColumn.CellDataFeatures<String, Object[]> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colNomeD.setCellFactory(col -> {
            return new TableCell<String, Object[]>() {
                @Override
                public void updateItem(Object[] price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText((String) price[0]);
                    }
                }
            };
        });
        colDebitoD.setComparator(new Comparator<Object[]>() {
            @Override
            public int compare(Object[] p1, Object[] p2) {
                return Double.compare((double) p1[1], (double) p2[1]);
            }
        });
        colDebitoD.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String, Object[]>, ObservableValue<Object[]>>() {
            @Override
            public ObservableValue<Object[]> call(TableColumn.CellDataFeatures<String, Object[]> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colDebitoD.setCellFactory(col -> {
            return new TableCell<String, Object[]>() {
                @Override
                public void updateItem(Object[] price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(OperacaoStringUtil.formatarStringValorMoedaEPonto((double) price[1]));
                    }
                }
            };
        });
    }

}
