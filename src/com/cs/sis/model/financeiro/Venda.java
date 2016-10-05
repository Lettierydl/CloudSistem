package com.cs.sis.model.financeiro;

import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.exception.ParametrosInvalidosException;
import com.cs.sis.util.OperacaoStringUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import com.cs.sis.model.exception.ParametrosInvalidosException;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "venda")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Venda.findAll", query = "SELECT v FROM Venda v"),
    @NamedQuery(name = "Venda.findById", query = "SELECT v FROM Venda v WHERE v.id = :id"),
    @NamedQuery(name = "Venda.findByDesconto", query = "SELECT v FROM Venda v WHERE v.desconto = :desconto"),
    @NamedQuery(name = "Venda.findByDia", query = "SELECT v FROM Venda v WHERE v.dia = :dia"),
    @NamedQuery(name = "Venda.findByFormaDePagamento", query = "SELECT v FROM Venda v WHERE v.formaDePagamento = :formaDePagamento"),
    @NamedQuery(name = "Venda.findByPaga", query = "SELECT v FROM Venda v WHERE v.paga = :paga"),
    @NamedQuery(name = "Venda.findByPartePaga", query = "SELECT v FROM Venda v WHERE v.partePaga = :partePaga"),
    @NamedQuery(name = "Venda.findByTotal", query = "SELECT v FROM Venda v WHERE v.total = :total"),
    @NamedQuery(name = "Venda.findByObservacao", query = "SELECT v FROM Venda v WHERE v.observacao = :observacao")})
