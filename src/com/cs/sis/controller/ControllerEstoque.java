package com.cs.sis.controller;

import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.ItemDeVenda;
import com.cs.sis.model.pessoas.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.pessoas.exception.ProdutoABaixoDoEstoqueException;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ControllerEstoque extends ControllerEntity<Produto> {

	/*
	 * Produto
	 */
        @Override
	public void create(Produto produto) {
        try {
            beginTransaction();
            em.persist(produto);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    /**
     * @param produto
     * @throws EntidadeNaoExistenteException
     */
    @Override
    public void edit(Produto produto) throws EntidadeNaoExistenteException{
        try {
            beginTransaction();
            produto = em.merge(produto);
            commitTransaction();
        } catch (Exception ex) {
        	try{
                    em.find(Produto.class, produto.getId());
        	}catch(EntityNotFoundException enfe){
                    throw new EntidadeNaoExistenteException("O produto com código de barras " + produto.getCodigoDeBarras() + " não existe.");
        	}
            throw ex;
        } finally {
            closeEntityManager();
        }
    }

        @Override
    public void destroy(Produto produto) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                refresh(produto);
            } catch (EntityNotFoundException enfe) {
            	throw new EntidadeNaoExistenteException("O produto com código de barras " + produto.getCodigoDeBarras() + " não existe.");
            }
            em.remove(produto);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }    
    
    public double retirarItensDoEstoque(List<ItemDeVenda> list) throws EntidadeNaoExistenteException{
        double valor = 0;
        for (ItemDeVenda it : list) {
            try {
                valor += it.getTotal();
                it.getProduto().removerQuantidadeDeEstoque(it.getQuantidade());
            } catch (ProdutoABaixoDoEstoqueException e) {
                // colocar alguma mensagem no relatorio do final do dia
            } finally {
                edit(it.getProduto());
            }

        }
        return valor;
    }
    
    //metodos para testes
    @Override
    public void removeAll(Class t){
    	try {
            beginTransaction();
            em.createNativeQuery("DELETE FROM Produto WHERE id > 0;", t).executeUpdate();
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }
    
    /**
     *
     * @param t Class do objeto a ser removido
     * @return
     */
    @SuppressWarnings("unchecked")
        @Override
	public int cont(Class t) {
        em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(t);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public Produto refresh(Produto entity) {
        em = getEntityManager();
        entity = em.getReference(Produto.class, entity.getId());
        entity.getId();
        return entity;
    }
    
	
	
}
