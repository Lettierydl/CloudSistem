package com.cs.sis.controller.impressora;

import com.cs.sis.controller.*;
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
public class ControllerImpressoraDaruma extends ControllerImpressora{

    private static ControllerImpressoraDaruma instance = null;

    public ControllerImpressoraDaruma() { }

    public static void carregarDLL() {
        try {
            System.loadLibrary("DarumaFramework");
            return;
        } catch (Exception | Error e) {
        }
        String userdir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        try {
            System.load(userdir + separator + "DarumaFrameWork.dll");
            return;
        } catch (Exception | Error e) {
        }

        try {
            System.load(userdir + ".jar" + separator + "DarumaFrameWork.dll");
            return;
        } catch (Exception | Error e) {
        }

        try {
            System.load(userdir + separator + "CloudSistem" + separator + "DarumaFrameWork.dll");
            return;
        } catch (Exception | Error e) {
        }

        //System.out.println("Comunicacao com Impressora:" + ECF.eBuscarPortaVelocidade_ECF_Daruma());
    }

    // ao utilizar esse metodo, deve ser feito um try com Error | Exception e e printar
    @Override
    public int testeConectividadeImpressora() {
        carregarDLL();
        return ECF.eBuscarPortaVelocidade_ECF_Daruma();
    }

    //sem tratamento de erros
    @Override
    public boolean imprimirTexto(String texto) {
        carregarDLL();
        ECF.iRGAbrirPadrao_ECF_Daruma();
        ECF.iRGImprimirTexto(texto);
        ECF.iRGFechar_ECF_Daruma();
        return true;
    }

    public boolean imprimirVenda(Venda v) {
        carregarDLL();

        FindFuncionario ffunc = new FindFuncionario();
        FindCliente fcli = new FindCliente();

        try {
            ECF.iRGAbrirPadrao_ECF_Daruma();
        } catch (Error | Exception e) {
            System.out.println("Não abriu Relatorio Gerencial");
        }

        int iRGImprimirTexto = 0;
        try {
            ECF.iRGAbrirPadrao_ECF_Daruma();

            String texto = " Data/Hora: "
                    + new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(v
                            .getDia().getTime()) + "  \n"
                    + " ---------------- Mercadorias ----------------- \n";

            ECF.iRGImprimirTexto(texto);
            texto = "";

            for (ItemDeVenda it : v.getItensDeVenda()) {
                if (it.getDescricaoProduto().length() > 30) {
                    texto += "- "
                            + it.getDescricaoProduto().toUpperCase()
                            + "\n \t \t ";
                } else {
                    texto += "- "
                            + it.getDescricaoProduto().toUpperCase()
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

            if (v.getPartePaga() > 0) {
                String partePaga = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getPartePaga()- v.getDesconto()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");
                String subtotal = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getTotal()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");

                String total = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getTotal() - v.getPartePaga()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");

                texto += " Valor Pago: R$ " + partePaga + "  \n";

                if (v.getDesconto() > 0) {
                    String desconto = new DecimalFormat("0.00").format(
                            new BigDecimal(v.getDesconto()).setScale(2,
                                    RoundingMode.HALF_UP).doubleValue()).replace(
                                    ".", ",");
                    texto += " Desconto: R$ " + desconto + "  \n";
                }

                texto += " TOTAL: R$ " + subtotal + "  \n\n";
                texto += " Débito adicionado ao Cliente: R$ " + total + "  \n\n";
            } else {
                if (v.getDesconto() > 0) {
                    String desconto = new DecimalFormat("0.00").format(
                            new BigDecimal(v.getDesconto()).setScale(2,
                                    RoundingMode.HALF_UP).doubleValue()).replace(
                                    ".", ",");
                    texto += " Desconto: R$ " + desconto + "  \n";
                }

                String subtotal = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getTotal()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");
                texto += " TOTAL: R$ " + subtotal + "  \n\n";
            }

            texto += " Operador: "
                    + ffunc
                    .funcionarioComId(v.getFuncionario().getId())
                    .getNome().toUpperCase() + " \n";

            try {
                Cliente c = fcli
                        .ClienteComId(v.getCliente().getId());
                texto += " Cliente: " + c.getNome().toUpperCase() + " \n";

                String debito = new DecimalFormat("0.00").format(
                        new BigDecimal(c.getDebito()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue())
                        .replace(".", ",");
                texto += " Saldo total: R$ " + debito + " \n \n";

                if (!v.getObservacao().isEmpty()) {
                    texto += " Obs: " + v.getObservacao().toUpperCase() + " \n";
                }

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
                if (it.getDescricaoProduto().length() > 30) {
                    texto += "- "
                            + it.getDescricaoProduto().toUpperCase()
                            + "\n \t \t ";
                } else {
                    texto += "- "
                            + it.getDescricaoProduto().toUpperCase()
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

            if (v.getPartePaga() > 0) {
                String partePaga = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getPartePaga() - v.getDesconto()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");
                String subtotal = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getTotal()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");

                String total = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getTotal() - v.getPartePaga()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");

                texto += " Valor Pago: R$ " + partePaga + "  \n";
                if (v.getDesconto() > 0) {
                    String desconto = new DecimalFormat("0.00").format(
                            new BigDecimal(v.getDesconto()).setScale(2,
                                    RoundingMode.HALF_UP).doubleValue()).replace(
                                    ".", ",");
                    texto += " Desconto: R$ " + desconto + "  \n";
                }
                texto += " TOTAL: R$ " + subtotal + "  \n\n";
                texto += " Débito adicionado ao Cliente: R$ " + total + "  \n\n";
            } else {
                String subtotal = new DecimalFormat("0.00").format(
                        new BigDecimal(v.getTotal()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue()).replace(
                                ".", ",");
                if (v.getDesconto() > 0) {
                    String desconto = new DecimalFormat("0.00").format(
                            new BigDecimal(v.getDesconto()).setScale(2,
                                    RoundingMode.HALF_UP).doubleValue()).replace(
                                    ".", ",");
                    texto += " Desconto: R$ " + desconto + "  \n";
                }
                texto += " TOTAL: R$ " + subtotal + "  \n\n";
            }

            texto += " Operador: "
                    + ffunc
                    .funcionarioComId(v.getFuncionario().getId())
                    .getNome().toUpperCase() + " \n";

            try {
                Cliente c = fcli
                        .ClienteComId(v.getCliente().getId());
                texto += " Cliente: " + c.getNome().toUpperCase() + " \n";

                String debito = new DecimalFormat("0.00").format(
                        new BigDecimal(c.getDebito()).setScale(2,
                                RoundingMode.HALF_UP).doubleValue())
                        .replace(".", ",");
                texto += " Saldo total: R$ " + debito + " \n \n";
            } catch (Exception e2) {
            }

            texto += " ---------------------------------------------- \n";
            System.out.println(texto);
            //e.printStackTrace();
            return false;
        }
        if (1 == iRGImprimirTexto) {
            return true;
        }
        return false;
    }

}
