package com.cs.sis.model.pessoas;

import java.util.Calendar;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Cliente.class)
public abstract class Cliente_ {

	public static volatile SingularAttribute<Cliente, Double> debito;
	public static volatile SingularAttribute<Cliente, String> telefone;
	public static volatile SingularAttribute<Cliente, String> endereco;
	public static volatile SingularAttribute<Cliente, String> cpf;
	public static volatile SingularAttribute<Cliente, String> celular;
	public static volatile SingularAttribute<Cliente, String> nome;
	public static volatile SingularAttribute<Cliente, Integer> id;
	public static volatile SingularAttribute<Cliente, Calendar> dataDeNascimento;

}

