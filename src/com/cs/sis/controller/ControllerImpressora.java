package com.cs.sis.controller;


import com.cs.sis.controller.find.FindCliente;
import com.cs.sis.controller.find.FindFuncionario;
import com.cs.sis.controller.impressora.ControllerImpressoraDaruma;
import com.cs.sis.controller.impressora.ControllerImpressoraLocal;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.Arquivo;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/*versao Daruma*/
public abstract class ControllerImpressora {

    private static ControllerImpressora instance = null;

    public static ControllerImpressora getInstance() {
        if (instance == null) {
            Arquivo arq = new Arquivo();
            String tipo = (String) arq.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.TIPO_DE_IMPRESSORA);
            
            if(tipo == "ECF"){
                instance = new ControllerImpressoraDaruma();
            }else{
                instance = new ControllerImpressoraLocal();  
            }
        }
        return instance;
    }

    public String criarTexto(Venda v){
        FindFuncionario ffunc = new FindFuncionario();
        FindCliente fcli = new FindCliente();
        
        String texto = "                  "+
                    VariaveisDeConfiguracaoUtil.getNomeEstabelecimento()+
                    "                   \n";
 
            texto += " Data/Hora: "
                    + new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(v
                            .getDia().getTime()) + "  \n"
                    +  " ---------------- Mercadorias ----------------- \n\n";

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
            texto += " ---------------------------------------------- \n\n";
            
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
            texto += "\n\n ID: "+v.getId() + "\n\n";
            texto += " ---------------------------------------------- \n\n\n\n\n\n\n\n\n\n";
            return texto;
    }
    // ao utilizar esse metodo, deve ser feito um try com Error | Exception e e printar
    public abstract int testeConectividadeImpressora();

    //sem tratamento de erros
    public abstract boolean imprimirTexto(String texto);

    public abstract boolean imprimirVenda(Venda v);

}
