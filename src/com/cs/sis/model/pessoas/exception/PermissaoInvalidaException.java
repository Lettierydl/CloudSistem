package com.cs.sis.model.pessoas.exception;

public class PermissaoInvalidaException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PermissaoInvalidaException(){
		super("Você não possue permissão para realizar esta operação.");
	}
	
}