public class Venda implements Comparable<Venda>, Pagavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;

    /**
     * Dia e hora que aconteceu a venda
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar dia;

    /**
     */
    @Column(nullable = false, precision = 2)
    private double desconto;

    /**
     * Valor Total da Venda
     */
    @Column(nullable = false, precision = 2)
    private double total;

    /**
     * O Cliente pode pagar apenas uma parte desta venda
     */
    @Column(nullable = false, precision = 2)
    private double partePaga;

    @Column(nullable = false)
    private boolean paga = false;

    @Enumerated(EnumType.STRING)
    private FormaDePagamento formaDePagamento;

    /**
     */
    @ManyToOne
    @JoinColumn(updatable = true)
    private Funcionario funcionario;

    /**
     */
    @OneToMany(mappedBy = "venda", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<ItemDeVenda> itensDeVenda = new ArrayList<ItemDeVenda>();

    /**
     */
    @ManyToOne
    @JoinColumn(updatable = true)
    private Cliente Cliente;

    @Column
    private String observacao;

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    @Override
    public Calendar getDia() {
        return dia;
    }

    public void setDia(Calendar dia) {
        this.dia = dia;
    }

    public FormaDePagamento getFormaDePagamento() {
        return formaDePagamento;
    }

    /*Apenas controller de Venda deve utilizar esse método*/
    public void setFormaDePagamento(FormaDePagamento formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    @Override
    public Cliente getCliente() {
        return Cliente;
    }

    public void setCliente(Cliente Cliente) {
        this.Cliente = Cliente;
    }

    public void setDesconto(double desconto) throws ParametrosInvalidosException{
        if(desconto > this.total){
            throw new ParametrosInvalidosException("Desconto maior que o valor da venda");
        }else{
            this.desconto = desconto;
        }
    }
    
    public double getDesconto() {
        return desconto;
    }

    /**
     * Valor da venda já com o desconto
     * @return 
     */
    public double getTotalComDesconto() {
        return this.total - this.desconto;
    }

    @Override
    public double getTotal() {
        return total;
        //return getTotalComDesconto();
    }

    /**
     * Valor da venda que falta ser pago
     */
    @Override
    public double getValorNaoPago() {
        return getTotalComDesconto() - partePaga;
    }

    @Override
    public boolean isPaga() {
        return paga;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }

    @Override
    public double getPartePaga() {
        return partePaga;
    }

    public void setPartePaga(double partePagaDaVenda) {
        this.partePaga = partePagaDaVenda;
    }

    /**
     * Acrescenta valor a parte paga da venda Modifica o estado da venda para
     * pago
     *
     * @param Valor para ser acrescentado a parte paga da venda
     */
    @Override
    public void acrescentarPartePaga(double partePagaDaVenda) {
        this.partePaga += partePagaDaVenda;
        this.partePaga = new BigDecimal(partePaga).setScale(3, RoundingMode.HALF_UP).doubleValue();
        if (this.partePaga >= getTotalComDesconto()) {
            setPaga(true);
        }
    }

    /**
     * Apenas o ControllerVenda deve utilizar
     *
     * @param itensDeVenda
     */
    public void setItensDeVenda(List<ItemDeVenda> itensDeVenda) {
        this.itensDeVenda = itensDeVenda;
    }

    public void recalcularTotal() {
        double custo = 0;
        for (ItemDeVenda it : getItensDeVenda()) {
            custo += it.getTotal();
        }
        this.total = custo;
    }

    public List<ItemDeVenda> getItensDeVenda() {
        if (itensDeVenda == null) {
            itensDeVenda = new ArrayList<ItemDeVenda>();
        }
        //Hibernate.initialize(itensDeVenda);
        return itensDeVenda;
    }
    
    public int getQuantidadeDeItens(){
        return getItensDeVenda().size();
    }

    static int in = 0;

    /**
     * @see Deve ser atualizado a venda logo em seguida para que o item de venda
     * seja salvo
     * @exception NullPointer se o produto nao existir
     * @param item já deve vir com produto
     */
    public void addItemDeVenda(ItemDeVenda item) {
        if (item.getProduto() == null) {
            throw new NullPointerException("Item de venda sem produto");
        } else {
            this.getItensDeVenda().add(item);
            item.setIndex(this.getItensDeVenda().size()+1);
            item.setVenda(this);
            this.desconto += item.getDesconto();
            this.total += item.getTotal();
            total = new BigDecimal(total).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }
    }

    /**
     * @see Este metodo so deve ser utilizado pelo
     * ControllerVenda.removerItemDeVenda(item)
     * @see Deve ser removido o ItemDeVenda do banco e logo em seguida chamar
     * esse método para atualizar a venda
     * @param item que existe nesta venda
     */
    public void removeItemDeVenda(ItemDeVenda item) {
        if (this.getItensDeVenda().remove(item)) {
            //atualizar o index
            this.desconto -= item.getDesconto();
            this.total -= item.getTotal();
            total = new BigDecimal(total).setScale(5, RoundingMode.HALF_UP).doubleValue();
        }
    }

    /**
     * @see Este metodo so deve ser utilizado pelo
     * ControllerVenda.removerItemDeVenda(item)
     * @see Deve ser removido o ItemDeVenda do banco e logo em seguida chamar
     * esse método para atualizar a venda
     * @param item que existe nesta venda
     */
    public void removeItemDeVendaJaDeletado(ItemDeVenda item) {
        this.desconto -= item.getDesconto();
        this.total -= item.getTotal();
        total = new BigDecimal(total).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Ordena da mais antiga para a mais recente
     */
    @Override
    public int compareTo(Venda o) {
        return o.dia.compareTo(this.dia);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(desconto);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((dia == null) ? 0 : dia.hashCode());
        result = prime * result + (paga ? 1231 : 1237);
        temp = Double.doubleToLongBits(partePaga);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(total);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Venda other = (Venda) obj;
        if (Double.doubleToLongBits(desconto) != Double
                .doubleToLongBits(other.desconto)) {
            return false;
        }
        if (dia == null) {
            if (other.dia != null) {
                return false;
            }
        }
        if (id != other.id || (id == 0 || 0 == other.id)) {
            return false;
        }
        if (paga != other.paga) {
            return false;
        }
        if (Double.doubleToLongBits(partePaga) != Double
                .doubleToLongBits(other.partePaga)) {
            return false;
        }
        if (Double.doubleToLongBits(total) != Double
                .doubleToLongBits(other.total)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Venda [id=" + id + ", total=" + total + "]";
    }

    @Override
    public String getDescricao() {
        return getObservacao();
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getObservacao() {
        return this.observacao;
    }

    @Override
    public String getData() {
        return OperacaoStringUtil.formatDataTimeValor(dia);
    }

    @Override
    public String getOrigem() {
        return "Venda";
    }


}
