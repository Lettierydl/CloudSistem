package com.cs.sis.controller.find;

import com.cs.sis.controller.FindEntity;
import com.cs.sis.model.pessoas.Cliente;
import java.util.List;
import javax.persistence.Query;

public class FindCliente extends FindEntity {

    @SuppressWarnings("unchecked")
    public static List<Cliente> clientesQueNomeLike(String nome) {
        if (nome == null || nome.length() == 0) {
            throw new IllegalArgumentException(
                    "nome inválido");
        }
        nome = nome.replace('*', '%');
        if (nome.charAt(0) != '%') {
            nome = "%" + nome;
        }
        if (nome.charAt(nome.length() - 1) != '%') {
            nome += "%";
        }
        Query q = getEntityManager()
                .createQuery(
                        "select o from Cliente as o where LOWER(o.nome) LIKE LOWER(:nome)",
                        Cliente.class);
        q.setParameter("nome", nome);
        List<Cliente> clientes = q.getResultList();

        return clientes;
    }

    @SuppressWarnings("unchecked")
    public static List<Cliente> clientesQueCPFLike(String cpf) {
        if (cpf == null || cpf.length() == 0) {
            throw new IllegalArgumentException(
                    "cpf inválido");
        }
        cpf = cpf.replace('*', '%');
        if (cpf.charAt(0) != '%') {
            cpf = "%" + cpf;
        }
        if (cpf.charAt(cpf.length() - 1) != '%') {
            cpf += "%";
        }

        Query q = getEntityManager()
                .createQuery(
                        "SELECT o FROM Cliente AS o WHERE LOWER(o.cpf) LIKE LOWER(:cpf)",
                        Cliente.class);
        q.setParameter("cpf", cpf.replace(" ", "%"));
        List<Cliente> clientes = q.getResultList();

        return clientes;
    }

    @SuppressWarnings("unchecked")
    public static List<Cliente> clientesQueNomeInicia(String nome) {
        if (nome == null || nome.length() == 0) {
            throw new IllegalArgumentException(
                    "nome inválido");
        }
        nome = nome.replace('*', '%');
        if (nome.charAt(0) != '%') {
            nome += "%";
        }

        Query q = getEntityManager()
                .createQuery(
                        "select o from Cliente as o where LOWER(o.nome) LIKE LOWER(:nome)",
                        Cliente.class);
        q.setParameter("nome", nome);
        List<Cliente> Clientes = q.getResultList();

        return Clientes;
    }

    @SuppressWarnings("unchecked")
    public static List<Cliente> clientesQueCPFInicia(String cpf) {
        if (cpf == null || cpf.length() == 0) {
            throw new IllegalArgumentException(
                    "cpf inválido");
        }
        cpf = cpf.replace('*', '%');
        if (cpf.charAt(0) != '%') {
            cpf += "%";
        }

        Query q = getEntityManager()
                .createQuery(
                        "select o from Cliente as o where LOWER(o.cpf) LIKE LOWER(:cpf)",
                        Cliente.class);
        q.setParameter("cpf", cpf);
        List<Cliente> Clientes = q.getResultList();

        return Clientes;
    }
    
    @SuppressWarnings("unchecked")
    public static List<Cliente> clientesQueNomeOuCPFIniciam(String nomeOuCpf) {
        String stringQuery = "select c FROM Cliente as c ";
        stringQuery += "where LOWER(c.nome) LIKE LOWER(:nome) or "
                + "LOWER(c.cpf) LIKE LOWER(:cpf) order by c.nome, c.cpf";

        Query query = getEntityManager().createQuery(stringQuery, Cliente.class);

        nomeOuCpf = nomeOuCpf + "%";
        query.setParameter("nome", nomeOuCpf);
        query.setParameter("cpf", nomeOuCpf);

        List<Cliente> Clientes = (List<Cliente>) query.getResultList();

        return Clientes;
    }

    public static List<Cliente> clientesQueNomeOuCPFIniciam(String nomeOuCpf,
            int maxResult) {
        String stringQuery = "select c FROM Cliente as c ";
        stringQuery += "where LOWER(c.nome) LIKE LOWER(:nome) or "
                + "LOWER(c.cpf) LIKE LOWER(:cpf) order by c.nome, c.cpf";

        Query query = getEntityManager().createQuery(stringQuery, Cliente.class);

        nomeOuCpf += "%";
        query.setParameter("nome", nomeOuCpf);
        query.setParameter("cpf", nomeOuCpf);

        query.setMaxResults(maxResult);

        @SuppressWarnings("unchecked")
        List<Cliente> Clientes = (List<Cliente>) query.getResultList();

        return Clientes;
    }

    public static Cliente clientesQueNomeOuCPFIqualA(String nomeOuCpf) {
        String stringQuery = "select c FROM Cliente as c ";
        stringQuery += "where LOWER(c.nome) = LOWER(:nome) or "
                + "LOWER(c.cpf) = LOWER(:cpf)";

        Query query = getEntityManager().createQuery(stringQuery, Cliente.class);

        query.setParameter("nome", nomeOuCpf);
        query.setParameter("cpf", nomeOuCpf);

        Cliente cliente = (Cliente) query.getSingleResult();
        return cliente;
    }

    public static List<String> nomeClientesQueNomeInicia(String nome) {
        String stringQuery = "select c.nome FROM Cliente as c ";
        stringQuery += "where LOWER(c.nome) LIKE LOWER(:nome) ";

        Query query = getEntityManager().createQuery(stringQuery, String.class);

        nome += "%";
        query.setParameter("nome", nome);

        @SuppressWarnings("unchecked")
        List<String> nomes = (List<String>) query.getResultList();
        return nomes;
    }

    @SuppressWarnings("unchecked")
    public static List<Cliente> listClientes() {

        Query consulta = getEntityManager()
                .createQuery("select c from Cliente c order by c.nome");
        List<Cliente> clientes = consulta.getResultList();
        return clientes;
    }
    
    @SuppressWarnings("unchecked")
    public static List<Cliente> listClientes(int limit) {

        Query consulta = getEntityManager()
                .createQuery("select c from Cliente c order by c.nome");
        consulta.setMaxResults(limit);
        List<Cliente> clientes = consulta.getResultList();
        return clientes;
    }

    public static Cliente clienteComId(int idCliente) {
        Query consulta = getEntityManager().createQuery(
                "select c from Cliente as c where c.id = :idCliente ",
                Cliente.class);
        consulta.setParameter("idCliente", idCliente);
        Cliente c = (Cliente) consulta.getSingleResult();
        return c;
    }

    public static Cliente clienteComNome(String nome) {
        if (nome == null || nome.length() == 0) {
            throw new IllegalArgumentException(
                    "nome inválido");
        }
        Query q = getEntityManager()
                .createQuery(
                        "select o from Cliente as o where LOWER(o.nome) = LOWER(:nome)",
                        Cliente.class);
        q.setParameter("nome", nome);
        Cliente cliente = (Cliente) q.getSingleResult();
        return cliente;
    }

    public static Cliente clienteComCPF(String cpf) {
        if (cpf == null || cpf.length() == 0) {
            throw new IllegalArgumentException(
                    "nome inválido");
        }
        Query q = getEntityManager().createQuery(
                "select o from Cliente as o where LOWER(o.cpf) = LOWER(:cpf)",
                Cliente.class);
        q.setParameter("cpf", cpf);
        Cliente cliente = (Cliente) q.getSingleResult();

        return cliente;
    }

}
