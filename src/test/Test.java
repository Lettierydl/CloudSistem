/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.cs.Facede;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import np.com.ngopal.control.AutoFillTextBox;

/**
 *
 * @author WEBNEP
 */
public class Test extends Application {

    ObservableList<String> data = FXCollections.observableArrayList(Facede.getInstance().buscarNomeClientePorNomeQueInicia(""));

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AutoFillTextBox without FilterMode");

        //Layout
        HBox hbox = new HBox();

        hbox.setSpacing(10);

        //CustomControl
        final AutoFillTextBox box = new AutoFillTextBox(data);
        box.setListLimit(10);
        box.setFilterMode(true);
        
        //Label
        Label l = new Label("AutoFillTextBox:");
        l.translateYProperty().set(5);
        l.translateXProperty().set(5);
        hbox.getChildren().addAll(l,box);
        

        Scene scene = new Scene(hbox, 300, 200);
        primaryStage.setScene(scene);

        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
