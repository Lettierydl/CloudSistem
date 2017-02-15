package com.cs.sis.controller.find;

import com.cs.sis.controller.FindEntity;
import com.cs.sis.model.estoque.Produto;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FindProduto extends FindEntity {

    @SuppressWarnings("unchecked")
    public static List<Produto> produtosQueCodigoDeBarrasLike(
            String codigoDeBarras) {
        if (codigoDeBarras == null || codigoDeBarras.length() == 0) {
            throw new IllegalArgumentException(
                    "codigo de barras inválido");
        }
        codigoDeBarras = codigoDeBarras.replace('*', '%');
        if (codigoDeBarras.charAt(0) != '%') {
            codigoDeBarras = "%" + codigoDeBarras;
        }
        if (codigoDeBarras.charAt(codigoDeBarras.length() - 1) != '%') {
            codigoDeBarras = codigoDeBarras + "%";
        }

        Query q = getEntityManager()
                .createQuery(
                        "select o from Produto as o where LOWER(o.codigoDeBarras) LIKE LOWER(:codigoDeBarras)",
                        Produto.class);
        q.setParameter("codigoDeBarras", codigoDeBarras);
        List<Produto> produtos = q.getResultList();

        return produtos;
    }

    @SuppressWarnings("unchecked")
    public static List<Produto> produtosQueDescricaoLike(String descricao) {
        if (descricao == null || descricao.length() == 0) {
            throw new IllegalArgumentException(
                    "descrição inválida");
        }
        descricao = descricao.replace("%", "");
        descricao = descricao + "%";

        Query q = getEntityManager()
                .createQuery(
                        "SELECT o FROM Produto AS o WHERE LOWER(o.descricao) LIKE LOWER(:descricao) order by descricao",
                        Produto.class);
        q.setParameter("descricao", descricao);
        List<Produto> produtos = q.getResultList();
        return produtos;
    }

    @SuppressWarnings("unchecked")
    public static List<Produto> produtosQueCodigoDeBarrasInicia(
            String codigoDeBarras) {
        if (codigoDeBarras == null || codigoDeBarras.length() == 0) {
            throw new IllegalArgumentException(
                    "codigo de barras inválido");
        }
        codigoDeBarras = codigoDeBarras.replace('*', '%');
        if (codigoDeBarras.charAt(0) != '%') {
            codigoDeBarras += "%";
        }

        Query q = getEntityManager()
                .createQuery(
                        "select o from Produto as o where LOWER(o.codigoDeBarras) LIKE LOWER(:codigoDeBarras)",
                        Produto.class);
        q.setParameter("codigoDeBarras", codigoDeBarras);
        List<Produto> produtos = q.getResultList();
        return produtos;
    }

    @SuppressWarnings("unchecked")
    public static List<Produto> produtosQueDescricaoInicia(String descricao) {
        if (descricao == null || descricao.length() == 0) {
            throw new IllegalArgumentException(
                    "descrição inválida");
        }
        descricao = descricao.replace('*', '%');
        if (descricao.charAt(0) != '%') {
            descricao += "%";
        }

        Query q = getEntityManager()
                .createQuery(
                        "select o from Produto as o where LOWER(o.descricao) LIKE LOWER(:descricao)  order by o.descricao",
                        Produto.class);
        q.setParameter("descricao", descricao);
        List<Produto> produtos = q.getResultList();
        return produtos;
    }

    @SuppressWarnings("unchecked")
    public static List<Produto> produtosQueDescricaoOuCodigoDeBarrasIniciam(
            String descricaoOuCodigo) {
        String stringQuery = "select p FROM Produto as p where LOWER(p.descricao) LIKE LOWER(:descricao) "
                + "or LOWER(p.codigoDeBarras) LIKE LOWER(:codigoDeBarras) order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        query.setParameter("descricao", descricaoOuCodigo + "%");
        query.setParameter("codigoDeBarras", descricaoOuCodigo + "%");
        List<Produto> produtos = (List<Produto>) query.getResultList();
        return produtos;
    }
    @SuppressWarnings("unchecked")
    public static List<Produto> produtosQueDescricaoOuCodigoDeBarrasIniciam(
        String descricaoOuCodigo, int limit) {
        String stringQuery = "select p FROM Produto as p where LOWER(p.descricao) LIKE LOWER(:descricao) "
                + "or LOWER(p.codigoDeBarras) LIKE LOWER(:codigoDeBarras) order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        query.setParameter("descricao", descricaoOuCodigo + "%");
        query.setParameter("codigoDeBarras", descricaoOuCodigo + "%");
        query.setMaxResults(limit);
        List<Produto> produtos = (List<Produto>) query.getResultList();
        return produtos;
    }
    

    public static Produto produtoComCodigoDeBarras(String codigo) {

        String stringQuery = "select p FROM Produto as p where p.codigoDeBarras = :codigo ";
        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        query.setParameter("codigo", codigo);

        Produto produto = (Produto) query.getSingleResult();
        return produto;
    }

    public static Produto produtoComId(int id) {
        EntityManager em = getEntityManager();
        String stringQuery = "select p FROM Produto as p where p.id = :id  order by p.descricao";
        Query query = em.createQuery(stringQuery, Produto.class);
        query.setParameter("id", id);

        Produto produto = (Produto) query.getSingleResult();
        
        //pode calsar erro
        //em.getTransaction().begin();
        //em.refresh(produto);
        //em.getTransaction().commit();

        return produto;
    }

    public static List<Produto> todosProdutos() {
        String stringQuery = "select p FROM Produto as p  order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        @SuppressWarnings("unchecked")
        List<Produto> produtos = (List<Produto>) query.getResultList();
        return produtos;
    }
    
    public static List<Produto> todosProdutos(int limit) {
        String stringQuery = "select p FROM Produto as p  order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        query.setMaxResults(limit);
        @SuppressWarnings("unchecked")
        List<Produto> produtos = (List<Produto>) query.getResultList();
        return produtos;
    }

    public static List<Object[]> informacaoTodosProdutos() {
        String stringQuery = "SELECT id, descricao, codigoDeBarras, REPLACE( ROUND(valorDeVenda, 2), '.', ',')as valorDeVenda, CONCAT( REPLACE( ROUND(quantidadeEmEstoque, 3), '.', ',') , ' ',descricaoUnidade) as quantidadeEmEstoque  FROM produto order by descricao;";

        Query query = getEntityManager().createNativeQuery(stringQuery);
        Object o = null;
        try {
            o = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
        List<Object[]> produtos = (List<Object[]>) o;
        return produtos;
    }

    public static Produto produtoComCodigoEDescricao(String nomeOuCodigo) {

        String stringQuery = "select p FROM Produto as p where p.codigoDeBarras = :cod or p.descricao = :desc  order by p.descricao";
        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        query.setParameter("desc", nomeOuCodigo);
        query.setParameter("cod", nomeOuCodigo);

        Produto produto = (Produto) query.getSingleResult();
        return produto;
    }

    public static List<String> drecricaoProdutoQueIniciam(String descricao) {
        String stringQuery = "select p.descricao FROM Produto as p where LOWER(p.descricao) LIKE LOWER(:descricao)  order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, String.class);
        query.setParameter("descricao", descricao + "%");

        @SuppressWarnings("unchecked")
        List<String> descricoes = (List<String>) query.getResultList();
        return descricoes;
    }

    public static List<String> drecricaoProdutoQueIniciam(String descricao,
            int maxResult) {
        String stringQuery = "select p.descricao FROM Produto as p where LOWER(p.descricao) LIKE LOWER(:descricao)  order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, String.class);
        query.setParameter("descricao", descricao + "%");

        query.setMaxResults(maxResult);

        @SuppressWarnings("unchecked")
        List<String> descricoes = (List<String>) query.getResultList();
        return descricoes;
    }

    public static List<String> codigoProdutoQueIniciam(String codigo) {
        String stringQuery = "select p.codigoBarras FROM Produto as p where LOWER(p.codigoBarras) LIKE LOWER(:codigoBarras)  order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, String.class);
        query.setParameter("codigoBarras", codigo + "%");

        @SuppressWarnings("unchecked")
        List<String> codigos = (List<String>) query.getResultList();

        return codigos;
    }

    public static Produto produtoComDescricao(String descricao) {

        String stringQuery = "select p FROM Produto as p where p.descricao = :desc";
        Query query = getEntityManager().createQuery(stringQuery, Produto.class);
        query.setParameter("desc", descricao);

        Produto produto = (Produto) query.getSingleResult();

        return produto;
    }

    public static List<String> drecricaoEPrecoProdutoQueIniciam(
            String descricao, int maxResult) {
        String stringQuery = "select CONCAT(p.descricao, ' R$ ', REPLACE( ROUND(p.valorDeVenda, 2), '.', ',') ) FROM Produto as p where LOWER(p.descricao) LIKE LOWER(:descricao)  order by p.descricao";

        Query query = getEntityManager().createQuery(stringQuery, String.class);
        query.setParameter("descricao", descricao + "%");
        query.setMaxResults(maxResult);

        @SuppressWarnings("unchecked")
        List<String> descricoes = (List<String>) query.getResultList();

        return descricoes;

    }

    public static List<String> drecricaoEPrecoProdutoQueIniciam(String descricao) {
        String stringQuery = "select CONCAT(p.descricao, ' R$ ', REPLACE( ROUND(p.valorDeVenda, 2), '.', ',') ) FROM Produto as p where LOWER(p.descricao) LIKE LOWER(:descricao)  order by p.descricao";

        Query query = getEntityManager().createNativeQuery(stringQuery, String.class);
        query.setParameter("descricao", descricao + "%");

        @SuppressWarnings("unchecked")
        List<String> descricoes = (List<String>) query.getResultList();

        return descricoes;
    }
    
    public static List<String> drecricaoEPrecoProdutos() {
        String stringQuery = "select CONCAT(p.descricao, ' \nR$ ', REPLACE( ROUND(p.valorDeVenda, 2), '.', ',') ) FROM Produto as p  order by p.descricao";

        Query query = getEntityManager().createNativeQuery(stringQuery);

        @SuppressWarnings("unchecked")
        List<String> descricoes = (List<String>) query.getResultList();

        return descricoes;
    }
    
    

}
