package com.cs.sis.controller.gerador;

import com.cs.sis.controller.Gerador;
import com.cs.sis.model.financeiro.FormaDePagamento;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.util.OperacaoStringUtil;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Lettiery
 */
public class GeradorRelatorio extends Gerador {

    public static String getMinDia() {
        // select min(cloudsistem.venda.dia) from cloudsistem.venda;
        String stringQuery = "select min(cloudsistem.venda.dia) from cloudsistem.venda;";

        Query query = getEntityManager().createNativeQuery(stringQuery);

        try {
            return new SimpleDateFormat("dd/MM/yyyy")
                    .format((Timestamp) query.getSingleResult());
        } catch (NullPointerException ne) {
            return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        }

    }

    /**
     * Posicao : descricao 0 : Soma de todas as partes pagas da venda 1 : Total
     * de todos os pagamentos realizados
     */
    public static double[] getRelatorioDeEntradaDeCaixa(Date diaInicio, Date diaFim) {

        Calendar di = Calendar.getInstance();
        di.setTime(diaInicio);
        di.set(Calendar.HOUR, 0);
        di.set(Calendar.MINUTE, 0);
        di.set(Calendar.SECOND, 00);

        Calendar df = Calendar.getInstance();
        df.setTime(diaFim);
        df.set(Calendar.HOUR, 23);
        df.set(Calendar.MINUTE, 59);
        df.set(Calendar.SECOND, 59);

        double[] valores = new double[2];

        String stringQuery = "select sum(v.partePaga) from Venda as v where v.formaDePagamento = :forma "
                + " and  v.dia between :diaInicio and :diaFim";

        Query query = getEntityManager().createQuery(stringQuery);
        query.setParameter("forma", FormaDePagamento.A_Vista);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            valores[0] = (double) query.getSingleResult();
        } catch (NullPointerException ne) {
            valores[0] = 0;
        }

        stringQuery = "select sum(p.valor) from Pagamento as p where p.data between :diaInicio and :diaFim";

