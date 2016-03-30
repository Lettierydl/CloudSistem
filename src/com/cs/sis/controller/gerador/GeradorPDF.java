/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.controller.gerador;

import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.util.Arquivo;
import com.cs.ui.img.IMG;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
            inserirHead(doc, "Venda do Cliente " + v.getCliente().getNome(), subTitulo);

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
            table.addCell(it.getProduto().getDescricao());

            table.addCell(new DecimalFormat("0.000").format(it.getQuantidade()));
            table.addCell(new DecimalFormat("0.00").format(it.getValorProduto()));
            table.addCell(new DecimalFormat("0.00").format(it.getTotal()));
        }

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
}
