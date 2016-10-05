package com.cs.sis.model.exception;

public class VendaPendenteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public VendaPendenteException(){
		super("Existe uma venda nao finalizada");
	}
	
	public VendaPendenteException(String msg){
		super(msg);
	}

}
