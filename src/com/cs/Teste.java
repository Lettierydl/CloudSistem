/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import np.com.ngopal.control.AutoFillTextBox;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author Lettiery
 */
public class Teste extends Application {

    public Teste() {
    }

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        Application.launch(args);
    }*/

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AutoFillTextBox without FilterMode");

        //SAMPLE DATA
        ObservableList data = FXCollections.observableArrayList(Facede.getInstance().buscarNomeClientePorNomeQueInicia(""));

        //Layout    
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        //CustomControl
        final AutoFillTextBox box = new AutoFillTextBox(data);
        //Label
        Label l = new Label("AutoFillTextBox: ");
        l.translateYProperty().set(5);
        l.translateXProperty().set(5);

        hbox.getChildren().addAll(l, box);
        Scene scene = new Scene(hbox, 300, 200);

        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("control.css").toExternalForm());
        primaryStage.show();

    }
}
