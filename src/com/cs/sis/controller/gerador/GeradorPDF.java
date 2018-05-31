/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.controller.gerador;

import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.util.Arquivo;
import com.cs.sis.util.OperacaoStringUtil;
import com.cs.ui.img.IMG;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lettiery
 */
public class GeradorPDF {

    private Arquivo a;

    public GeradorPDF() {
        a = new Arquivo();
    }

    public String gerarPdfRelatorioEstoqueProdutos(boolean valoresNegativos, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            //String subTitulo = "Estoque Verificado no dia "
            //        + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
            
            String subTitulo = "Balanço Geral de Estoque do período 01/12/2017 à 31/12/2017";
            inserirHead(doc, "Mercadinho Popular\n"
                    + "Edvaneide Torres Vilar de Carvalho\n"
                    + "CNPJ: 07.643.907/0001-18", subTitulo);

            PdfPTable table = getTableEstoqueProdutos(valoresNegativos);

            doc.add(table);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String gerarPdfRelatorioEstoqueProdutos(boolean valoresNegativos, boolean pararLista, Calendar dataInicio, Calendar dataFim, List<String> codigos_retirados, double total_requisitado, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter instance = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            
            //String subTitulo = "Estoque Verificado no dia "
            //        + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
            
            String data = "";
            try{
                data += OperacaoStringUtil.formatDataValor(dataInicio)+ " à "+
                    OperacaoStringUtil.formatDataValor(dataFim);
            }catch(NullPointerException e){
                data += ("01/01/"+(Calendar.getInstance().get(Calendar.YEAR)-1))+ " à ";
                data += ("31/12/"+(Calendar.getInstance().get(Calendar.YEAR)-1));
            }
            
            String subTitulo = "Balanço Geral de Estoque do período "+data; //" 01/12/2017 à 31/12/2017";
            inserirHead(doc, "Mercadinho Popular\n"
                    + "Edvaneide Torres Vilar de Carvalho\n"
                    + "CNPJ: 07.643.907/0001-18", subTitulo);

            PdfPTable table = getTableEstoqueProdutos(valoresNegativos, pararLista,codigos_retirados, total_requisitado);

            doc.add(table);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String gerarPdfRelatorioDebitoClientes(double maiorQue, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String subTitulo ;
            if(maiorQue == 0){
                subTitulo = "Débito dos Clientes \n Referênte ao dia "
                    + new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            }else{
                subTitulo = "Clientes com Débitos Maiores que "
                    +OperacaoStringUtil.formatarStringValorMoedaComDescricao(maiorQue)
                    +"\n Referênte ao dia "
                    + new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            }
            inserirHead(doc, "Débito dos Clientes", subTitulo);

            PdfPTable table = getTableDebitoClientes(maiorQue);

            doc.add(table);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }

    }
    
    
    public String gerarPdfRelatorioBalancoProdutos(Date inicio, Date fim, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String subTitulo = "Saída de produtos \n Referênte do dia "
                    + new SimpleDateFormat("dd/MM/yyyy").format(inicio) + " ao dia " + new SimpleDateFormat("dd/MM/yyyy").format(fim);
            inserirHead(doc, "Balanço de Produtos", subTitulo);

            PdfPTable table = getTableBalancoProdutos(inicio, fim);

            doc.add(table);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public String gerarPdfDaVenda(Venda v, List<ItemDeVenda> itens, File destino) throws IOException {
        Document doc = new Document();

        try {
            String path = a.getRelatorio().getCanonicalPath() + "/Venda_" + v.getId() + ".pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String subTitulo = "Venda Realizada no dia "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getDia().getTime());
            try {
                inserirHead(doc, "Venda do Cliente " + v.getCliente().getNome(), subTitulo);
            } catch (NullPointerException ne) {
                inserirHead(doc, "Venda à vista ID: " + v.getId(), subTitulo);
            }

            PdfPTable table = getTableDeItens(itens);

            doc.add(table);

            Paragraph p2 = new Paragraph(
                    "Total: " + new DecimalFormat("0.00").format(v.getTotal())
                    + " \n Funcionário: " + v.getFuncionario().getNome(), new Font(Font.FontFamily.COURIER, 16, Font.BOLD));
            doc.add(p2);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }

    }

    public String gerarPdfRelatorioBalancoProdutos(Date inicio, Date fim) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + "/RelatorioBalancoProdutos.pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String subTitulo = "Saída de produtos \n Referênte do dia "
                    + new SimpleDateFormat("dd/MM/yyyy").format(inicio) + " ao dia " + new SimpleDateFormat("dd/MM/yyyy").format(fim);
            inserirHead(doc, "Balanço de Produtos", subTitulo);

            PdfPTable table = getTableBalancoProdutos(inicio, fim);

            doc.add(table);

            doc.close();
            return path;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public String gerarPdfDaVenda(Venda v, List<ItemDeVenda> itens) {
        Document doc = new Document();

        try {
            String path = a.getRelatorio().getCanonicalPath() + "/Venda_" + v.getId() + ".pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String subTitulo = "Venda Realizada no dia "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getDia().getTime());
            if(v.getCliente() != null){
                inserirHead(doc, "Venda do Cliente " + v.getCliente().getNome(), subTitulo);
            }else{//venda a vista
                inserirHead(doc, "Venda À Vista", subTitulo);
            }

            PdfPTable table = getTableDeItens(itens);

            doc.add(table);

            Paragraph p2 = new Paragraph(
                    "Total: " + new DecimalFormat("0.00").format(v.getTotal())
                    + " \n Funcionário: " + v.getFuncionario().getNome(), new Font(Font.FontFamily.COURIER, 16, Font.BOLD));
            doc.add(p2);

            doc.close();
            return path;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    private PdfPTable getTableDeItens(List<ItemDeVenda> itens) throws DocumentException {

        PdfPTable table = new PdfPTable(4);
        table.setWidths(new int[]{5, 1, 1, 1});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Produto");
        table.addCell("Quantidade");
        table.addCell("Valor UN");
        table.addCell("Total");
        table.getDefaultCell().setBackgroundColor(null);

        for (ItemDeVenda it : itens) {
            table.addCell(it.getDescricaoProduto());

            table.addCell(new DecimalFormat("0.000").format(it.getQuantidade()));
            table.addCell(new DecimalFormat("0.00").format(it.getValorProduto()));
            table.addCell(new DecimalFormat("0.00").format(it.getTotal()));
        }

        return table;

    }
    
    private PdfPTable getTableEstoqueProdutos(boolean valoresNegativos) throws DocumentException {
        double total_requisitado = 381624.18;
        
        Map<String, Double[]> saida = GeradorRelatorio.getRelatorioEstoqueProdutos(valoresNegativos);

        PdfPTable table = new PdfPTable(4);
        table.setWidths(new int[]{5, 2, 1, 1});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Produto");
        table.addCell("Quantidade em Estoque");
        table.addCell("Valor de Compra");
        table.addCell("SubTotal");
        table.getDefaultCell().setBackgroundColor(null);
        double quantidade = 0, total = 0;
        List<String> c = new ArrayList<String>();
        c.addAll(saida.keySet());
        Collections.sort(c);
        for (String produto : c) {
            
            Double[] val = saida.get(produto);
            
            double qt, vc, sub;
            qt = val[0]; vc = val[1]; sub = val[0]*val[1];
        
            if(qt > 0 && qt < 10000){
                quantidade += qt;
                total += (qt * vc);
                table.addCell(produto);
                table.addCell(new DecimalFormat("0.000").format(qt));
                table.addCell(new DecimalFormat("0.00").format(vc));
                table.addCell(new DecimalFormat("0.00").format(qt*vc));
            }else{
                continue;
                //table.addCell(new DecimalFormat("0.00").format(val[1]));
                //table.addCell(new DecimalFormat("0.000").format(0));
                //table.addCell(new DecimalFormat("0.00").format(vc));
                //table.addCell(new DecimalFormat("0.00").format(0));
            }
            if(total_requisitado-10 <= total && total <= total_requisitado+10){
                break;
            }
            
        }

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));

        PdfPCell cell = new PdfPCell(new Paragraph("Total"));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell(cell);

        //table.addCell(OperacaoStringUtil.formatarStringValorIntegerEPonto(
        //       saida.size()) + " Produtos");

        //table.addCell(OperacaoStringUtil.formatarStringQuantidadeEPonto(quantidade)
        //        + " Itens");
        
        String tot = OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(381624.18);
        PdfPCell cel2 = new PdfPCell(new Paragraph(tot));
        cel2.setColspan(2);
        cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cel2.setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell(cel2);
        
        //table.addCell(OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(total));
        //System.out.println(total);
        return table;
    }
    /*
    @parametr boolean paraLista = true a lista é parada ao aingir o limite
    */
    private PdfPTable getTableEstoqueProdutos(boolean valoresNegativos,boolean paraLista,  List<String> codigos_retirados, double total_requisitado) throws DocumentException {
        //double total_requisitado = 381624.18;
        
        Map<String, Double[]> saida = GeradorRelatorio.getRelatorioEstoqueProdutos(valoresNegativos, codigos_retirados);

        PdfPTable table = new PdfPTable(4);
        table.setWidths(new int[]{5, 2, 1, 1});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Produto");
        table.addCell("Quantidade em Estoque");
        table.addCell("Valor de Compra");
        table.addCell("SubTotal");
        table.getDefaultCell().setBackgroundColor(null);
        double quantidade = 0, total = 0;
        List<String> c = new ArrayList<String>();
        c.addAll(saida.keySet());
        Collections.sort(c);
        for (String produto : c) {
            if(paraLista && total_requisitado <= total){
                break;
            }
            
            Double[] val = saida.get(produto);
            
            double qt, vc, sub;
            qt = val[0]; vc = val[1]; sub = val[0]*val[1];
        
            if(qt > 0 && qt < 10000){
                quantidade += qt;
                total += (qt * vc);
                table.addCell(produto);
                table.addCell(new DecimalFormat("0.000").format(qt));
                table.addCell(new DecimalFormat("0.00").format(vc));
                table.addCell(new DecimalFormat("0.00").format(qt*vc));
            }else{
                continue;
                //table.addCell(new DecimalFormat("0.00").format(val[1]));
                //table.addCell(new DecimalFormat("0.000").format(0));
                //table.addCell(new DecimalFormat("0.00").format(vc));
                //table.addCell(new DecimalFormat("0.00").format(0));
            }
            
            
        }

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));

        PdfPCell cell = new PdfPCell(new Paragraph("Total"));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell(cell);

        //table.addCell(OperacaoStringUtil.formatarStringValorIntegerEPonto(
        //       saida.size()) + " Produtos");

        //table.addCell(OperacaoStringUtil.formatarStringQuantidadeEPonto(quantidade)
        //        + " Itens");
        
        String tot = OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(total_requisitado);
        PdfPCell cel2 = new PdfPCell(new Paragraph(tot));
        cel2.setColspan(2);
        cel2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cel2.setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell(cel2);
        
        //table.addCell(OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(total));
        //System.out.println(total);
        return table;
    }

