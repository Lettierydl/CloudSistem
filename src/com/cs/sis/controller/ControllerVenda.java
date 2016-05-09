package com.cs.sis.controller;

import com.cs.sis.controller.find.FindVenda;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.Divida;
import com.cs.sis.model.financeiro.FormaDePagamento;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.financeiro.Venda;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.pessoas.exception.EstadoInvalidoDaVendaAtualException;
import com.cs.sis.model.pessoas.exception.VariasVendasPendentesException;
import com.cs.sis.model.pessoas.exception.VendaPendenteException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ControllerVenda extends ControllerEntity<Pagavel> {

    private static Venda atual;

    /*
     * Venda
     */
    public void create(Venda Venda) {
        try {
            beginTransaction();
            em.persist(Venda);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    /*
     * Divida
     */
    public void create(Divida divida) {
        try {
            beginTransaction();
            em.persist(divida);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void edit(Venda venda) throws EntidadeNaoExistenteException {
        try {
            for (ItemDeVenda it : venda.getItensDeVenda()) {
                if (it.getId() == 0) {
                    create(it);
                }
            }
            beginTransaction();
            venda = em.merge(venda);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(Venda.class, venda.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("A Venda com código "
                        + venda.getId() + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    public void edit(Divida divida) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(divida);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(Divida.class, divida.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("A Divida com código "
                        + divida.getId() + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    public void edit(ItemDeVenda item) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.find(ItemDeVenda.class, item.getId());

            em.merge(item);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(ItemDeVenda.class, item.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Item de venda com o código " + item.getId()
                        + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    public void destroy(Venda venda) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                refresh(venda);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("A Venda com código "
                        + venda.getId() + " não existe.");
            }
            em.remove(em.getReference(venda.getClass(), venda.getId()));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void destroy(Divida divida) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                refresh(divida);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("A Divida com código "
                        + divida.getId() + " não existe.");
            }

            em.remove(em.getReference(divida.getClass(), divida.getId()));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void destroy(ItemDeVenda item) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                item = em.getReference(ItemDeVenda.class, item.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Item de venda com código " + item.getId()
                        + " não existe.");
            }

            try {

                em.remove(em.getReference(ItemDeVenda.class, item.getId()));
                /*Query q = em.createNativeQuery("DELETE FROM item_de_venda WHERE id = :idItem ", ItemDeVenda.class);
                 q.setParameter("idItem", item.getId());
                 */
                commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            closeEntityManager();
        }
    }

    private void create(ItemDeVenda item) {
        try {
            beginTransaction();
            em.persist(item);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void removeAllVendas() {
        try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Venda WHERE id > 0;", Venda.class)
                    .executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void removeAllDividas() {
        try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Divida WHERE id > 0;", Divida.class)
                    .executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public int getQuantidadeVendas() {
        try {
            beginTransaction();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Venda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public int getQuantidadeDividas() {
        try {
            beginTransaction();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Divida.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    /**
     * Verifica quais vendas podem ser pagas<br/>
     * Se não puder pagar uma venda completa é acrescentado uma parte paga na
     * venda
     *
     * @see ControllerPessoa.edit(Cliente c) deve ser chamado logo em seguida
     * para atualizar o debito do cliente
     * @param Pagamento que foi efetuado
     * @throws Exception
     * @throws EntidadeNaoExistenteException se venda não existir
     */
    public void abaterValorDoPagamentoNaVenda(Pagamento p)
            throws EntidadeNaoExistenteException, Exception {
        Cliente c = p.getCliente();
        double valorRestante = p.getValor();
        List<Pagavel> pagaveis = new ArrayList<Pagavel>();
        pagaveis.addAll(FindVenda.dividasNaoPagaDoCliente(c));
        pagaveis.addAll(FindVenda.vendasNaoPagaDoCliente(c));

        for (Pagavel pag : pagaveis) {
            if (valorRestante == 0) {// ja pagou a venda
                break;
            }
            if (pag.getValorNaoPago() > valorRestante) {// paga parte da
                // venda
                pag.acrescentarPartePaga(valorRestante);
                valorRestante = 0;
            } else {// paga venda toda, pode sobrar resto pra outras vendas ou
                // nao
                valorRestante -= pag.getValorNaoPago();
                pag.acrescentarPartePaga(pag.getValorNaoPago());
            }
            if (pag instanceof Venda) {
                edit((Venda) pag);
            } else if (pag instanceof Divida) {
                edit((Divida) pag);
            }
        }


        // logo em seguida deve diminuir o debito do cliente com o valor do
        // Venda
    }

    public void atualizarStatusPagaveis(List<Pagavel> pagaveis) throws EntidadeNaoExistenteException{
        for (Pagavel pag : pagaveis) {
            if (!pag.isPaga() && new BigDecimal(pag.getValorNaoPago()).setScale(2, RoundingMode.HALF_UP).doubleValue() == 0) {
                if (pag instanceof Venda) {
                    Venda v = ((Venda) pag);
                    v.setPaga(true);
                    edit(v);
                } else if (pag instanceof Divida) {
                    Divida d = ((Divida) pag);
                    d.setPaga(true);
                    edit(d);
                }
            }
        }
    }
    
    /**
     *
     * @throws VendaPendenteException se existir uma venda atual sem ser
     * encerrada
     */
    public void iniciarNovaVenda() throws VendaPendenteException {
        if (atual == null) {
            atual = new Venda();
            atual.setFuncionario(ControllerLogin.logado);
            atual.setDia(Calendar.getInstance());
            create(atual);
        } else {
            throw new VendaPendenteException();
        }
    }

    /**
     *
     * @exception EntidadeNaoExistenteException se a venda atual for vazia
     */
    public void finalizarVendaAVista(Venda v)
            throws EntidadeNaoExistenteException, Exception {
        if (v.getFormaDePagamento() != null) {
            throw new EstadoInvalidoDaVendaAtualException("Venda já finalizada anteriormente, inicie uma nova venda");
        }
        v.setFormaDePagamento(FormaDePagamento.A_Vista);
        v.setPaga(true);
        v.setPartePaga(v.getTotal());
        edit(v);
        atual = null;
    }

    public double finalizarVendaAPrazo(Cliente c, double partePaga) throws EntidadeNaoExistenteException, EstadoInvalidoDaVendaAtualException, Exception {
        return finalizarVendaAPrazo(atual, c, partePaga);
    }

    // retorna o que deve ser acrecentado a conta do cliente
    public double finalizarVendaAPrazo(Venda v, Cliente c,
            double partePaga) throws EntidadeNaoExistenteException, EstadoInvalidoDaVendaAtualException, Exception {
        try {
            v = em.getReference(Venda.class, v.getId());
        } catch (Exception e) {
        }
        if (v.getFormaDePagamento() != null) {
            throw new EstadoInvalidoDaVendaAtualException("Venda já finalizada anteriormente, inicie uma nova venda");
        }
        v.setFormaDePagamento(FormaDePagamento.A_Prazo);
        v.setPaga(false);
        v.setPartePaga(partePaga);
        v.setCliente(c);
        edit(v);
        double credito = v.getTotal() - v.getPartePaga();
        atual = null;
        if (credito < 0) {
            System.err.println("venda com valor errado");
            return 0;
        } else {
            return credito;
        }
    }

    public Venda recuperarVendaPendente(Funcionario logado) throws EntidadeNaoExistenteException,
            Exception {
        List<Venda> pendentes = FindVenda
                .getVendasNaoFinalizadasPorFuncionario(logado);
        if (pendentes.size() == 1) {
            atual = pendentes.get(0);
            return pendentes.get(0);
        } else if (pendentes.size() > 1) {
            List<Venda> newPendentes = new ArrayList<Venda>();
            for (Venda p : pendentes) {
                newPendentes.add(p);
            }
            throw new VariasVendasPendentesException(newPendentes);
        } else {
            throw new EntidadeNaoExistenteException();
        }
    }

    public void setAtualComVendaPendenteTemporariamente() {
        Venda melhor_escolha = null;
        List<Venda> pendentes = FindVenda
                .getVendasNaoFinalizadasPorFuncionario(ControllerLogin.logado);
        for (Venda v : pendentes) {
            if (melhor_escolha == null) {
                melhor_escolha = v;
            } else if (melhor_escolha.getTotal() > v.getTotal()) {
                melhor_escolha = v;
            }
        }
        atual = melhor_escolha;
    }

    public void selecionarVendaPendente(int id_venda)
            throws EntidadeNaoExistenteException, Exception {
        List<Venda> pendentes = FindVenda
                .getVendasNaoFinalizadasPorFuncionario(ControllerLogin.logado);
        for (Venda v : pendentes) {
            if (id_venda == v.getId()) {
                atual = v;
                atual.setDia(Calendar.getInstance());
                edit(atual);
                return;
            }
        }
    }

    public void removerVendaPendente(Venda v)
            throws EntidadeNaoExistenteException {
        List<Venda> pendentes = FindVenda
                .getVendasNaoFinalizadasPorFuncionario(ControllerLogin.logado);
        if (pendentes.contains(v)) {
            destroy(v);
        } else {
            System.err.println("Não consegui remover a venda pendente");
        }
    }

    public Venda getAtual() {
        refreshAtual();
        return atual;
    }

    public void atualizarDataVendaAtual() throws EntidadeNaoExistenteException {
        atual.setDia(Calendar.getInstance());
        this.edit(atual);
    }

    public void addItem(ItemDeVenda it) throws EntidadeNaoExistenteException {
        this.refreshAtual();

        atual.addItemDeVenda(it);
        edit(atual);
    }

    public void removerItem(ItemDeVenda it)
            throws EntidadeNaoExistenteException, Exception {

        try {
            refreshAtual();
            it.setVenda(null);
            edit(it);
            try {
                destroy(it);
            } catch (Exception ee) {
                ee.printStackTrace();
                // item perdido
            }
        } catch (Exception e) {
            throw e;
        }
        refreshAtual();
        atual.removeItemDeVendaJaDeletado(it);
        edit(atual);

    }

    public void removerVenda(Pagavel p)
            throws EntidadeNaoExistenteException, Exception {

        try {
            destroy(p);
        } catch (Exception e) {
            throw e;
        }

    }

    public void refreshValorVendaAtual() throws EntidadeNaoExistenteException, Exception {
        refreshAtual();
        atual.recalcularTotal();
        edit(atual);
        refreshAtual();
    }

    private void refreshAtual() {
        try {
            beginTransaction();
            atual = em.find(Venda.class, atual.getId());
            em.refresh(atual);
            commitTransaction();
        } catch (EntityNotFoundException enfe) {
        } catch (Exception e) {
        } finally {
            closeEntityManager();
        }

    }

    public int limparBancoDeDados(Date data) {
        int antes = getQuantidadeVendas();
        try {
            beginTransaction();
            Query q = em
                    .createNativeQuery(
                            "DELETE FROM venda WHERE venda.dia < :data and venda.paga = true;",
                            Venda.class);
            q.setParameter("data", data);
            q.executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
        int removidas = antes - getQuantidadeVendas();
        return (removidas * 100) / antes;
    }

    @Override
    public void create(Pagavel entity) {
        if (entity instanceof Venda) {
            Venda v = (Venda) entity;
            create(v);
        } else if (entity instanceof Divida) {
            Divida d = (Divida) entity;
            create(d);
        }
    }

    @Override
    public void edit(Pagavel entity) throws EntidadeNaoExistenteException {
        if (entity instanceof Venda) {
            Venda v = (Venda) entity;
            edit(v);
        } else if (entity instanceof Divida) {
            Divida d = (Divida) entity;
            edit(d);
        }
    }

    @Override
    public void destroy(Pagavel entity) throws EntidadeNaoExistenteException {
        if (entity instanceof Venda) {
            Venda v = (Venda) entity;
            destroy(v);
        } else if (entity instanceof Divida) {
            Divida d = (Divida) entity;
            destroy(d);
        }
    }

    @Override
    public Pagavel refresh(Pagavel entity) {
        em = getEntityManager();
        entity = em.getReference(entity.getClass(), entity.getId());
        entity.getId();
        return entity;
    }

    @Override
    public int cont(Class e) {
        if (e.equals(Venda.class)) {
            return getQuantidadeVendas();
        } else if (e.equals(Divida.class)) {
            return getQuantidadeDividas();
        }
        return 0;
    }

    @Override
    public void removeAll(Class e) {
        if (e.equals(Venda.class)) {
            removeAllVendas();
        } else if (e.equals(Divida.class)) {
            removeAllDividas();
        }
    }

}
