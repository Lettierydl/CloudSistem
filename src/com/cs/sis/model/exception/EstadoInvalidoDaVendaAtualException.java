package com.cs.sis.model.exception;

public class EstadoInvalidoDaVendaAtualException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EstadoInvalidoDaVendaAtualException(){
		super("Estado inválido da venda atual");
	}
	
	public EstadoInvalidoDaVendaAtualException(String msg){
		super(msg);
	}

}
