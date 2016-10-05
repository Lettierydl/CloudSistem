package com.cs.sis.model.exception;

public class EntidadeNaoExistenteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntidadeNaoExistenteException(String msg){
		super(msg);
	}
	
	public EntidadeNaoExistenteException(){
		super("Entidade não existente.");
	}
}
