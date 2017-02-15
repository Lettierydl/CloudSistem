package com.cs.sis.model.estoque;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lettiery
 */
public enum UnidadeProduto {
    //UND, PEÇA,KG, G, M, L;
    UND, PECA, CONJUNTO;
    
    public String getName(){
    	return this.name().replace("_", " ");
    }
}
