/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.cs.Facede;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import np.com.ngopal.control.AutoFillTextBox;

/**
 * FXML Controller class
 *
 * @author msi
 */
public class FXMLController implements Initializable {
    ObservableList<String> data = FXCollections.observableArrayList(Facede.getInstance().buscarNomeClientePorNomeQueInicia(""));
    
    
    @FXML
    private AutoCompleteTextField autoFill;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        autoFill.setList(Facede.getInstance().buscarNomeClientePorNomeQueInicia(""));
        System.err.println(Facede.getInstance().buscarNomeClientePorNomeQueInicia("").size());
        
    }    
}
