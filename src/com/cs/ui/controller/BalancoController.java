package com.cs.ui.controller;

import com.cs.Facede;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.io.File;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.Dialogs;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hNome.setList(f.buscarNomeClientePorNomeQueInicia(""));

        colDataH.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colTotalH.setCellValueFactory(new PropertyValueFactory<>("total"));
        colPagaH.setCellValueFactory(new PropertyValueFactory<>("partePaga"));

        MaskFieldUtil.upperCase(hNome);
        JavaFXUtil.nextFielOnAction(hNome, hDe);
    }

    @FXML
    void gerarHistorico(ActionEvent event) {
        Cliente c;

        try {
            c = f.buscarClientePorNome(hNome.getText());
        } catch (Exception e) {
            Dialogs.create().title("Cliente não cadastrado")
                    .masthead("Cliente " + hNome.getText() + " não encontrado")
                    .showError();
            return;
        }

        List<Pagavel> pagaveis = new ArrayList<>();
        try {
            pagaveis = f.buscarPagaveisDoCliente(c,
                    JavaFXUtil.toDate(hDe).getTime(),
                    JavaFXUtil.toDate(hAte).getTime());
        } catch (Exception e) {
            if (JavaFXUtil.toDate(hDe) == null || JavaFXUtil.toDate(hAte) == null) {
                Dialogs.create().title("Data não preenchida")
                        .masthead("Por favor preencha a data desejada")
                        .showError();
                return;
            }
        }

        JavaFXUtil.colunValueMoedaFormat(colPagaH);
        JavaFXUtil.colunValueMoedaFormat(colTotalH);
        JavaFXUtil.colunDataTimeFormat(colDataH);

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
                Dialogs.create().title("Data não preenchida")
                        .masthead("Por favor preencha a data desejada")
                        .showError();
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
                Dialogs.create().title("Data não preenchida")
                        .masthead("Por favor preencha a data desejada")
                        .showError();
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
                Dialogs.create().title("Data não preenchida")
                        .masthead("Por favor preencha a data desejada")
                        .showError();
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
            Dialogs.create().title("Data não preenchida")
                    .masthead("Por favor preencha a data desejada")
                    .showError();
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
                Dialogs.create().title("PDF salvo com sucesso")
                        .masthead("PDF salvo com sucesso")
                        .message(file.getAbsolutePath())
                        .showInformation();
            } catch (FuncionarioNaoAutorizadoException ex) {
                Dialogs.create()
                        .title("Funcionário não autorizado")
                        .masthead("Funcionário não autorizado a gerar PDFs")
                        .message("Por favor entre com um funcionário autorizado")
                        .showError();
            }

        }
    }

    @FXML
    void gerarPlanilhaGeral(ActionEvent event) {
        if (JavaFXUtil.toDate(gDe) == null || JavaFXUtil.toDate(gAte) == null) {
            Dialogs.create().title("Data não preenchida")
                    .masthead("Por favor preencha a data desejada")
                    .showError();
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
                Dialogs.create().title("Planilha salva com sucesso")
                        .masthead("Planilha salva com sucesso")
                        .message(file.getAbsolutePath())
                        .showInformation();
            } catch (FuncionarioNaoAutorizadoException ex) {
                Dialogs.create()
                        .title("Funcionário não autorizado")
                        .masthead("Funcionário não autorizado a gerar PDFs")
                        .message("Por favor entre com um funcionário autorizado")
                        .showError();
            }

        }
    }

}
