/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.util.converter;

import java.text.DecimalFormat;
import javafx.util.StringConverter;

/**
 *
 * @author Lettiery
 */
public class ValueDecimalConverter extends StringConverter<Double> {

    @Override
    public String toString(Double object) {
        if (object.floatValue() == 0.0) {// é inteiro
            return new DecimalFormat("0").format(object);
        }
        return new DecimalFormat("0.000").format(object);
    }

    @Override
    public Double fromString(String string) {
        try {
            if (string.isEmpty()) {
                return 0.0;
            }
            return Double.valueOf(string.replace(".", "").replace(" ", "")
                    .replace(",", "."));
        } catch (NumberFormatException ne) {
            return 0.0;
        }
    }

}
