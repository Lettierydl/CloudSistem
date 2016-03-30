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
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class MercadoriasController implements Initializable {

    private Facede f;
    private Venda atual;
    private static ObservableList<ItemDeVenda> mercadorias;
    private static int index = 0;
    
    
    @FXML
    private TableView<ItemDeVenda> itens;
    
    @FXML
    private TableColumn<ItemDeVenda, ItemDeVenda> saidaCol;
    @FXML
    private TableColumn<?, ?> valCol;
    @FXML
    private TableColumn<?, ?> produtoCol;
    @FXML
    private TableColumn<ItemDeVenda, ItemDeVenda> estoqueCol;
    @FXML
    private TableColumn<ItemDeVenda, ItemDeVenda> codigoCol;

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

    public MercadoriasController() {
        f = Facede.getInstance();
        if(mercadorias == null){
            mercadorias =  FXCollections.observableArrayList();
        }
    }
    
    @FXML
    void limparColsulta(ActionEvent event) {
        mercadorias.clear();
        preencherInformacoes();
    }

    @FXML
    void imprimir(ActionEvent event) {
        Venda v = new Venda();
        for(ItemDeVenda t: mercadorias){
            v.addItemDeVenda(t);
        }
        v.setDia(Calendar.getInstance());
        v.setFuncionario(f.getFuncionarioLogado());
        v.setObservacao("Consulta de mercadorias");
        f.imprimirVenda(v);
    }
    
    @FXML
    public void voltarVendas(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_VENDA);
    }
    @FXML
    public void home(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_PRINCIPAL);
    }
    @FXML
    void enterKey(ActionEvent event) {
        String txt = codigo.getText();
        if(txt.isEmpty()){
            return;
        }
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
            it.setIndex(index++);
            try {
                mercadorias.add(0, it);
                preencherInformacoes();
                quantidade.setText("1");
                codigo.setText("");
                codigo.requestFocus();
                codigo.selectAll();
            } catch (Exception e) {
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
        //inserir autoComplet
    }

    @FXML
    void enterkeyQuantidade(ActionEvent event) {

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        valCol.setCellValueFactory(new PropertyValueFactory<>("valorProduto"));
        produtoCol.setCellValueFactory(new PropertyValueFactory<>("DescricaoProduto"));

        JavaFXUtil.colunValueQuantidadeFormat(saidaCol);
        JavaFXUtil.colunValueMoedaFormat(valCol);
        JavaFXUtil.colunValueQuantidadeFormat(estoqueCol);

        MaskFieldUtil.quantityField(quantidade);
        quantidade.setAlignment(Pos.CENTER_LEFT);
        MaskFieldUtil.upperCase(codigo);
        JavaFXUtil.nextFielOnAction(quantidade, codigo);
        
        preencherInformacoes();
        itens.setItems(mercadorias);
        
        codigo.setList(f.buscarDescricaoEPrecoProdutos());
        JavaFXUtil.beginFoccusTextField(codigo);
        
        codigo.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    enterKey(null);
                }
            }
        });
    }

    public void preencherInformacoes() {
        double val = 0;
        for(ItemDeVenda it : this.mercadorias){
            val += it.getTotal();
        }
        ItemDeVenda ult = getUltimoItemVendido();
        if (ult != null) {
            descricao.setText(descricaoItem(ult));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(val));
        }else{
            descricao.setText("X = ");
            total.setText("0");
        }
        quantidade.setText("1");
        codigo.requestFocus();
        codigo.selectAll();
        configurarColunas();
    }

    public ItemDeVenda getUltimoItemVendido() {
        try {
            return mercadorias.get(0);
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
    protected void configurarColunas() {
        saidaCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda>, ObservableValue<ItemDeVenda>>() {
            @Override
            public ObservableValue<ItemDeVenda> call(TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        saidaCol.setCellFactory(new Callback<TableColumn<ItemDeVenda, ItemDeVenda>, TableCell<ItemDeVenda, ItemDeVenda>>() {
            @Override
            public TableCell<ItemDeVenda, ItemDeVenda> call(TableColumn<ItemDeVenda, ItemDeVenda> btnCol) {
                return new TableCell<ItemDeVenda, ItemDeVenda>() {
                    @Override
                    public void updateItem(final ItemDeVenda obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        Label l = new Label(OperacaoStringUtil.formatarStringQuantidadeInteger(f.getRelatorioDeProduto30Dias(obj.getProduto().getId())));
                        l.setTooltip(new Tooltip("Quantidade vendida nos últimos 30 dias"));
                        super.updateItem(obj, empty);
                        setGraphic(l);
                    }
                };
            }
        });
        
        codigoCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda>, ObservableValue<ItemDeVenda>>() {
            @Override
            public ObservableValue<ItemDeVenda> call(TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        codigoCol.setCellFactory(new Callback<TableColumn<ItemDeVenda, ItemDeVenda>, TableCell<ItemDeVenda, ItemDeVenda>>() {
            @Override
            public TableCell<ItemDeVenda, ItemDeVenda> call(TableColumn<ItemDeVenda, ItemDeVenda> btnCol) {
                return new TableCell<ItemDeVenda, ItemDeVenda>() {
                    @Override
                    public void updateItem(final ItemDeVenda obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        Label l = new Label(obj.getProduto().getCodigoDeBarras());
                        super.updateItem(obj, empty);
                        setGraphic(l);
                    }
                };
            }
        });
        
        estoqueCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda>, ObservableValue<ItemDeVenda>>() {
            @Override
            public ObservableValue<ItemDeVenda> call(TableColumn.CellDataFeatures<ItemDeVenda, ItemDeVenda> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        estoqueCol.setCellFactory(new Callback<TableColumn<ItemDeVenda, ItemDeVenda>, TableCell<ItemDeVenda, ItemDeVenda>>() {
            @Override
            public TableCell<ItemDeVenda, ItemDeVenda> call(TableColumn<ItemDeVenda, ItemDeVenda> btnCol) {
                return new TableCell<ItemDeVenda, ItemDeVenda>() {
                    @Override
                    public void updateItem(final ItemDeVenda obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        Label l = new Label(OperacaoStringUtil.formatarStringQuantidadeInteger(obj.getProduto().getQuantidadeEmEstoque()));
                        super.updateItem(obj, empty);
                        setGraphic(l);
                    }
                };
            }
        });
    }

}
