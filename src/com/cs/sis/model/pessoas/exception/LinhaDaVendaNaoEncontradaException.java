package com.cs.sis.model.pessoas.exception;

public class LinhaDaVendaNaoEncontradaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LinhaDaVendaNaoEncontradaException(){
		super("A Linha da Venda não foi encontrada");
	}

}
