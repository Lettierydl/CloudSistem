/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.model.pessoas;

import com.cs.sis.util.OperacaoStringUtil;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "funcionario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Funcionario.findAll", query = "SELECT f FROM Funcionario f"),
    @NamedQuery(name = "Funcionario.findById", query = "SELECT f FROM Funcionario f WHERE f.id = :id"),
    @NamedQuery(name = "Funcionario.findByCelular", query = "SELECT f FROM Funcionario f WHERE f.celular = :celular"),
    @NamedQuery(name = "Funcionario.findByCpf", query = "SELECT f FROM Funcionario f WHERE f.cpf = :cpf"),
    @NamedQuery(name = "Funcionario.findByEndereco", query = "SELECT f FROM Funcionario f WHERE f.endereco = :endereco"),
    @NamedQuery(name = "Funcionario.findByLogin", query = "SELECT f FROM Funcionario f WHERE f.login = :login"),
    @NamedQuery(name = "Funcionario.findByNome", query = "SELECT f FROM Funcionario f WHERE f.nome = :nome"),
    @NamedQuery(name = "Funcionario.findBySenha", query = "SELECT f FROM Funcionario f WHERE f.senha = :senha"),
    @NamedQuery(name = "Funcionario.findByTelefone", query = "SELECT f FROM Funcionario f WHERE f.telefone = :telefone"),
    @NamedQuery(name = "Funcionario.findByTipoDeFuncionario", query = "SELECT f FROM Funcionario f WHERE f.tipoDeFuncionario = :tipoDeFuncionario")})
public class Funcionario implements Serializable, Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;

    /**
     */
    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    /**
     */
    @Column(nullable = false)
    private String senha;

    /**
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDeFuncionario tipoDeFuncionario;

    /**
     */
    @Column(nullable = true, length = 11, unique = true)
    private String cpf;

    /**
     */
    @Column(nullable = true, length = 15)
    private String telefone;

    @Column(nullable = true, length = 15)
    private String celular;

    @Column(nullable = true, length = 500)
    private String endereco;

    /*
     @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "funcionario")
     private List<Pagamento> pagamentos = new ArrayList<Pagamento>();
     */
    
    
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTelefone() {
        return OperacaoStringUtil.formatarStringParaMascaraDeTelefone(telefone);
    }

    public void setTelefone(String telefone) {
        this.telefone = OperacaoStringUtil.retirarMascaraDeTelefone(telefone);
    }

    public String getCelular() {
        return OperacaoStringUtil.formatarStringParaMascaraDeTelefone(celular);
    }

    public void setCelular(String celular) {
        this.celular = OperacaoStringUtil.retirarMascaraDeTelefone(celular);
    }
    
    public String getTelefones(){
        return getTelefone() + "\n"+getCelular();
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoDeFuncionario getTipoDeFuncionario() {
        return tipoDeFuncionario;
    }

    public void setTipoDeFuncionario(TipoDeFuncionario tipoDeFuncionario) {
        this.tipoDeFuncionario = tipoDeFuncionario;
    }

    public String getCpf() {
        return OperacaoStringUtil.formatarStringParaMascaraDeCPF(cpf);
    }

    public void setCpf(String cpf) {
        this.cpf = OperacaoStringUtil.retirarMascaraDeCPF(cpf);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((senha == null) ? 0 : senha.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Funcionario other = (Funcionario) obj;
        if (login == null) {
            if (other.login != null) {
                return false;
            }
        } else if (!login.equals(other.login)) {
            return false;
        }
        if (nome == null) {
            if (other.nome != null) {
                return false;
            }
        } else if (!nome.equals(other.nome)) {
            return false;
        }
        if (senha == null) {
            if (other.senha != null) {
                return false;
            }
        } else if (!senha.equals(other.senha)) {
            return false;
        }
        return true;
    }

}
