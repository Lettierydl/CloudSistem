package com.cs.sis.model.pessoas.exception;

public class EntidadeJaPersistidaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntidadeJaPersistidaException(String nomeDaEntidade) {
		super(nomeDaEntidade+" já cadastrado com as mesmas caracteríticas");
	}

}
