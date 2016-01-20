package com.cs.sis.controller.configuracao;

import com.cs.sis.model.pessoas.TipoDeFuncionario;
import java.io.Serializable;


public class ConfiguracaoPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String chave;// nome

	private TipoDeFuncionario tipoDeFuncionario;

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public TipoDeFuncionario getTipoDeFuncionario() {
		return tipoDeFuncionario;
	}

	public void setTipoDeFuncionario(TipoDeFuncionario tipoDeFuncionario) {
		this.tipoDeFuncionario = tipoDeFuncionario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		result = prime
				* result
				+ ((tipoDeFuncionario == null) ? 0 : tipoDeFuncionario
						.hashCode());
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
		ConfiguracaoPK other = (ConfiguracaoPK) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		if (tipoDeFuncionario != other.tipoDeFuncionario)
			return false;
		return true;
	}

	public ConfiguracaoPK() {
	}

}