        query = getEntityManager().createQuery(stringQuery);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            valores[1] = (double) query.getSingleResult();
        } catch (NullPointerException ne) {
            valores[1] = 0;
        }
        return valores;
    }

    /**
     * Posicao : descricao 0 : Total das vendas a vista 1 : Total das vendas a
     * prazo 2 : Total de dividas
     *
     * 3 : Quantidade de vendas a vista 4 : Quantidade de vendas a prazo 5 :
     * Quantidade de dividas
     */
    public static double[] getRelatorioDeVendas(Date diaInicio, Date diaFim) {
        Calendar di = Calendar.getInstance();
        di.setTime(diaInicio);
        di.set(Calendar.HOUR, 0);
        di.set(Calendar.MINUTE, 0);
        di.set(Calendar.SECOND, 00);

        Calendar df = Calendar.getInstance();
        df.setTime(diaFim);
        df.set(Calendar.HOUR, 23);
        df.set(Calendar.MINUTE, 59);
        df.set(Calendar.SECOND, 59);

        double[] valores = new double[6];

        String stringQuery = "select sum(v.total), count(v.id) from Venda as v where v.formaDePagamento = :forma "
                + " and  v.dia between :diaInicio and :diaFim";

        Query query = getEntityManager().createQuery(stringQuery);
        query.setParameter("forma", FormaDePagamento.A_Vista);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            Object[] o = (Object[]) query.getSingleResult();
            valores[0] = (double) o[0];
            valores[3] = (long) o[1];
        } catch (Exception e) {
        }

        stringQuery = "select sum(v.total), count(v.id) from Venda as v where v.formaDePagamento = :forma "
                + " and  v.dia between :diaInicio and :diaFim";

        query = getEntityManager().createQuery(stringQuery);
        query.setParameter("forma", FormaDePagamento.A_Prazo);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            Object[] o = (Object[]) query.getSingleResult();
            valores[1] = (double) o[0];
            valores[4] = (long) o[1];
        } catch (Exception e) {
        }

        stringQuery = "select sum(d.total), count(d.id) from Divida as d where "
                + " d.dia between :diaInicio and :diaFim";

        query = getEntityManager().createQuery(stringQuery);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            Object[] o = (Object[]) query.getSingleResult();
            valores[2] = (double) o[0];
            valores[5] = (long) o[1];
        } catch (Exception e) {
        }

        stringQuery = "select sum(d.total), count(d.id) from Divida as d, Venda as v where "
                + " d.dia between :diaInicio and :diaFim  and  v.dia between :diaInicio and :diaFim"
                + " and v.formaDePagamento = :forma ";

        query = getEntityManager().createQuery(stringQuery);
        query.setParameter("forma", FormaDePagamento.A_Prazo);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            Object[] o = (Object[]) query.getSingleResult();
            valores[2] = (double) o[0];
            valores[5] = (long) o[1];
        } catch (Exception e) {
        }
        return valores;
    }

    public static double[] getRelatorioProduto(Date diaInicio, Date diaFim) {
        Calendar di = Calendar.getInstance();
        di.setTime(diaInicio);
        di.set(Calendar.HOUR, 0);
        di.set(Calendar.MINUTE, 0);
        di.set(Calendar.SECOND, 00);

        Calendar df = Calendar.getInstance();
        df.setTime(diaFim);
        df.set(Calendar.HOUR, 23);
        df.set(Calendar.MINUTE, 59);
        df.set(Calendar.SECOND, 59);

        double[] valores = new double[2];

        String stringQuery = "select sum(it.total), sum((it.valorProduto - it.valorCompraProduto)* it.quantidade) from Venda as v,ItemDeVenda as it where "
                + " v.dia between :diaInicio and :diaFim";

        Query query = getEntityManager().createQuery(stringQuery);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        try {
            Object[] o = (Object[]) query.getSingleResult();
            valores[0] = (double) o[0];
            valores[1] = (double) o[1];
        } catch (Exception e) {
        }
        return valores;
    }

    /**
     * Map Chave : Descricao
     *
     * Valor 0 : Valor de Compra do Produto 1 : Valor de Venda do Produto 2 :
     * Quantidade Vendida 3 : Lucro Total
     *
     * @param diaInicio
     */
    public static Map<String, Double[]> getRelatorioDetalhadoSaidaProduto(
            Date diaInicio, Date diaFim) {
        Calendar di = Calendar.getInstance();
        di.setTime(diaInicio);
        di.set(Calendar.HOUR, 0);
        di.set(Calendar.MINUTE, 0);
        di.set(Calendar.SECOND, 00);

        Calendar df = Calendar.getInstance();
        df.setTime(diaFim);
        df.set(Calendar.HOUR, 23);
        df.set(Calendar.MINUTE, 59);
        df.set(Calendar.SECOND, 59);

        Map<String, Double[]> saida = new HashMap<String, Double[]>();
        EntityManager em = getEntityManager();
        Query query = em
                .createQuery(
                        "select p.descricao, p.valorDeCompra, p.valorDeVenda , sum(it.quantidade), sum((p.valorDeVenda - p.valorDeCompra)* it.quantidade) "
                        + " from Venda as v , Produto as p, ItemDeVenda as it "
                        + " where v.dia between :di and :df and it.venda = v "
                        + " and it.produto = p  group by p.descricao order by p.descricao");
        query.setParameter("di", di);
        query.setParameter("df", df);

        for (Object obj : query.getResultList()) {
            Object[] o = (Object[]) obj;
            Double[] valores = {0.0, 0.0, 0.0, 0.0};
            valores[0] = (Double) o[1];
            valores[1] = (Double) o[2];
            valores[2] = (Double) o[3];
            valores[3] = (Double) o[4];
            saida.put((String) o[0], valores);
        }
        return saida;

    }

    public static List<Venda> vendasDoCliente(Cliente cliente, Date diaInicio, Date diaFim) {
        String stringQuery = "select v FROM Venda as v ";
        stringQuery += "WHERE v.paga = false and v.cliente = ?"
                + " order by v.total , v.dia DESC ";

        Query query = getEntityManager().createQuery(stringQuery, Venda.class);
        query.setParameter(1, cliente);

        @SuppressWarnings("unchecked")
        List<Venda> vendas = (List<Venda>) query.getResultList();

        return vendas;
    }

    public static double getRelatorioDeProduto30Dias(int idProduto) {
        Calendar di = Calendar.getInstance();
        di.set(Calendar.DATE, di.get(Calendar.DATE) - 30);
        di.set(Calendar.HOUR, 0);
        di.set(Calendar.MINUTE, 0);
        di.set(Calendar.SECOND, 00);

        
        
        Calendar df = Calendar.getInstance();

        System.err.print(OperacaoStringUtil.formatDataValor(di));
        System.err.println(" - " + OperacaoStringUtil.formatDataValor(df));
        
        String stringQuery = "select sum(r.qt) from ("
                + "select DISTINCT it.id, it.quantidade as qt from item_de_venda as it, venda as v "
                + "where it.produto_id = ? and "
                + "v.dia between ? and ?) as r;";
        
        Query query = getEntityManager().createNativeQuery(stringQuery);
        query.setParameter(1, idProduto);
        query.setParameter(2, di);
        query.setParameter(3, df);

        try {
            double o = (double) query.getSingleResult();
            return o;
        } catch (NullPointerException ne) {
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
