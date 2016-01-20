package com.cs.sis.model.estoque;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Produto.class)
public abstract class Produto_ {

	public static volatile SingularAttribute<Produto, Double> valorDeVenda;
	public static volatile SingularAttribute<Produto, UnidadeProduto> descricaoUnidade;
	public static volatile SingularAttribute<Produto, Double> limiteMinimoEmEstoque;
	public static volatile SingularAttribute<Produto, CategoriaProduto> categoria;
	public static volatile SingularAttribute<Produto, Double> valorDeCompra;
	public static volatile SingularAttribute<Produto, Integer> id;
	public static volatile SingularAttribute<Produto, String> codigoDeBarras;
	public static volatile SingularAttribute<Produto, Double> quantidadeEmEstoque;
	public static volatile SingularAttribute<Produto, String> descricao;

}

