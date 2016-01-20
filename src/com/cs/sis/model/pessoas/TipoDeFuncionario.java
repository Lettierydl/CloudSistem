package com.cs.sis.model.pessoas;

public enum TipoDeFuncionario {

    Caixa, Supervisor, Gerente;
    
    public String getName(){
    	return this.name();
    }
}
