package Funcionalidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;

public class Registro implements Serializable {

    private String razao = "CloudSistem";
    private String endereco= "";
    private String proprietario= "";
    private String telefone= "";
    private String chaveRegistro= "";
    private int cont = 15;
    private int dia = -1;
    private boolean registrado = false;
    private static Arquivo arq = new Arquivo();
    private static final String arqv = "Registro.csi";

    private Registro() {
    }


    public boolean registrar(String registro, String razao, String endereco, String proprietario, String telefone) {
        try{
            if (registro.substring(5, 9).toUpperCase().replace(" ", "1").equalsIgnoreCase(razao.substring(4, 6).toUpperCase().replace(" ", "1") + razao.substring(0, 1).toUpperCase().replace(" ", "1") + razao.substring(razao.length() - 1).toUpperCase().replace(" ", "1"))) {
            if (registro.substring(15, 19).equalsIgnoreCase(chaveComputador().replace("-", "").substring(4, 8))) {
                this.registrado = true;
                this.telefone = telefone;
                this.endereco = endereco;
                this.razao = razao;
                this.proprietario = proprietario;
                this.chaveRegistro = registro;
                arq.salvarRegistro(this, arqv);
                return true;
            }
        }
        }catch (Exception e) {
        }
        return false;
    }

    public String chaveComputador() {
        String chave = "";
        try {
            String ip = getMac();
            if (ip == null) {
                ip = "00-90-F5-74-D3-19";
            }
            chave += String.valueOf(Math.random()).replace(".", "").substring(0, 4) + "-";
            chave += ip.replace(".", "-").substring(0, 5) + String.valueOf(Math.random()).replace(".", "").substring(0, 2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return chave;
    }

    public static Registro getIntance() {
        Registro reg = (Registro) arq.recuperarRegistro(arqv);
        if(reg == null){
            reg = new Registro();
                arq.salvarRegistro(reg, arqv);
            }
        if(reg != null){
            return reg;
        }
        return new Registro();
    }

    public void iniciarRegistro() {
        Registro r = getIntance();
        this.cont = r.getCont();
        this.dia = r.getDia();
        this.endereco = r.getEndereco();
        this.proprietario = r.getProprietario();
        this.razao = r.getRazao();
        this.registrado = r.isRegistrado();
        if (registrado) {
            return;
        }
        if (dia == -1) {
            dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        if (dia != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            cont--;
            dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
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

    public String getTelefone() {
        return telefone;
    }

    public int getCont() {
        return cont;
    }

    public int getDia() {
        return dia;
    }

    public boolean isRegistrado() {
        return registrado;
    }
    
    public boolean isBloqueado(){
        return this.cont == 0;
    }
   
    
    
    public String getMac() {
        BufferedReader in;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("ipconfig /all");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if ((line.indexOf("Endere") != -1) && (line.indexOf("IP") == -1)) {
                   StringBuffer bf =  new StringBuffer((line.substring(line.indexOf("-") + 2)));
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
