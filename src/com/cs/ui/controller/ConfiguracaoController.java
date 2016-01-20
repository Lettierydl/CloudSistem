package com.cs.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.RotateTransition;
import javafx.animation.RotateTransitionBuilder;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class ConfiguracaoController implements Initializable {
    
    @FXML
    private Rectangle rect;
    
    @FXML
    private RotateTransition rotateTransition;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rect.setArcHeight(20);
        rect.setArcWidth(20);
        rect.setFill(Color.ORANGE);
        

        rotateTransition = RotateTransitionBuilder.create()
                .node(rect)
                .duration(Duration.seconds(4))
                .fromAngle(0)
                .toAngle(720)
                .cycleCount(Timeline.INDEFINITE)
                .autoReverse(true)
                .build();
        
        rotateTransition.play();
    }    
    
}
