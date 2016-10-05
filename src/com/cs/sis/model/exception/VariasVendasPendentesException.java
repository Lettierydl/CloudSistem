package com.cs.sis.model.exception;

import com.cs.sis.model.financeiro.Venda;
import java.util.List;

public class VariasVendasPendentesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
        public List<Venda> pendentes;
	
	public VariasVendasPendentesException(){
		super("Existe várias vendas não finalizadas");
	}
	
	public VariasVendasPendentesException(String msg){
		super(msg);
	}

    public VariasVendasPendentesException(List<Venda> newPendentes) {
        this.pendentes = newPendentes;
    }

}
