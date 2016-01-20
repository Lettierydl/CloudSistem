package com.cs.sis.model.pessoas.exception;

public class ProdutoABaixoDoEstoqueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ProdutoABaixoDoEstoqueException(){
		super("Produto com quantidade de estoque negativa");
	}

}
