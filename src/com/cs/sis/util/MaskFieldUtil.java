/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Lettiery
 */
public abstract class MaskFieldUtil {

    private static List<KeyCode> ignoreKeyCodes = new ArrayList<>();

    public static void ignoreKeys(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler) new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent keyEvent) {
                if (ignoreKeyCodes.contains(keyEvent.getCode())) {
                    keyEvent.consume();
                }
            }
        });
    }

    //xxxxx-xxxxx-xxxxx-xxxxx
    public static void serialTextField(final TextField textField) {
        MaskFieldUtil.maxField(textField, 23);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                MaskFieldUtil.positionCaret(textField);
                String value = textField.getText();
                value = value.replaceAll("[^\\w]", "");
                value = value.replaceFirst("(\\w{5})(\\w)", "$1-$2");
                value = value.replaceFirst("(\\w{5})\\-(\\w{5})(\\w)", "$1-$2-$3");
                value = value.replaceFirst("(\\w{5})\\-(\\w{5})\\-(\\w{5})(\\w)", "$1-$2-$3-$4");
                textField.setText(value);
            }
        });
    }

    public static void dateField(final TextField textField) {
        MaskFieldUtil.maxField(textField, 10);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                MaskFieldUtil.positionCaret(textField);
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
                value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
                textField.setText(value);
            }
        });
    }

    public static void numericField(final TextField textField) {
        textField.lengthProperty().addListener((ChangeListener) new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                char ch;
                if (newValue.intValue() > oldValue.intValue() && ((ch = textField.getText().charAt(oldValue.intValue())) < '0' || ch > '9')) {
                    textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                }
            }
        });
    }

    public static void monetaryField(final TextField textField) {
        textField.setAlignment(Pos.CENTER_RIGHT);
        MaskFieldUtil.maxField(textField, 17);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                MaskFieldUtil.positionCaret(textField);
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");
                textField.setText(value);
            }
        });
    }
    
    public static void quantityField(final TextField textField) {
        textField.setAlignment(Pos.CENTER_RIGHT);
        MaskFieldUtil.maxField(textField, 17);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                MaskFieldUtil.positionCaret(textField);
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceAll("([0-9]{1})([0-9]{3})$", "$1,$2");
                textField.setText(value);
            }
        });
    }

    public static BigDecimal monetaryValueFromField(TextField textField) {
        if (textField.getText().isEmpty()) {
            return null;
        }
        BigDecimal retorno = BigDecimal.ZERO;
        NumberFormat nf = NumberFormat.getNumberInstance();
        try {
            Number parsedNumber = nf.parse(textField.getText());
            retorno = new BigDecimal(parsedNumber.toString());
        } catch (ParseException ex) {
            Logger.getLogger(MaskFieldUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public static void cpfCnpjField(TextField textField) {
        MaskFieldUtil.maxField(textField, 18);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                String value = textField.getText();
                if (value.length() <= 14) {
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                    value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                    value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
                } else {
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{2})(\\d)", "$1.$2");
                    value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                    value = value.replaceFirst("(\\d{3})(\\d)", "$1/$2");
                    value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                }
                textField.setText(value);
                MaskFieldUtil.positionCaret(textField);
            }
        });

        textField.lengthProperty().addListener((observableValue, number, number2) -> {
            String value = textField.getText();
            if (number2.intValue() <= 14) {
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
            } else {
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{2})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1/$2");
                value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
            }
            textField.setText(value);
            MaskFieldUtil.positionCaret(textField);
        }
        );
    }

    public static void cepField(TextField textField) {
        MaskFieldUtil.maxField(textField, 9);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                MaskFieldUtil.positionCaret(textField);
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                textField.setText(value);
            }
        });
    }

    public static void foneField(TextField textField) {
        MaskFieldUtil.maxField(textField, 14);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                int tam = value.length();
                value = value.replaceFirst("(\\d{2})(\\d)", "($1)$2");
                value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                if (tam > 10) {
                    value = value.replaceAll("-", "");
                    value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                }
                textField.setText(value);
                MaskFieldUtil.positionCaret(textField);
            }
        });
    }

    public static void cpfField(TextField textField) {
        MaskFieldUtil.maxField(textField, 14);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
                textField.setText(value);
                MaskFieldUtil.positionCaret(textField);
            }
        });
    }

    public static void cnpjField(TextField textField) {
        MaskFieldUtil.maxField(textField, 18);
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{2})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{3})(\\d)", "$1/$2");
                value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                textField.setText(value);
                MaskFieldUtil.positionCaret(textField);
            }
        });
    }
    
    public static void upperCase(TextField campo) {
        campo.textProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue!=null && !newValue.isEmpty()){
                campo.setText(newValue.toUpperCase());
            }
        });
    }

    public static void positionCaret(TextField textField) {
        Platform.runLater(() -> {
            if (textField.getText().length() != 0) {
                textField.positionCaret(textField.getText().length());
            }

        }
        );
    }

    public static void maxField(TextField textField, Integer length) {
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.length() > length) {
                textField.setText(oldValue);
            }
        }
        );
    }

    public static String onlyDigitsValue(TextField field) {
        String result = field.getText();
        if (result == null) {
            return null;
        }
        return result.replaceAll("[^0-9]", "");
    }

    public static String onlyAlfaNumericValue(TextField field) {
        String result = field.getText();
        if (result == null) {
            return null;
        }
        return result.replaceAll("[^0-9]", "");
    }

    static {
        Collections.addAll(ignoreKeyCodes, new KeyCode[]{KeyCode.F1, KeyCode.F2, KeyCode.F3, KeyCode.F4, KeyCode.F5, KeyCode.F6, KeyCode.F7, KeyCode.F8, KeyCode.F9, KeyCode.F10, KeyCode.F11, KeyCode.F12});
    }

}
