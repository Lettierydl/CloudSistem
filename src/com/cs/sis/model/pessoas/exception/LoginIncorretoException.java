package com.cs.sis.model.pessoas.exception;

public class LoginIncorretoException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginIncorretoException(){
		super("Login Inválido");
	}
	
	public LoginIncorretoException(String msg){
		super(msg);
	}
}
