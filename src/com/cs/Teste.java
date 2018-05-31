/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs;

import com.cs.sis.util.OperacaoStringUtil;
import com.cs.sis.util.Registro;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author Lettiery
 */
public class Teste {

    static private int abyte = 0;
    static private String password = "LETTIERY DLAMARE";

    public static String doConvert(String in) {

        StringBuffer tempReturn = new StringBuffer();

        for (int i = 0; i < in.length(); i++) {

            abyte = in.charAt(i);
            int cap = abyte & 32;
            abyte &= ~cap;
            abyte = ((abyte >= 'A') && (abyte <= 'Z') ? ((abyte - 'A' + 13) % 26 + 'A') : abyte) | cap;
            tempReturn.append((char) abyte);
        }

        return tempReturn.toString();

    }

    //Rua Rita Pereira de Almeida, N 173, Livramento - PB
    public static void main(String args[]) {
        //xRAZx-xPROx-xCHAx
            
        
        String serial = Registro.criarRegistro         //810D050565
        ("BELOSMAR VILAR DE CARVALHO", "EDVANEIDE TORRES VILAR",
                "16-7008-0789");
        
        System.out.println(serial);
        
        String dec = OperacaoStringUtil.criptografar18(serial.replace("-", "").toUpperCase()).toUpperCase();
            System.err.println(dec);
            String decR = dec.substring(1, 3).toUpperCase();
            String decP = dec.substring(6, 8).toUpperCase();
            String decC = dec.substring(11, 13).toUpperCase();
            System.err.println(decR);
            System.err.println(decP);
            System.err.println(decC);
    }

}
