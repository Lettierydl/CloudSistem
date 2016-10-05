package com.cs.sis.model.exception;

public class SenhaIncorretaException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SenhaIncorretaException(){
		super("Senha incorreta");
	}
	
	public SenhaIncorretaException(String msg){
		super(msg);
	}
}
