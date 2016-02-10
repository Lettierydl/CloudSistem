package com.cs.sis.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import br.com.daruma.jna.ECF;
import com.cs.sis.controller.find.FindCliente;
import com.cs.sis.controller.find.FindFuncionario;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;

/*versao Daruma*/
public class ControllerImpressora {

    private static ControllerImpressora instance = null;

    public static ControllerImpressora getInstance() {
        if (instance == null) {
            instance = new ControllerImpressora();
        }
        return instance;
    }

    private ControllerImpressora() {

    }

    public static void carregarDLL() {
        try {
            System.loadLibrary("DarumaFramework");
            return;
        } catch (Exception e) {
            System.err.println("Impressora não conetada");
        }
        String userdir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        try{
            System.load(userdir + separator + "DarumaFrameWork.dll");
            return;
        }catch(Exception e){
            System.err.println("Impressora não conetada");
        }
        
        try{
            System.load(userdir+".jar" + separator + "DarumaFrameWork.dll");
            return;
        }catch(Exception e){
            System.err.println("Impressora não conetada");
        }
        
        

        //System.out.println("Comunicacao com Impressora:" + ECF.eBuscarPortaVelocidade_ECF_Daruma());
    }

    public boolean imprimirVenda(Venda v) {
        carregarDLL();

        FindFuncionario ffunc = new FindFuncionario();
        FindCliente fcli = new FindCliente();

        int iRGImprimirTexto = 0;
        try {
            carregarDLL();
        } catch (Error | Exception e) {
        }
        try {

            String texto = " Data/Hora: "
                    + new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(v
                            .getDia().getTime()) + "  \n"
                    + " ---------------- Mercadorias ----------------- \n";

            ECF.iRGImprimirTexto(texto);
            texto = "";

            for (ItemDeVenda it : v.getItensDeVenda()) {
                if (it.getProduto().getDescricao().length() > 30) {
                    texto += "- "
                            + it.getProduto().getDescricao().toUpperCase()
                            + "\n \t \t ";
                } else {
                    texto += "- "
                            + it.getProduto().getDescricao().toUpperCase()
                            + "  ";
                }

                if (new Double(it.getQuantidade()).toString().split(".").length == 1) {
                    texto += String.valueOf((int) it.getQuantidade()).replace(
                            ".", ",");
                } else {
                    texto += new DecimalFormat("0.000").format(
                            new BigDecimal(it.getQuantidade()).setScale(2,
                                    RoundingMode.HALF_UP).doubleValue())
                            .replace(".", ",");
                }

                texto += " x "
                        + new DecimalFormat("0.00").format(
                                new BigDecimal(it.getValorProduto()).setScale(
                                        2, RoundingMode.HALF_UP).doubleValue())
                        .replace(".", ",") + " = ";
                texto += new DecimalFormat("0.00").format(
                        new BigDecimal(it.getTotal()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",")
                        + "\n";
                ECF.iRGImprimirTexto(texto);
                texto = "";
            }

            texto += " ---------------------------------------------- \n";
            ECF.iRGImprimirTexto(texto);
            texto = "";

            String total = String.valueOf(
                    new BigDecimal(v.getTotal()).setScale(2,
                            RoundingMode.HALF_UP).doubleValue()).replace(
                            ".", ",");
            texto += " TOTAL: R$ " + total + "  \n\n";

            texto += " Operador: "
                    + ffunc
                    .funcionarioComId(v.getFuncionario().getId())
                    .getNome().toUpperCase() + " \n";

            try {
                Cliente c = fcli
                        .clienteComId(v.getCliente().getId());
                texto += " Cliente: " + c.getNome().toUpperCase() + " \n";

                if (!v.getObservacao().isEmpty()) {
                    texto += " Obs: " + v.getObservacao().toUpperCase() + " \n";
                }

                String debito = String.valueOf(
                        new BigDecimal(c.getDebito()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue())
                        .replace(".", ",");
                texto += " Saldo total: R$ " + debito + " \n \n";
            } catch (Exception e) {
            }
            ECF.iRGImprimirTexto(texto);
            texto = "";

            texto += " ---------------------------------------------- \n";
            ECF.iRGImprimirTexto(texto);

            iRGImprimirTexto = ECF.iRGFechar_ECF_Daruma();
        } catch (Exception | Error e) {
            String texto = " Data/Hora: "
                    + new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(v
                            .getDia().getTime()) + "  \n"
                    + " ---------------- Mercadorias ----------------- \n";

            for (ItemDeVenda it : v.getItensDeVenda()) {
                if (it.getProduto().getDescricao().length() > 30) {
                    texto += "- "
                            + it.getProduto().getDescricao().toUpperCase()
                            + "\n \t \t ";
                } else {
                    texto += "- "
                            + it.getProduto().getDescricao().toUpperCase()
                            + "  ";
                }

                if (new Double(it.getQuantidade()).toString().split(".").length == 1) {
                    texto += String.valueOf((int) it.getQuantidade()).replace(
                            ".", ",");
                } else {
                    texto += new DecimalFormat("0.000").format(
                            new BigDecimal(it.getQuantidade()).setScale(2,
                                    RoundingMode.HALF_UP).doubleValue())
                            .replace(".", ",");
                }

                texto += " x "
                        + new DecimalFormat("0.00").format(
                                new BigDecimal(it.getValorProduto()).setScale(
                                        2, RoundingMode.HALF_UP).doubleValue())
                        .replace(".", ",") + " = ";
                texto += new DecimalFormat("0.00").format(
                        new BigDecimal(it.getTotal()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",")
                        + "\n";
            }

            texto += " ---------------------------------------------- \n";

            String total = String.valueOf(
                    new BigDecimal(v.getTotal()).setScale(2,
                            RoundingMode.HALF_UP).doubleValue()).replace(
                            ".", ",");
            texto += " TOTAL: R$ " + total + "  \n\n";

            texto += " Operador: "
                    + ffunc
                    .funcionarioComId(v.getFuncionario().getId())
                    .getNome().toUpperCase() + " \n";

            try {
                Cliente c = fcli
                        .clienteComId(v.getCliente().getId());
                texto += " Cliente: " + c.getNome().toUpperCase() + " \n";

                String debito = String.valueOf(
                        new BigDecimal(c.getDebito()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue())
                        .replace(".", ",");
                texto += " Saldo total: R$ " + debito + " \n \n";
            } catch (Exception e2) {
            }

            texto += " ---------------------------------------------- \n";
            //System.out.println(texto);
            return false;
        }
        if (1 == iRGImprimirTexto) {
            return true;
        }
        return false;
    }

}
