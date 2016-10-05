package com.cs.sis.model.estoque;

import com.cs.sis.model.exception.ProdutoABaixoDoEstoqueException;
import java.io.Serializable;
import java.util.Objects;
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
@Table(name = "produto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Produto.findAll", query = "SELECT p FROM Produto p"),
    @NamedQuery(name = "Produto.findById", query = "SELECT p FROM Produto p WHERE p.id = :id"),
    @NamedQuery(name = "Produto.findByCategoria", query = "SELECT p FROM Produto p WHERE p.categoria = :categoria"),
    @NamedQuery(name = "Produto.findByCodigoDeBarras", query = "SELECT p FROM Produto p WHERE p.codigoDeBarras = :codigoDeBarras"),
    @NamedQuery(name = "Produto.findByDescricao", query = "SELECT p FROM Produto p WHERE p.descricao = :descricao"),
    @NamedQuery(name = "Produto.findByDescricaoUnidade", query = "SELECT p FROM Produto p WHERE p.descricaoUnidade = :descricaoUnidade"),
    @NamedQuery(name = "Produto.findByLimiteMinimoEmEstoque", query = "SELECT p FROM Produto p WHERE p.limiteMinimoEmEstoque = :limiteMinimoEmEstoque"),
    @NamedQuery(name = "Produto.findByQuantidadeEmEstoque", query = "SELECT p FROM Produto p WHERE p.quantidadeEmEstoque = :quantidadeEmEstoque"),
    @NamedQuery(name = "Produto.findByValorDeCompra", query = "SELECT p FROM Produto p WHERE p.valorDeCompra = :valorDeCompra"),
    @NamedQuery(name = "Produto.findByValorDeVenda", query = "SELECT p FROM Produto p WHERE p.valorDeVenda = :valorDeVenda")})
public class Produto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;

    @Column(nullable = false, unique = true, length = 13)
    private String codigoDeBarras;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false, precision = 2)
    private double valorDeCompra;

    /**
     */
    @Column(nullable = false, precision = 2)
    private double valorDeVenda;

    /**
     */
    @Column(nullable = false, precision = 4)
    private double quantidadeEmEstoque;

    /**
     */
    @Column(nullable = false, precision = 4)
    private double limiteMinimoEmEstoque;

    /**
     */
    @Enumerated(EnumType.STRING)
    private CategoriaProduto categoria = CategoriaProduto.Outra;

    @Enumerated(EnumType.STRING)
    private UnidadeProduto descricaoUnidade;

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValorDeCompra() {
        return valorDeCompra;
    }

    public void setValorDeCompra(double valorDeCompra) {
        this.valorDeCompra = valorDeCompra;
    }

    public double getValorDeVenda() {
        return valorDeVenda;
    }

    public void setValorDeVenda(double valorDeVenda) {
        this.valorDeVenda = valorDeVenda;
    }

    public double getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public void setQuantidadeEmEstoque(double quantidadeEmEstoque) {
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    public double getLimiteMinimoEmEstoque() {
        return limiteMinimoEmEstoque;
    }

    public void setLimiteMinimoEmEstoque(double limiteMinimoEmEstoque) {
        this.limiteMinimoEmEstoque = limiteMinimoEmEstoque;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria( CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public UnidadeProduto getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(UnidadeProduto descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    //esse metododeve ser chamado apenas pelo controller venda
    public void removerQuantidadeDeEstoque(double quantidade) throws ProdutoABaixoDoEstoqueException {
        this.quantidadeEmEstoque -= quantidade;
        if (this.quantidadeEmEstoque > 0) {
            throw new ProdutoABaixoDoEstoqueException();
        }
    }
    public void acrescentarQuantidadeDeEstoque(double quantidade) {
        this.quantidadeEmEstoque += quantidade;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((codigoDeBarras == null) ? 0 : codigoDeBarras.hashCode());
        result = prime * result + id;
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
        Produto other = (Produto) obj;
        if (codigoDeBarras == null) {
            if (other.codigoDeBarras != null) {
                return false;
            }
        } else if (!codigoDeBarras.equals(other.codigoDeBarras)) {
            return false;
        }
        return id == other.id;
    }
    
    
    public boolean equalsAtributes(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Produto other = (Produto) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.codigoDeBarras, other.codigoDeBarras)) {
            return false;
        }
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valorDeCompra) != Double.doubleToLongBits(other.valorDeCompra)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valorDeVenda) != Double.doubleToLongBits(other.valorDeVenda)) {
            return false;
        }
        if (Double.doubleToLongBits(this.quantidadeEmEstoque) != Double.doubleToLongBits(other.quantidadeEmEstoque)) {
            return false;
        }
        if (Double.doubleToLongBits(this.limiteMinimoEmEstoque) != Double.doubleToLongBits(other.limiteMinimoEmEstoque)) {
            return false;
        }
        if (this.categoria != other.categoria) {
            return false;
        }
        if (this.descricaoUnidade != other.descricaoUnidade) {
            return false;
        }
        return true;
    }

    

}
