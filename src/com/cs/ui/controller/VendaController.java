package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.exception.VendaPendenteException;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class VendaController implements Initializable {

    private Facede f;
    private Venda atual;
    private ObservableList<ItemDeVenda> observableList;

    @FXML
    private TableView<ItemDeVenda> itens;
    @FXML
    private TableColumn<ItemDeVenda, ItemDeVenda> cancelCol;
    @FXML
    private TableColumn<?, ?> qtCol;
    @FXML
    private TableColumn<?, ?> totalCol;
    @FXML
    private TableColumn<?, ?> produtoCol;
    @FXML
    private TableColumn<?, ?> valorCol;

    @FXML
    private AutoCompleteTextField codigo;
    @FXML
    private TextField quantidade;

    @FXML
    private Text nomeSupermercado;

    @FXML
    private Label descricao;
    @FXML
    private Label total;

    public VendaController() {
        f = Facede.getInstance();
    }

    @FXML
    public void pesquisaMercadorias(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_MERCADORIAS);
    }

    @FXML
    public void finalizarAvista(ActionEvent event) {
        if (f.getVendaAtual().getTotal() <= 0) {
            Dialogs.create()
                    .title("Venda zerada")
                    .masthead("Por favor adicione algum item antes de filizar a venda")
                    .showError();
        } else {
            Main.trocarDeTela(ControllerTelas.TELA_FINALIZAR_A_VISTA);
        }
    }

    @FXML
    public void finalizarAprazo(ActionEvent event) {
        if (f.getVendaAtual().getTotal() <= 0) {
            Dialogs.create()
                    .title("Venda zerada")
                    .masthead("Por favor adicione algum item antes de filizar a venda")
                    .showError();
        } else {
            Main.trocarDeTela(ControllerTelas.TELA_FINALIZAR_A_PRAZO);
        }
    }

    @FXML
    public void home(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_PRINCIPAL);
    }

    @FXML
    void enterKey(ActionEvent event) {
        String txt = codigo.getText();
        try {
            Produto p = f.buscarProdutoPorDescricaoOuCodigo(txt.replace(" R$ ", ";").split(";")[0]);
            double qt = OperacaoStringUtil.converterStringValor(quantidade.getText());
            if (qt <= 0) {
                Dialogs.create()
                        .title("Valor incorreto")
                        .masthead("Quantidade não pode ser zero")
                        .showError();
                quantidade.setText("1");
                codigo.requestFocus();
                codigo.selectAll();
                return;
            }
            ItemDeVenda it = new ItemDeVenda();
            it.setProduto(p);
            it.setQuantidade(qt);
            try {
                f.addItemAVenda(it);
                preencherInformacoes();
                quantidade.setText("1");
                codigo.setText("");
                codigo.requestFocus();
                codigo.selectAll();
            } catch (Exception e) {// venda nao existente, erro grande
                Dialogs.create().showException(e);
            }
        } catch (NoResultException | NonUniqueResultException ne) {
            codigo.requestFocus();
            codigo.selectAll();
            Dialogs.create()
                    .title("Produto inválido")
                    .masthead("Produto não cadastrado")
                    .message(txt)
                    .showError();
        }
    }

    @FXML
    void codigoDigitado(KeyEvent event) {
        if (event.getText().contains("*")) {
            double qt = 0;
            try {
                qt = OperacaoStringUtil.converterStringValor(codigo.getText().replace("*", ""));
                if (qt <= 0) {
                    throw new NumberFormatException();
                }
                quantidade.setText(OperacaoStringUtil.formatarStringQuantidade(qt));
                codigo.setText("");
                codigo.requestFocus();
                codigo.selectAll();
            } catch (NumberFormatException ne) {
                Dialogs.create()
                        .title("Valor incorreto")
                        .masthead("Quantidade inválida")
                        .showError();
                codigo.setText(codigo.getText().replace("*", ""));
                quantidade.setText("1");
                codigo.requestFocus();
                codigo.selectAll();
            }
        }
    }

    @FXML
    void enterkeyQuantidade(ActionEvent event) {

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

        JavaFXUtil.colunValueModedaFormat(valorCol);
        JavaFXUtil.colunValueModedaFormat(totalCol);
        JavaFXUtil.colunValueQuantidadeFormat(qtCol);

        MaskFieldUtil.quantityField(quantidade);
        quantidade.setAlignment(Pos.CENTER_LEFT);
        JavaFXUtil.nextFielOnAction(quantidade, codigo);

        verificarVendasPendentes();
        preencherInformacoes();
        List<String> list = f.buscarDescricaoEPrecoProdutos();
        
        MaskFieldUtil.upperCase(codigo);
        codigo.setList(list);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                codigo.requestFocus();
                codigo.selectAll();
            }
        });
    }
    
    public void adicionarTeclaDeAtalho(KeyCode tecla, String tela, Scene scene){
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.getCode() == tecla) {
                Main.trocarDeTela(tela);
            }
        });
    }

    public void preencherInformacoes() {
        atual = f.getVendaAtual();
        observableList = FXCollections.observableList(getItensDaVenda());
        itens.setItems(observableList);
        configurarColunaEditar();
        ItemDeVenda ult = getUltimoItemVendido();
        if (ult != null) {
            descricao.setText(descricaoItem(ult));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal()));
        }
        quantidade.setText("1");
        codigo.requestFocus();
        codigo.selectAll();
    }

    public void verificarVendasPendentes() {
        List<Venda> pendentes = f.buscarVendaNaoFinalizadasPorFuncionario(f.getFuncionarioLogado());
        if (pendentes.size() == 1) {
            try {
                // tornar ela atual
                f.recuperarVendaPendenteDoFuncionarioLogado();
                f.refreshValorDeVendaAtual();
            } catch (Exception ex) {
                ex.printStackTrace();
                Dialogs.create().showException(ex);
            }
        } else if (pendentes.size() > 1) {// varias vendas pendentes, mostrar erro
            Dialogs.create()
                    .title("Várias vendas pendentes")
                    .masthead("Várias vendas inicializadas para o mesmo funcionário")
                    .showError();
        } else {
            try {
                f.inicializarVenda();
            } catch (VendaPendenteException ex) {
                Dialogs.create().showException(ex);
            }
        }
        if (f.getVendaAtual() == null) {// verifica se iniciou venda atual
            f.setAtualComVendaPendenteTemporariamente();// vai deixar para
        }
    }

    public List<ItemDeVenda> getItensDaVenda() {
        List<ItemDeVenda> it = f.buscarItensDaVendaPorIdDaVenda(f.getVendaAtual().getId());
        Collections.sort(it);
        return it;
    }

    public ItemDeVenda getUltimoItemVendido() {
        try {
            return getItensDaVenda().get(0);
        } catch (IndexOutOfBoundsException i) {
            return null;
        }
    }

    public String descricaoItem(ItemDeVenda it) {
        return it.getDescricaoProduto() + "\n"
                + OperacaoStringUtil.formatarStringQuantidadeInteger(it.getQuantidade()) + " X "
                + OperacaoStringUtil.formatarStringValorMoeda(it.getValorProduto()) + " = "
                + OperacaoStringUtil.formatarStringValorMoeda(it.getTotal());
    }

    public void removerItem(ItemDeVenda it) {
        Dialogs dialog = Dialogs.create()
                .title("Remover Item")
                .masthead("Deseja realmente remover o item?")
                .message(descricaoItem(it))
                .actions(Dialog.Actions.YES, Dialog.Actions.CANCEL);
        dialog.style(DialogStyle.UNDECORATED);
        Action act = dialog.showError();
        if (act.equals(Dialog.Actions.YES)) {
            try {
                f.removerItemDaVenda(it);
                preencherInformacoes();
            } catch (Exception ex) {
                Dialogs.create()
                        .title("Erro ao remover item")
                        .showException(ex);
            }
        }
    }

    @FXML
    protected void configurarColunaEditar() {
        cancelCol.setComparator(new Comparator<ItemDeVenda>() {
            @Override
            public int compare(ItemDeVenda p1, ItemDeVenda p2) {
                return p1.toString().compareToIgnoreCase(p2.toString());
            }
        });
        cancelCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda>, ObservableValue<ItemDeVenda>>() {
            @Override
            public ObservableValue<ItemDeVenda> call(TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        cancelCol.setCellFactory(new Callback<TableColumn<ItemDeVenda, ItemDeVenda>, TableCell<ItemDeVenda, ItemDeVenda>>() {
            @Override
            public TableCell<ItemDeVenda, ItemDeVenda> call(TableColumn<ItemDeVenda, ItemDeVenda> btnCol) {
                return new TableCell<ItemDeVenda, ItemDeVenda>() {
                    @Override
                    public void updateItem(final ItemDeVenda obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final Button button = new Button(null, new ImageView(new Image("com/cs/ui/img/bt_cancel.png")));
                        button.setStyle("-fx-background-color: none;");
                        button.setCursor(Cursor.HAND);
                        button.setMinSize(10, 10);

                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                removerItem(obj);
                            }
                        });
                    }
                };
            }
        });
    }

}
