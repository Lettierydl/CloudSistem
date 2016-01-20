package com.cs.sis.model.financeiro;

import com.cs.sis.model.estoque.Produto;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ItemDeVenda.class)
public abstract class ItemDeVenda_ {

	public static volatile SingularAttribute<ItemDeVenda, Double> total;
	public static volatile SingularAttribute<ItemDeVenda, Venda> venda;
	public static volatile SingularAttribute<ItemDeVenda, Double> desconto;
	public static volatile SingularAttribute<ItemDeVenda, Produto> produto;
	public static volatile SingularAttribute<ItemDeVenda, Integer> indice;
	public static volatile SingularAttribute<ItemDeVenda, Double> valorCompraProduto;
	public static volatile SingularAttribute<ItemDeVenda, Integer> id;
	public static volatile SingularAttribute<ItemDeVenda, Double> valorProduto;
	public static volatile SingularAttribute<ItemDeVenda, Double> quantidade;

}

