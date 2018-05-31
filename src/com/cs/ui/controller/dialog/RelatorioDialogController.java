package com.cs.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.AutoCompleteTextField;
import com.cs.sis.util.JavaFXUtil;
import com.cs.sis.util.MaskFieldUtil;
import com.cs.sis.util.OperacaoStringUtil;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.persistence.NoResultException;


/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class RelatorioDialogController extends DialogController<Produto> {
    private final String ARQUIVO_PRODUTOS = "produtos_remocao.txt";
    @FXML
    private TableView<Produto> itens;
    
    private List<String> produtos;

    @FXML
    private TableColumn codigoCol;
    @FXML
    private TableColumn removeCol;
    @FXML
    private TableColumn produtoCol;

    @FXML
    private AutoCompleteTextField nomeComplet;
    @FXML
    private TextField valorFinal;
    @FXML
    private DatePicker dataInicial;
    @FXML
    private DatePicker dataFinal;
    @FXML
    private CheckBox limitarLista;

     public RelatorioDialogController() {
        super();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.produtoCol.setCellValueFactory(new PropertyValueFactory<Cliente, String>("descricao"));
        this.codigoCol.setCellValueFactory(new PropertyValueFactory<Cliente, Double>("codigoDeBarras"));
        configurarColunaRemove();
        
        nomeComplet.setList(f.buscarDescricaoProdutos());
  
        MaskFieldUtil.upperCase(nomeComplet);
        MaskFieldUtil.upperCase(this.nomeComplet);
        MaskFieldUtil.monetaryField(this.valorFinal);
        
        MaskFieldUtil.dateField(dataInicial.getEditor());
        MaskFieldUtil.dateField(dataFinal.getEditor());
       
        JavaFXUtil.nextFielOnAction(valorFinal, dataInicial);
        JavaFXUtil.nextFielOnAction(dataInicial.getEditor(), dataFinal);
        JavaFXUtil.nextFielOnAction(dataFinal.getEditor(), okButton);
        
        dataInicial.getEditor().setText("01/01/"+(Calendar.getInstance().get(Calendar.YEAR)-1));
        dataFinal.getEditor().setText("31/12/"+(Calendar.getInstance().get(Calendar.YEAR)-1));
        
        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });
        
        limitarLista.setTooltip(new Tooltip("Limitar lista de produtos ao atingir Valor Final"));
        okButton.setTooltip(new Tooltip("Criar PDF com essas configurações"));
        nomeComplet.setTooltip(new Tooltip("Nome ou Código do produto que deseja remover do relatório"));
        valorFinal.setTooltip(new Tooltip("Total final do relatório"));
        
        try{
            produtos = (List <String>) f.recuperarTemporario(ARQUIVO_PRODUTOS);
            produtos.stream().forEach((codigo)->{
                try{itens.getItems().add(f.buscarProdutoPorCodigo(codigo));}catch(Exception e){}
                        });
        }catch(Exception | Error e){
            produtos = new ArrayList<String>();
        }
           
    }
    
    @FXML
    public void enterKeyNome() {
        String txt = nomeComplet.getText();
        if (txt.isEmpty()) {return;}
        try{
            Produto p = f.buscarProdutoPorDescricaoOuCodigo(txt);
            this.itens.getItems().add(p);
            nomeComplet.clear();
            nomeComplet.removeItem(p.getDescricao());
            produtos.add(p.getCodigoDeBarras());
            f.salvarTemporarios(ARQUIVO_PRODUTOS, produtos);
        }catch(NoResultException ne){}
    }
    
    @Override
    @FXML
    protected void okClicado() {
        List<String> codigos = new ArrayList();
        for(Produto p: this.itens.getItems().subList(0, itens.getItems().size())){
            codigos.add(p.getCodigoDeBarras());
        }
        
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

        File file = fileChooser.showSaveDialog(this.itens.getScene().getWindow());
        if (file != null) {
            try {
                f.gerarPdfRelatorioEstoqueProdutos(false, limitarLista.isSelected(), JavaFXUtil.toDate(dataInicial),
                        JavaFXUtil.toDate(dataFinal), codigos, OperacaoStringUtil.converterStringValor(this.valorFinal.getText()), file);
                JavaFXUtil.showDialog("PDF salvo com sucesso",
                        "PDF salvo com sucesso",
                        file.getAbsolutePath());
            } catch (FuncionarioNaoAutorizadoException ex) {
                JavaFXUtil.showDialog(ex);
            }

        }
        dialogStage.close();
    }
    
    
    protected void configurarColunaRemove() {
        removeCol.setComparator((Object p1, Object p2)->{return p1.toString().compareToIgnoreCase(p2.toString());});
        removeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Produto, Produto>, ObservableValue>() {
            @Override
            public ObservableValue<Produto> call(TableColumn.CellDataFeatures<Produto, Produto> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
   
        removeCol.setCellFactory(new Callback<TableColumn<Produto, Produto>, TableCell<Produto, Produto>>() {
            @Override
            public TableCell<Produto, Produto> call(TableColumn<Produto, Produto> btnCol) {
                return new TableCell<Produto, Produto>() {
                    @Override
                    public void updateItem(final Produto obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        
                        final Button button = new Button(null, new ImageView(new Image("com/cs/ui/img/bt_cancel.png")));
                        button.setStyle("-fx-background-color: none;");
                        button.setCursor(Cursor.HAND);
                        button.setMinSize(10, 10);

                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction((event)->{itens.getItems().remove(obj);
                        itens.setItems(itens.getItems());
                        produtos.remove(obj.getCodigoDeBarras());
                        f.salvarTemporarios(ARQUIVO_PRODUTOS, produtos);
                        });
                    }
                };
            }
        });
    }
    
    
    @Override
    public String getTitulo() {
        return "Configurações do Relatório";
    }

    @Override
    public boolean isEntradaValida() {
        return true;
    }

    @Override
    public void setEntity(Produto entity) {
    }

    

}
