package com.cs.sis.model.exception;

public class FuncionarioNaoAutorizadoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FuncionarioNaoAutorizadoException(){
		super("Funcionário não autorizado para realizar esta operação");
	}
	
	
	public FuncionarioNaoAutorizadoException(String operacao){
		super("Funcionário não autorizado a realizar "+operacao+" entre no sistema com outro funcionário");
	}
	
}
