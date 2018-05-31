package com.cs.ui.controller;

import com.cs.ControllerTelas;
import com.cs.Facede;
import com.cs.Main;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.exception.VendaPendenteException;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Timeline;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private AutoCompleteTextField nomeComplet;
    
    @FXML
    private TextField codigo;
    
    @FXML
    private TextField quantidade;

    
    @FXML
    private Label descricao;
    @FXML
    private Label total;
    
    @FXML
    private Text razaoSocial;

    static Timeline delayTimeline;

    public VendaController() {
        f = Facede.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        valorCol.setCellValueFactory(new PropertyValueFactory<>("valorProduto"));
        qtCol.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        produtoCol.setCellValueFactory(new PropertyValueFactory<>("DescricaoProduto"));

        JavaFXUtil.colunValueMoedaFormat(valorCol);
        JavaFXUtil.colunValueMoedaFormat(totalCol);
        JavaFXUtil.colunValueQuantidadeFormat(qtCol);

        MaskFieldUtil.quantityField(quantidade);
        quantidade.setAlignment(Pos.CENTER_LEFT);
        JavaFXUtil.nextFielOnAction(quantidade, codigo);

        verificarVendasPendentes();
        preencherInformacoes();
        List<String> list = f.buscarDescricaoEPrecoProdutos();

        MaskFieldUtil.upperCase(codigo);
        
        nomeComplet.setList(list);
        nomeComplet.setVisible(false);
        nomeComplet.setDisable(true);
        MaskFieldUtil.upperCase(nomeComplet);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                codigo.requestFocus();
                codigo.selectAll();
            }
        });
        codigo.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    enterKey(null);
                }
            }
        });
        razaoSocial.setText(VariaveisDeConfiguracaoUtil.getNomeEstabelecimento());

    }

    @FXML
    public void pesquisaMercadorias(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_MERCADORIAS);
    }

    @FXML
    public void finalizarAvista(ActionEvent event) {
        if (f.getVendaAtual().getTotal() <= 0) {
            JavaFXUtil.showDialog(
                    "Venda zerada"
                    ,"Por favor adicione algum item antes de filizar a venda",
                    Alert.AlertType.ERROR);
        } else {
            Main.trocarDeTela(ControllerTelas.TELA_FINALIZAR_A_VISTA);
        }
    }

    @FXML
    public void finalizarAprazo(ActionEvent event) {
        if (f.getVendaAtual().getTotal() <= 0) {
            JavaFXUtil.showDialog(
                    "Venda zerada"
                    ,"Por favor adicione algum item antes de filizar a venda",
                    Alert.AlertType.ERROR);
        } else {
            Main.trocarDeTela(ControllerTelas.TELA_FINALIZAR_A_PRAZO);
        }
    }

    @FXML
    public void home(ActionEvent event) {
        Main.trocarDeTela(ControllerTelas.TELA_PRINCIPAL);
    }

    @FXML
    void enterKey(ActionEvent ev) {
        String txt = codigo.getText();
        if (txt.isEmpty()) {
            return;
        }
        
        try {
            Produto p = f.buscarProdutoPorCodigo(txt);
            
            double qt = OperacaoStringUtil.converterStringValor(quantidade.getText());
            inserirItemDeVenda(p, qt);
            nomeComplet.setDisable(true);
            nomeComplet.setVisible(false);
            return;
        } catch (NoResultException | NonUniqueResultException ne) {
            if (txt.startsWith("2") && txt.length() == 13) {
                //2023000 162,69 9
                String vTotal = txt.substring(7, 10) + "."
                        + txt.substring(10, 12);
                String nCodigo = txt.substring(0, 7);
                double t = 0;
                
                try{
                    t = Double.valueOf(vTotal);
                }catch(NumberFormatException nee){return;}

                try{
                    //produto de banca
                    Produto p = f.buscarProdutoPorCodigo(nCodigo);
                    quantidade.setText(OperacaoStringUtil.formatarStringQuantidade(t / p.getValorDeVenda()));
                    codigo.setText(nCodigo);
                    inserirItemDeVenda(p, t / p.getValorDeVenda());
                    return;
                }catch(NoResultException | NonUniqueResultException ne2) {
                }
            }

            codigo.requestFocus();
            codigo.selectAll();
            JavaFXUtil.showDialog(
                    "Produto inválido"
                    ,"Produto não cadastrado"
                    ,txt,
                    Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    void enterKeyNome(ActionEvent ev) {
        String txt = nomeComplet.getText();
        if (txt.isEmpty()) {
            return;
        }
        
        try {
            Produto p = f.buscarProdutoPorDescricao(txt.replace(" R$ ", ";").split(";")[0]);
            double qt = OperacaoStringUtil.converterStringValor(quantidade.getText());
            inserirItemDeVenda(p, qt);
            codigo.requestFocus();
            codigo.selectAll();
            nomeComplet.setText("");
            nomeComplet.setVisible(false);
            nomeComplet.setDisable(true);
            return;
        } catch (NoResultException | NonUniqueResultException ne) {
            nomeComplet.requestFocus();
            nomeComplet.selectAll();
            JavaFXUtil.showDialog(
                    "Produto inválido"
                    ,"Produto não cadastrado"
                    ,txt,
                    Alert.AlertType.ERROR);
        }
    }
    
    
     private void inserirItemDeVenda(Produto p, double qt) {
            if (qt <= 0) {
                JavaFXUtil.showDialog(
                        "Valor incorreto"
                        ,"Quantidade não pode ser zero",
                        Alert.AlertType.ERROR);
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
                
                atual = f.getVendaAtual();
                observableList.add(0, it);
                configurarColunaEditar();
                descricao.setText(descricaoItem(it));
                total.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal()));
                
                quantidade.setText("1");
                codigo.setText("");
                codigo.requestFocus();
                codigo.selectAll();
            } catch (Exception e) {// venda nao existente, erro grande
                JavaFXUtil.showDialog(e);
            }
    }
    

    // tentar com todos os eventos em vez do relanssed coloca o pressed e o tiped
    @FXML
    void codigoDigitado(KeyEvent event) {
        if (codigo.getText().contains("*")) {
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
                JavaFXUtil.showDialog(
                        "Valor incorreto"
                        ,"Quantidade inválida",
                        Alert.AlertType.ERROR);
                codigo.setText(codigo.getText().replace("*", ""));
                quantidade.setText("1");
                codigo.requestFocus();
                codigo.selectAll();
            }
        } else {
            try {
                Integer.valueOf(codigo.getText());
                //se so tiver numero, pode ser um codigo
                //agora busca os produtos que iniciam com esse codigo
                //se tiver mais de um, nao foi enter
                //se tiver so um, e o codigo for identico ao digitado, foi digitado enter
            } catch (NumberFormatException ne) {
                if(codigo.getText() != null && !codigo.getText().isEmpty() && !codigo.getText().replace(",", "").replace(".", "").replaceAll("\\d", "").isEmpty()){
                    nomeComplet.setVisible(true);
                    nomeComplet.setDisable(false);
                    nomeComplet.setText(codigo.getText().trim());

                    codigo.setText("");
                    nomeComplet.requestFocus();
                    nomeComplet.positionCaret(nomeComplet.getText().length());
                }
            }
        }
    }

    public void adicionarTeclaDeAtalho(KeyCode tecla, String tela, Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.getCode() == tecla) {
                Main.trocarDeTela(tela);
            }
        });
    }

    public void preencherInformacoes() {
        atual = f.getVendaAtual();
        //observableList = FXCollections.observableList(getItensDaVenda());
        observableList = FXCollections.observableList(atual.getItensDeVenda());
        Collections.reverse(observableList);
        itens.setItems(observableList);
        configurarColunaEditar();
        ItemDeVenda ult = getUltimoItemVendido();
        if (ult != null) {
            descricao.setText(descricaoItem(ult));
            total.setText(OperacaoStringUtil.formatarStringValorMoeda(atual.getTotal()));
        }else{
            descricao.setText("X = ");
            total.setText("0");
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
                JavaFXUtil.showDialog(ex);
            }
        } else if (pendentes.size() > 1) {// varias vendas pendentes, mostrar erro
            JavaFXUtil.showDialog(
                    "Várias vendas pendentes"
                    ,"Várias vendas inicializadas para o mesmo funcionário",
                    Alert.AlertType.ERROR);
        } else {
            try {
                f.inicializarVenda();
            } catch (VendaPendenteException ex) {
                JavaFXUtil.showDialog(ex);
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
                + OperacaoStringUtil.formatarStringQuantidade(it.getQuantidade()) + " X "
                + OperacaoStringUtil.formatarStringValorMoeda(it.getValorProduto()) + " = "
                + OperacaoStringUtil.formatarStringValorMoeda(it.getTotal());
    }

    public void removerItem(ItemDeVenda it) {
        ButtonType act = JavaFXUtil.showDialogOptions(
                "Remover Item"
                ,"Deseja realmente remover o item?"
                ,descricaoItem(it));
        if (act.equals(ButtonType.YES)) {
            try {
                f.removerItemDaVenda(it);
                preencherInformacoes();
            } catch (Exception ex) {
                JavaFXUtil.showDialog(
                        "Erro ao remover item",ex);
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
