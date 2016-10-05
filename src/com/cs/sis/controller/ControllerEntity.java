/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.controller;

import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import com.cs.sis.util.Arquivo;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author Lettiery
 */
public abstract class ControllerEntity<T> {

    public static final String UNIDADE_DE_PERSISTENCIA = "CloudSistemPU";
    private static final EntityManagerFactory emf = createEntityManagerFactory();
    static EntityManager em;

    private static EntityManagerFactory createEntityManagerFactory() {
        EntityManagerFactory factory;
        Arquivo a = new Arquivo();
        if (a.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.EXTRATEGIA_DE_CONEXAO).toString().equalsIgnoreCase("local")) {
            System.out.println("Conectando ao MYSQL pelo localhost ");
            factory = Persistence.createEntityManagerFactory(UNIDADE_DE_PERSISTENCIA);
        } else {
            Properties props = new Properties();
            System.out.println("Conectando ao MYSQL por: ");
            String url = "jdbc:mysql://" + a.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.IP_DO_BANCO).toString() + ":3306/cloudsistem";
            System.out.println(url);
            props.setProperty("javax.persistence.jdbc.url", url);
            factory = Persistence.createEntityManagerFactory(UNIDADE_DE_PERSISTENCIA, props);
        }

        return factory;
    }

    static EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        return em;
    }

    protected void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    protected void beginTransaction() {
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (transaction.isActive()) {
            commitTransaction();
        }
        transaction.begin();
    }

    protected void commitTransaction() {
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    public abstract void create(T entity) throws Exception;

    public abstract void edit(T entity) throws EntidadeNaoExistenteException;

    public abstract void destroy(T entity) throws EntidadeNaoExistenteException;

    public abstract T refresh(T entity);

    public abstract int cont(Class e);

    public abstract void removeAll(Class e);

}
