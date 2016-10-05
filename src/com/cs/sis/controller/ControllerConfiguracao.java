package com.cs.sis.controller;

import static com.cs.sis.controller.ControllerEntity.getEntityManager;
import com.cs.sis.controller.configuracao.Configuracao;
import com.cs.sis.controller.configuracao.ConfiguracaoPK;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ControllerConfiguracao extends ControllerEntity<Configuracao> {
    /*
     * Configuracao
     */

    @Override
    public void create(Configuracao config) {
        try {
            beginTransaction();
            em.persist(config);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Configuracao config)
            throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            ConfiguracaoPK cpk = new ConfiguracaoPK();
            cpk.setChave(config.getChave());
            cpk.setTipoDeFuncionario(config.getTipoDeFuncionario());

            config = em.merge(config);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(Configuracao.class, config.getChave());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "A Configuração com código " + config.getChave()
                        + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void destroy(Configuracao config)
            throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                config = em.getReference(Configuracao.class, config.getChave());
                config.getChave();
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "A Configuracão com a chave " + config.getChave()
                        + " não existe.");
            }

            // remove das listas e atualiza entidades relacionadas
            em.remove(config);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void removeAll(Class e) {
        try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Configuracao;", e)
                    .executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public int cont(Class e) {
        try {
            getEntityManager();
            @SuppressWarnings("rawtypes")
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Configuracao.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    public boolean getValor(String chave, TipoDeFuncionario tipo)
            throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        try {
            String stringQuery = "select c FROM Configuracao as c ";
            stringQuery += "where c.chave = :chave and "
                    + "c.tipoDeFuncionario = :tipo ";

            Query query = getEntityManager().createQuery(stringQuery,
                    Configuracao.class);

            query.setParameter("tipo", tipo);
            query.setParameter("chave", chave);

            Configuracao con = (Configuracao) query.getSingleResult();
            return con.isValor();
        } catch (EntityNotFoundException ee) {
            Configuracao c = new Configuracao(chave, false, tipo);
            create(c);
            return false;
        } catch (NoResultException nr) {
            Configuracao c = new Configuracao(chave, false, tipo);
            create(c);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void putValor(String chave, boolean valor, TipoDeFuncionario tipo) {

        try {
            getEntityManager();
            ConfiguracaoPK cpk = new ConfiguracaoPK();
            cpk.setChave(chave);
            cpk.setTipoDeFuncionario(tipo);

            Configuracao con = em.getReference(Configuracao.class, cpk);
            con.setValor(valor);
            edit(con);
        } catch (Exception e) {
            Configuracao c = new Configuracao(chave, valor, tipo);
            create(c);
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public Configuracao refresh(Configuracao entity) {
        em = getEntityManager();
        ConfiguracaoPK cpk = new ConfiguracaoPK();
        cpk.setChave(entity.getChave());
        cpk.setTipoDeFuncionario(entity.getTipoDeFuncionario());
        entity = em.getReference(Configuracao.class, cpk);
        return entity;
    }
}
