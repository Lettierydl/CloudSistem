package com.cs.sis.model.exception;

public class FuncionarioJaLogadoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FuncionarioJaLogadoException(){
		super("Já existe um login deste funcionário");
	}
	
}
