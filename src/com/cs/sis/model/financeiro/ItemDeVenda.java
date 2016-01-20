/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.model.financeiro;

import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.estoque.UnidadeProduto;
import com.cs.sis.util.OperacaoStringUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "item_de_venda")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemDeVenda.findAll", query = "SELECT i FROM ItemDeVenda i"),
    @NamedQuery(name = "ItemDeVenda.findById", query = "SELECT i FROM ItemDeVenda i WHERE i.id = :id"),
    @NamedQuery(name = "ItemDeVenda.findByDesconto", query = "SELECT i FROM ItemDeVenda i WHERE i.desconto = :desconto"),
    @NamedQuery(name = "ItemDeVenda.findByIndice", query = "SELECT i FROM ItemDeVenda i WHERE i.indice = :indice"),
    @NamedQuery(name = "ItemDeVenda.findByQuantidade", query = "SELECT i FROM ItemDeVenda i WHERE i.quantidade = :quantidade"),
    @NamedQuery(name = "ItemDeVenda.findByTotal", query = "SELECT i FROM ItemDeVenda i WHERE i.total = :total"),
    @NamedQuery(name = "ItemDeVenda.findByValorCompraProduto", query = "SELECT i FROM ItemDeVenda i WHERE i.valorCompraProduto = :valorCompraProduto"),
    @NamedQuery(name = "ItemDeVenda.findByValorProduto", query = "SELECT i FROM ItemDeVenda i WHERE i.valorProduto = :valorProduto")})
public class ItemDeVenda implements Comparable<ItemDeVenda> {

    public ItemDeVenda() {
    }

    public ItemDeVenda(Produto p, double quantidade) {
        this.setQuantidade(quantidade);
        this.setProduto(p);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;

    /**
     * Indice do item na venda
     */
    @Column(nullable = false)
    private int indice;

    /**
     * Quantidade do vendida do produto
     */
    @Column(nullable = false, precision = 4)
    private double quantidade;

    /**
     * Valor que o produto foi vendido quando aconteceu a venda
     */
    @Column(nullable = false, precision = 2)
    private double valorProduto;

    /**
     * Valor que o produto foi comprado quando aconteceu a venda
     */
    @Column(nullable = false, precision = 2)
    private double valorCompraProduto;

    /**
     * Total da venda Sem contabilizar o desconto da promoção
     */
    @Column(nullable = false, precision = 2)
    private double total;

    /**
     * Valor de desconto do item se tiver promoção valida
     */
    @Column(nullable = false, precision = 4)
    private double desconto;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
        CascadeType.DETACH})
    @JoinColumn(updatable = true)
    private Venda venda;

    /**
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = true)
    private Produto produto;

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return indice;
    }

    void setIndex(int index) {
        this.indice = index;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
        if (this.produto != null) {
            setValores(this.produto);
        }
    }

    public double getValorProduto() {
        return valorProduto;
    }

    public double getTotal() {
        return total;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        setValores(this.produto);
    }

    public double getTotalComDesconto() {
        return total - desconto;
    }

    @Override
    public String toString() {
        String toString = getId() + " " + getIndex() + " " + produto.getDescricao() + " X "
                + OperacaoStringUtil.formatarStringQuantidade(quantidade) + " "
                + produto.getDescricaoUnidade();
        toString += " = " + OperacaoStringUtil
                .formatarStringValorMoedaComDescricao(getTotalComDesconto());
        return toString;
    }

    // deve ser utilizado apenas quando for setar o produto pela primeira vez
    private void setValores(Produto p) {
        this.quantidade = new BigDecimal(this.quantidade).setScale(3,
                RoundingMode.HALF_UP).doubleValue();
        valorProduto = p.getValorDeVenda();
        valorCompraProduto = p.getValorDeCompra();
        total = new BigDecimal(quantidade * valorProduto).setScale(2,
                RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        ItemDeVenda other = (ItemDeVenda) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ItemDeVenda o) {
        return Integer.compare(o.id, id);
    }

}
