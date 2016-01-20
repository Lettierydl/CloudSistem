package com.cs.sis.model.financeiro;

import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import java.util.Calendar;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Venda.class)
public abstract class Venda_ {

	public static volatile SingularAttribute<Venda, Cliente> cliente;
	public static volatile SingularAttribute<Venda, Double> total;
	public static volatile SingularAttribute<Venda, String> observacao;
	public static volatile SingularAttribute<Venda, Double> desconto;
	public static volatile SingularAttribute<Venda, Integer> id;
	public static volatile SingularAttribute<Venda, FormaDePagamento> formaDePagamento;
	public static volatile SingularAttribute<Venda, Funcionario> funcionario;
	public static volatile SingularAttribute<Venda, Calendar> dia;
	public static volatile SingularAttribute<Venda, Double> partePaga;
	public static volatile ListAttribute<Venda, ItemDeVenda> itensDeVenda;
	public static volatile SingularAttribute<Venda, Boolean> paga;

}

