/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.model.pessoas;

import com.cs.sis.model.pessoas.exception.ParametrosInvalidosException;
import com.cs.sis.util.OperacaoStringUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "cliente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findById", query = "SELECT c FROM Cliente c WHERE c.id = :id"),
    @NamedQuery(name = "Cliente.findByCelular", query = "SELECT c FROM Cliente c WHERE c.celular = :celular"),
    @NamedQuery(name = "Cliente.findByCpf", query = "SELECT c FROM Cliente c WHERE c.cpf = :cpf"),
    @NamedQuery(name = "Cliente.findByDataDeNascimento", query = "SELECT c FROM Cliente c WHERE c.dataDeNascimento = :dataDeNascimento"),
    @NamedQuery(name = "Cliente.findByDebito", query = "SELECT c FROM Cliente c WHERE c.debito = :debito"),
    @NamedQuery(name = "Cliente.findByEndereco", query = "SELECT c FROM Cliente c WHERE c.endereco = :endereco"),
    @NamedQuery(name = "Cliente.findByNome", query = "SELECT c FROM Cliente c WHERE c.nome = :nome"),
    @NamedQuery(name = "Cliente.findByTelefone", query = "SELECT c FROM Cliente c WHERE c.telefone = :telefone")})
public class Cliente implements Serializable, Pessoa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private int id;
	
	/**
     */
	@Column(nullable = false, precision = 2, unique = true)
	private String nome;

	/**
	 * Valor total de quanto o cliente deve
     */
	@Column(nullable = false, precision = 2)
	private double debito;

	/**
	 * sem a mascara
     */
	@Column(nullable = true, length = 11)
	private String cpf;

	/**
     */
	@Temporal(TemporalType.DATE)
	private Calendar dataDeNascimento;
	
	@Column(nullable = true, length = 15)
	private String telefone;
	
	@Column(nullable = true, length = 15)
	private String celular;
	
	@Column(nullable = true, length = 500)
	private String endereco;	

	/**
     
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "cliente")
	private List<Pagamento> pagamentos = new ArrayList<Pagamento>();
	*/
	 
	
	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}
	
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
        
        public String getTelefones(){
            return getTelefone()+"\n"+getCelular();
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

	public double getDebito() {
		debito = new BigDecimal(this.debito).setScale(2,
				RoundingMode.HALF_UP).doubleValue();
		return debito;
	}

	public String getCpf() {
		return OperacaoStringUtil.formatarStringParaMascaraDeCPF(cpf);
	}

	public void setCpf(String cpf) {
		this.cpf = OperacaoStringUtil.retirarMascaraDeCPF(cpf);
	}

	public Calendar getDataDeNascimento() {
		return dataDeNascimento;
	}
	
	public Date getDataDeNascimentoDate() {
		if(dataDeNascimento != null){
			return dataDeNascimento.getTime();
		}
		return null;
	}

	public void setDataDeNascimento(Calendar dataDeNascimento) {
		this.dataDeNascimento = dataDeNascimento;
	}
	
	public void setDataDeNascimentoDate(Date dataDeNascimento) {
		if(dataDeNascimento != null){
			this.dataDeNascimento = Calendar.getInstance();
			this.dataDeNascimento.setTime(dataDeNascimento);
		}
	}
	
	public void acrecentarDebito(double valor)
			throws ParametrosInvalidosException {
		if (valor > 0) {
			debito += valor;
			debito = new BigDecimal(this.debito).setScale(2,
					RoundingMode.HALF_UP).doubleValue();
		} else {
			throw new ParametrosInvalidosException("Não pode ser acencentado um valor negativo ao debito do cliente");
		}
	}

	public void diminuirDebito(double valor)
			throws ParametrosInvalidosException {
		if (valor > 0) {
			debito -= valor;
			debito = new BigDecimal(this.debito).setScale(2,
					RoundingMode.HALF_UP).doubleValue();
		} else {
			throw new ParametrosInvalidosException("N��o pode ser retirado um valor negativo ao debito do cliente");
		}
	}

	@Override
	public String toString() {
		return nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		result = prime * result + id;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Cliente other = (Cliente) obj;
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		if (id != other.id)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}	

}