    private PdfPTable getTableBalancoProdutos(Date inicio, Date fim) throws DocumentException {
        Map<String, Double[]> saida = GeradorRelatorio.getRelatorioDetalhadoSaidaProduto(inicio, fim);

        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{5, 1, 1, 1, 1});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Produto");
        table.addCell("Estoque");
        table.addCell("Compra");
        table.addCell("Venda");
        table.addCell("Lucro");
        table.getDefaultCell().setBackgroundColor(null);
        double compra = 0, venda = 0, lucro = 0, qt = 0;
        List<String> c = new ArrayList<String>();
        c.addAll(saida.keySet());
        Collections.sort(c);
        for (String produto : c) {
            table.addCell(produto);
            Double[] val = saida.get(produto);
            compra += val[0];
            venda += val[1];
            lucro += val[3];
            qt += val[2];
            table.addCell(new DecimalFormat("0.000").format(val[2]));
            table.addCell(new DecimalFormat("0.00").format(val[0]));
            table.addCell(new DecimalFormat("0.00").format(val[1]));
            table.addCell(new DecimalFormat("0.00").format(val[3]));
        }

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));

        PdfPCell cell = new PdfPCell(new Paragraph("Total"));
        cell.setColspan(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell(cell);

        table.addCell(saida.size() + " Produtos");

        table.addCell(new DecimalFormat("0.000").format(qt));
        table.addCell(new DecimalFormat("0.00").format(compra));
        table.addCell(new DecimalFormat("0.00").format(venda));
        table.addCell(new DecimalFormat("0.00").format(lucro));

        return table;
    }

    public void inserirHead(Document doc, String titulo, String subTitulo) throws MalformedURLException, DocumentException {
        Image img = null;
        try {
            img = Image.getInstance(IMG.class.getResource("logo_relatorio.png"));

            img.setAlignment(Element.ALIGN_LEFT);

            doc.add(img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font f = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
        Paragraph p = new Paragraph(titulo, f);
        Paragraph p2 = new Paragraph(subTitulo, new Font(Font.FontFamily.COURIER, 16, Font.BOLD));
        p.setAlignment(Element.ALIGN_CENTER);

        doc.add(p);
        doc.add(p2);
    }

    private PdfPTable getTableDebitoClientes(double maiorQue) throws DocumentException {
        List<Object[]> debitos = GeradorRelatorio.getRelatorioClientesComDebitoMaiorQue(maiorQue);

        PdfPTable table = new PdfPTable(2);
        table.setWidths(new int[]{5, 2});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Cliente");
        table.addCell("Débito");
        table.getDefaultCell().setBackgroundColor(null);
        double total = 0;
        for (Object[] d : debitos) {
            table.addCell((String) d[0]);
            double val = (double) d[1];
            table.addCell(OperacaoStringUtil.formatarStringValorMoedaEPonto(val));
            total+= val;
        }

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));

        PdfPCell cell = new PdfPCell(new Paragraph("Total"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell(cell);

        table.addCell(OperacaoStringUtil.formatarStringValorMoedaComDescricaoEPonto(total));

        
        table.addCell("Quantidade de Clientes");
        table.addCell(OperacaoStringUtil.formatarStringValorInteger(debitos.size()));

        
        return table;
    }

}
