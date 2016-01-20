package com.cs.sis.controller;

import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.Pagamento;
import com.cs.sis.model.pessoas.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.pessoas.exception.ParametrosInvalidosException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ControllerPagamento extends ControllerEntity<Pagamento> {


    /*x
     * Pagamento
     */
    @Override
    public void create(Pagamento pagamento) throws ParametrosInvalidosException {
        regrasDeUmPagamento(pagamento);
        try {
            beginTransaction();
            em.persist(pagamento);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Pagamento pagamento) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(pagamento);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(Pagamento.class, pagamento.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Pagamento com código " + pagamento.getId()
                        + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    public void destroy(Pagamento pagamento)
            throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                refresh(pagamento);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Pagamento com código " + pagamento.getId()
                        + " não existe.");
            }
            em.remove(pagamento);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void removeAll(Class e) {
        try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Pagamento WHERE id > 0;",
                    e).executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    /**
     *
     * @param p
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public int cont(Class p) {
        try {
            @SuppressWarnings("rawtypes")
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(p);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    private void regrasDeUmPagamento(Pagamento p) throws ParametrosInvalidosException {
        if (p.getValor() < 0) {//pagamentos nao podem ter o valor negativo
            throw new ParametrosInvalidosException("Pagamento com valor negativo");
        } else if (p.getCliente() == null) {
            throw new ParametrosInvalidosException("Pagamento sem cliente");
        }

    }

    @Override
    public Pagamento refresh(Pagamento entity) {
        em = getEntityManager();
        entity = em.getReference(Pagamento.class, entity.getId());
        entity.getId();
        return entity;
    }

}
