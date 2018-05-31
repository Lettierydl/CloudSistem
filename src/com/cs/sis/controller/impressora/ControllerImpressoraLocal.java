package com.cs.sis.controller.impressora;

import com.cs.sis.controller.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.cs.sis.controller.find.FindCliente;
import com.cs.sis.controller.find.FindFuncionario;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.Arquivo;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/*versao Daruma*/
public class ControllerImpressoraLocal extends ControllerImpressora{

    public static String IMPRESSORA = "ELGIN i7(USB)";
    public ControllerImpressoraLocal() { }

    public static String[] listaImpressora() {
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        String[] nomes = new String[ps.length];
        for (int i = 0, l = nomes.length; i < l; i++) {
            nomes[i] = ps[i].getName();
        }
        return nomes;
    }

    private static PrintService selectImpress(String imp) {
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(
                DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        for (PrintService p : ps) {
            if (p.getName().equals(imp)) {
                return p;
            }
        }
        return null;
    }
    
    public static boolean imprimir(String impressora, String texto) {
        try {
            PrintService[] ps = PrintServiceLookup.lookupPrintServices(
                DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            PrintService service = null;
            for (PrintService p : ps) {
                if (p.getName().equals(impressora)) {
                    service = p;
                    break;
                }
            }
            DocPrintJob dpj = service.createPrintJob();
            InputStream is = new ByteArrayInputStream(texto.getBytes());
            SimpleDoc sd = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE,
                    null);
            dpj.print(sd, null);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean imprimir(PrintService impressora, String texto) {
        try {
            DocPrintJob dpj = impressora.createPrintJob();
            InputStream is = new ByteArrayInputStream(texto.getBytes());
            SimpleDoc sd = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE,
                    null);
            dpj.print(sd, null);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    
    // ao utilizar esse metodo, deve ser feito um try com Error | Exception e e printar
    @Override
    public int testeConectividadeImpressora() {
        return 0;
    }

    //sem tratamento de erros
    @Override
    public boolean imprimirTexto(String texto) {
        /*File f = new File("");
        try {
            f = File.createTempFile("imprimir_cs_", ".txt");
            Arquivo.criarArquivoTexto(texto,f);
        } catch (IOException ex) {
        }
        try {
            Desktop.getDesktop().print(f);
            f.deleteOnExit();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ControllerImpressoraLocal.class.getName()).log(Level.SEVERE, null, ex);
        }
        f.deleteOnExit();*/
        
        /*String nomeImpressoras[] = listaImpressora();
        for (String nome : nomeImpressoras) {
            System.out.println(nome);
        }
        //ELGIN i7(USB)
        System.out.println(nomeImpressoras[2]);//ELGIN i7(USB)
         */
        PrintService impress = selectImpress(ControllerImpressoraLocal.IMPRESSORA); //ELGIN i7(USB)
        return imprimir(impress, texto.endsWith("\n\n\n\n\n\n\n\n")?texto:(texto+"\n\n\n\n\n\n\n\n") );
    }

    @Override
    public boolean imprimirVenda(Venda v) {
        //Elgin line
        try {      
            return imprimirTexto(super.criarTexto(v));
        } catch (Exception | Error e) {
            System.out.println(super.criarTexto(v));
            //e.printStackTrace();
            return false;
        }
    }

}
