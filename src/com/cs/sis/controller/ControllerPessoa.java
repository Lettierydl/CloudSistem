package com.cs.sis.controller;

import com.cs.sis.controller.find.FindVenda;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.Pagavel;
import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.Pessoa;
import com.cs.sis.model.pessoas.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.pessoas.exception.ParametrosInvalidosException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ControllerPessoa extends ControllerEntity<Pessoa> {

    /*
     * Cliente
     */
    public void create(Cliente cliente) {
        try {
            beginTransaction();
            em.persist(cliente);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void edit(Cliente cliente) throws EntidadeNaoExistenteException{
        try {
            beginTransaction();
            cliente = em.merge(cliente);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(Cliente.class, cliente.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("O cliente "
                        + cliente.getNome() + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    public void destroy(Cliente cliente) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                refresh(cliente);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("O cliente "
                        + cliente.getNome() + " não existe.");
            }
            em.remove(em.getReference(cliente.getClass(), cliente.getId()));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    /*
     * Funcionario
     */
    public void create(Funcionario funcionario) {
        try {
            beginTransaction();
            em.persist(funcionario);
	    commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void edit(Funcionario funcionario)
            throws EntidadeNaoExistenteException{
        try {
            beginTransaction();
            em.merge(funcionario);
            commitTransaction();
        } catch (Exception ex) {
            try {
                em.find(Funcionario.class, funcionario.getId());
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("O funcionário "
                        + funcionario.getNome() + " não existe.");
            }
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

    public void destroy(Funcionario funcionario)
            throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                refresh(funcionario);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException("A funcionário "
                        + funcionario.getNome() + " não existe.");
            }
            em.remove(funcionario);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    // metodos para testes
    public void removeAllCliente() {
        try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Cliente WHERE id > 0;",
                    Cliente.class).executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public void removeAllFuncionarios() {
        try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Funcionario WHERE id > 0;",
                    Funcionario.class).executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    public int getQuantidadeClientes() {
        try {
            @SuppressWarnings("rawtypes")
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    @SuppressWarnings("unchecked")
    public int getQuantidadeFuncionarios() {
        try {
            @SuppressWarnings("rawtypes")
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Funcionario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    public double recalcularDebitoDoCliente(Cliente c) throws EntidadeNaoExistenteException, Exception {
        List<Pagavel> di_ve = FindVenda.pagavelNaoPagoDoCliente(c);
        double deb_real = 0;
        for (Pagavel p : di_ve) {
            deb_real += p.getValorNaoPago();
        }
        deb_real = new BigDecimal(deb_real).setScale(2, RoundingMode.HALF_UP).doubleValue();
        c = getEntityManager().find(Cliente.class, c.getId());
        try {
            if (deb_real != c.getDebito()) {
                if (deb_real - c.getDebito() > 0.0) {
                    c.acrecentarDebito(new BigDecimal(deb_real - c.getDebito()).setScale(2,
                            RoundingMode.HALF_UP).doubleValue());
                } else {
                    c.diminuirDebito(new BigDecimal(c.getDebito() - deb_real).setScale(2,
                            RoundingMode.HALF_UP).doubleValue()
                    );
                }
                edit(c);
            }
        } catch (ParametrosInvalidosException e) {
            e.printStackTrace();
            throw e;
        }
        return c.getDebito();
    }

    @Override
    public Pessoa refresh(Pessoa entity) {
        em = getEntityManager();
        entity = em.getReference(entity.getClass(), entity.getId());
        entity.getId();
        return entity;
    }

    @Override
    public void create(Pessoa entity) throws Exception {
        if(entity instanceof Cliente){
            Cliente c = (Cliente)entity;
            create(c);
        }else if(entity instanceof Funcionario){
            Funcionario f = (Funcionario)entity;
            create(f);
        }
    }

    @Override
    public void edit(Pessoa entity) throws EntidadeNaoExistenteException {
        if(entity instanceof Cliente){
            Cliente c = (Cliente)entity;
            edit(c);
        }else if(entity instanceof Funcionario){
            Funcionario f = (Funcionario)entity;
            edit(f);
        }
    }

    @Override
    public void destroy(Pessoa entity) throws EntidadeNaoExistenteException {
        if(entity instanceof Cliente){
            Cliente c = (Cliente)entity;
            destroy(c);
        }else if(entity instanceof Funcionario){
            Funcionario f = (Funcionario)entity;
            destroy(f);
        }
    }

    @Override
    public int cont(Class e) {
        if(e.equals(Cliente.class)){
            return getQuantidadeClientes();
        }else if(e.equals(Funcionario.class)){
            return getQuantidadeFuncionarios();
        }
        return 0;
    }

    @Override
    public void removeAll(Class e) {
        if(e.equals(Cliente.class)){
            removeAllCliente();
        }else if(e.equals(Funcionario.class)){
            removeAllFuncionarios();
        }
    }

}
