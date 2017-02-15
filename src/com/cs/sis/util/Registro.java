package com.cs.sis.util;

import com.cs.sis.controller.gerador.GeradorRelatorio;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Registro implements Serializable {

    private String razao = "CloudSistem";
    private String endereco = "";
    private String proprietario = "";
    private String chaveRegistro = "";
    //private int cont = 15;
    //private int dia = -1;
    private boolean registrado = false;
    private static Arquivo arq = new Arquivo();
    private static final String arqv = "Registro.csi";
    private static final int daysFree = 15;

    private Registro() {}

    public boolean registrar(String registro, String razao, String endereco, String proprietario) {
        try {
            //xRAZx-xPROx-xCHAx
            String dec = OperacaoStringUtil.criptografar18(registro.replace("-", "").toUpperCase()).toUpperCase();
            String decR = dec.substring(1, 3).toUpperCase();
            String decP = dec.substring(6, 8).toUpperCase();
            String decC = dec.substring(11, 13).toUpperCase();

            if (razao.toUpperCase().startsWith(decR) && proprietario.toUpperCase().startsWith(decP) && chaveComputador().toUpperCase().startsWith(decC)) {
                this.registrado = true;
                this.endereco = endereco;
                this.razao = razao;
                this.proprietario = proprietario;
                this.chaveRegistro = registro;
                arq.salvarRegistro(this, arqv);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static String criarRegistro(String razao, String proprietario, String chaveDoComputador) {
        String decR = razao.substring(0, 3);
        String decP = proprietario.substring(0, 3);
        String decC = chaveDoComputador.substring(0, 3);

        String cod = String.valueOf(Math.random()).substring(3, 4) + decR + String.valueOf(Math.random()).substring(3, 4)
                + String.valueOf(Math.random()).substring(3, 4) + decP + String.valueOf(Math.random()).substring(3, 4)
                + String.valueOf(Math.random()).substring(3, 4) + decC + String.valueOf(Math.random()).substring(3, 4);
        cod = OperacaoStringUtil.criptografar18(cod);
        cod = cod.replaceAll("[^\\w]", "");
        cod = cod.replaceFirst("(\\w{5})(\\w)", "$1-$2");
        cod = cod.replaceFirst("(\\w{5})\\-(\\w{5})(\\w)", "$1-$2-$3");
        return cod;
    }

    public static String chaveComputador() {
        String chave = "";
        try {
            String ip = getMac();
            if (ip == null) {
                ip = "00-90-F5-74-D3-19";
            }
            chave += ip.replace(".", "-").substring(0, 5) + String.valueOf(Math.random()).replace(".", "").substring(0, 2);
            chave += "-" + String.valueOf(Math.random()).replace(".", "").substring(0, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return chave;
    }

    public static Registro getIntance() {
        Registro reg = null;
        try {
            reg = (Registro) arq.recuperarRegistro(arqv);
        } catch (Exception e) {
        }
        if (reg == null) {
            reg = new Registro();
            arq.salvarRegistro(reg, arqv);
            reg.iniciarRegistro();
        }
        if (reg != null) {
            return reg;
        }
        return new Registro();
    }

    private void iniciarRegistro() {
        Registro r = getIntance();
        //this.cont = r.getCont();
        //this.dia = r.getDia();
        this.endereco = r.getEndereco();
        this.proprietario = r.getProprietario();
        this.razao = r.getRazao();
        this.registrado = r.isRegistrado();
        if (registrado) {
            return;
        }
        
        arq.salvarRegistro(this, arqv);

    }

    public String getRazao() {
        return razao;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getProprietario() {
        return proprietario;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public boolean isBloqueado() {
        if (registrado) {
            return false;
        }
        String minDia = GeradorRelatorio.getMinDia();
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(minDia));
            int dif = OperacaoStringUtil.diffInDays(cal, Calendar.getInstance());
            return (dif >= daysFree);
        } catch (NullPointerException | ParseException ne) {
           ne.printStackTrace();
        }
        return false;
        
    }

    public static  String getMac() {
        BufferedReader in;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("ipconfig /all");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if ((line.indexOf("Endere") != -1) && (line.indexOf("IP") == -1)) {
                    StringBuffer bf = new StringBuffer((line.substring(line.indexOf("-") + 2)));
                    return new String(bf.reverse());
                }
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Erro: " + e);
        }
        return null;
    }

    public void setChaveRegistro(String chaveRegistro) {
        this.chaveRegistro = chaveRegistro;
    }

    public String getChaveRegistro() {
        return chaveRegistro;
    }

}
