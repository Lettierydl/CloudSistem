package com.cs.sis.model.financeiro;

import com.cs.sis.model.pessoas.Cliente;
import com.cs.sis.model.pessoas.Funcionario;
import java.util.Calendar;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Divida.class)
public abstract class Divida_ {

	public static volatile SingularAttribute<Divida, Cliente> cliente;
	public static volatile SingularAttribute<Divida, Double> total;
	public static volatile SingularAttribute<Divida, Integer> id;
	public static volatile SingularAttribute<Divida, FormaDePagamento> formaDePagamento;
	public static volatile SingularAttribute<Divida, Funcionario> funcionario;
	public static volatile SingularAttribute<Divida, Calendar> dia;
	public static volatile SingularAttribute<Divida, Double> partePaga;
	public static volatile SingularAttribute<Divida, String> descricao;
	public static volatile SingularAttribute<Divida, Boolean> paga;

}

