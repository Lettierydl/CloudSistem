package com.cs.sis.controller.find;

import com.cs.sis.controller.FindEntity;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.pessoas.Cliente;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class FindPagamento extends FindEntity {

    @SuppressWarnings("unchecked")
    public static List<Pagamento> pagamentosDeHoje() {

        String stringQuery = "select p FROM Pagamento as p ";
        stringQuery += "WHERE day(p.data) = day(curdate()) and p.data >= curdate()"
                + " order by p.data DESC";

        Query query = getEntityManager().createQuery(stringQuery, Pagamento.class);

        List<Pagamento> pagamentos = (List<Pagamento>) query.getResultList();

        return pagamentos;
    }

    public static List<Pagamento> pagamentosDoCliente(Cliente cliente, Date diaInicio,
            Date diaFim) {

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

        String stringQuery = "select p FROM Pagamento as p ";
        stringQuery += "WHERE p.cliente = :cli and p.data between :diaInicio and :diaFim"
                + " order by p.data DESC";

        Query query = getEntityManager().createQuery(stringQuery, Pagamento.class);

        query.setParameter("cli", cliente);
        query.setParameter("diaInicio", di);
        query.setParameter("diaFim", df);

        @SuppressWarnings("unchecked")
        List<Pagamento> pagamentos = (List<Pagamento>) query.getResultList();

        return pagamentos;
    }

    public static List<Pagamento> pagamentosDoCliente(Cliente cliente) {

        String stringQuery = "select p FROM Pagamento as p ";
        stringQuery += "WHERE p.cliente = :cli"
                + " order by p.data DESC";

        Query query = getEntityManager().createQuery(stringQuery, Pagamento.class);

        query.setParameter("cli", cliente);

        @SuppressWarnings("unchecked")
        List<Pagamento> pagamentos = (List<Pagamento>) query.getResultList();

        return pagamentos;
    }

    public static List<Pagamento> pagamentosDosClientes(List<Cliente> clientes) {
        //vai rodando pelo cliente_id //for clientes cli{ p.cliente = cli or }
        String stringQuery = "select p FROM Pagamento as p ";
        stringQuery += "WHERE (p.cliente != NULL) ";
        for (int i = 0; i < clientes.size(); i++) {
            if (i > 0) {
                stringQuery += "or p.cliente = :cli" + i + " ";
            } else {
                stringQuery += "and ( p.cliente = :cli" + i + " ";
            }
        }

        stringQuery += ") order by p.data DESC";

        Query query = getEntityManager().createQuery(stringQuery, Pagamento.class);

        for (int i = 0; i < clientes.size(); i++) {
            query.setParameter("cli" + i, clientes.get(i));
        }

        @SuppressWarnings("unchecked")
        List<Pagamento> pagamentos = (List<Pagamento>) query.getResultList();

        return pagamentos;
    }

    public static Pagamento pagamentoId(int id) {

        String stringQuery = "select p FROM Pagamento as p where p.id = :id";

        Query query = getEntityManager().createQuery(stringQuery, Pagamento.class);
        query.setParameter("id", id);

        Pagamento pagamento = (Pagamento) query.getSingleResult();

        return pagamento;
    }

}
