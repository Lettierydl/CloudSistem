package com.cs.sis.model.estoque;

public enum CategoriaProduto {
    Alimentos, Produtos_de_Limpeza, Produtos_de_Higiene, Bebidas_e_Sucos, Produtos_de_Bazar, Papelaria, Utilidades_Domesticas, Outra;
    
    public String getName(){
    	return this.name().replace("_", " ");
    }
}