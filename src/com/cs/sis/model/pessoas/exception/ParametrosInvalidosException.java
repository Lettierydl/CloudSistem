package com.cs.sis.model.pessoas.exception;

public class ParametrosInvalidosException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParametrosInvalidosException(){
		super("Valor de compo inválido para esta operação");
	}
	
	
	public ParametrosInvalidosException(String msg){
		super(msg);
	}
}
