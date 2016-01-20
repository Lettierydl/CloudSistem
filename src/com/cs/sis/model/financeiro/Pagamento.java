/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.model.financeiro;

import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import java.util.Calendar;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "pagamento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pagamento.findAll", query = "SELECT p FROM Pagamento p"),
    @NamedQuery(name = "Pagamento.findById", query = "SELECT p FROM Pagamento p WHERE p.id = :id"),
    @NamedQuery(name = "Pagamento.findByData", query = "SELECT p FROM Pagamento p WHERE p.data = :data"),
    @NamedQuery(name = "Pagamento.findByFormaDePagamento", query = "SELECT p FROM Pagamento p WHERE p.formaDePagamento = :formaDePagamento"),
    @NamedQuery(name = "Pagamento.findByObservacao", query = "SELECT p FROM Pagamento p WHERE p.observacao = :observacao"),
    @NamedQuery(name = "Pagamento.findByValor", query = "SELECT p FROM Pagamento p WHERE p.valor = :valor")})
public class Pagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar data;

	@Column(nullable = false, precision = 2)
	private double valor;

	@Column
	private String observacao;

	/**
	 * Como foi efetuado o Pagamento
	 */
	@Enumerated(EnumType.STRING)
	private FormaDePagamento formaDePagamento;

	@ManyToOne
	@JoinColumn(updatable = true)
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(updatable = true)
	private Funcionario funcionario;

	public Pagamento() {
		this.data = Calendar.getInstance();
	}

	public Pagamento(double valor, Cliente cliente, Funcionario funcionario) {
		super();
		this.data = Calendar.getInstance();
		this.valor = valor;
		this.cliente = cliente;
		this.funcionario = funcionario;
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	public Calendar getData() {
		return data;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public FormaDePagamento getFormaDePagamento() {
		return formaDePagamento;
	}

	public void setFormaDePagamento(FormaDePagamento formaDePagamento) {
		this.formaDePagamento = formaDePagamento;
	}

	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((observacao == null) ? 0 : observacao.hashCode());
		long temp;
		temp = Double.doubleToLongBits(valor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pagamento other = (Pagamento) obj;
		if (id != other.id || (id == 0 || 0 == other.id))
			return false;
		if (observacao == null) {
			if (other.observacao != null)
				return false;
		} else if (!observacao.equals(other.observacao))
			return false;
		if (Double.doubleToLongBits(valor) != Double
				.doubleToLongBits(other.valor))
			return false;
		return true;
	}

}