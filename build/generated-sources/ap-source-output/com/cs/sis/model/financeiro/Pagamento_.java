package com.cs.sis.model.financeiro;

import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import java.util.Calendar;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Pagamento.class)
public abstract class Pagamento_ {

	public static volatile SingularAttribute<Pagamento, Cliente> cliente;
	public static volatile SingularAttribute<Pagamento, String> observacao;
	public static volatile SingularAttribute<Pagamento, Calendar> data;
	public static volatile SingularAttribute<Pagamento, Double> valor;
	public static volatile SingularAttribute<Pagamento, Integer> id;
	public static volatile SingularAttribute<Pagamento, FormaDePagamento> formaDePagamento;
	public static volatile SingularAttribute<Pagamento, Funcionario> funcionario;

}

