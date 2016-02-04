/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.util;

import com.cs.sis.model.pessoas.Pessoa;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

/**
 *
 * @author Lettiery
 */
public class JavaFXUtil {

    public static void colunValueModedaFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Double, Number>() {
                    @Override
                    public void updateItem(Number price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatarStringValorMoeda(price.doubleValue()));
                        }
                    }
                });
    }

    public static void colunValueQuantidadeFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Double, Number>() {
                    @Override
                    public void updateItem(Number price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatarStringQuantidade(price.doubleValue()));
                        }
                    }
                });
    }
    
    public static void colunDataTimeFormat(TableColumn colunaDouble) {
        colunaDouble.setCellFactory(col
                -> new TableCell<Calendar, Calendar>() {
                    @Override
                    public void updateItem(Calendar price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(OperacaoStringUtil.formatDataTimeValor(price));
                        }
                    }
                });
    }
    
    public static void nextFielOnAction(TextField action, Node foccus){
        action.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                foccus.requestFocus();
                if(foccus instanceof TextField){
                    ((TextField) foccus).selectAll();
                }
            }
        });
    }
    
    
    public static Calendar toDate(DatePicker datePicker) {
        if(datePicker.getValue() == null){
            return null;
        }
        LocalDate ld = datePicker.getValue();
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(Date.from(instant));
        return gc;
    }

    /**
     * Converte Date para LocalDate
     *
     * @param d
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Calendar d) {
        Instant instant = Instant.ofEpochMilli(d.getTime().getTime());
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static void beginFoccusTextField(TextField text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
                text.selectAll();
            }
        });
    }

    public static void colunPessoaFormat(TableColumn colunaPessoa) {
        colunaPessoa.setCellFactory(col
                -> new TableCell<Pessoa, Pessoa>() {
                    @Override
                    public void updateItem(Pessoa price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(price.getNome());
                        }
                    }
                });
    }

}
