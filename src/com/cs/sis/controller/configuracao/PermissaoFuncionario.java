package com.cs.sis.controller.configuracao;

import com.cs.sis.controller.ControllerConfiguracao;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.model.pessoas.exception.FuncionarioNaoAutorizadoException;


public class PermissaoFuncionario {
	
	
	
	public static final String ALTERAR_CONFIGURACOES = "ALTERAR_CONFIGURACOES";
	
	public static final String ALTERAR_FUNCIONARIO = "ALTERAR_FUNCIONARIO";
	public static final String ALTERAR_PRODUTO = "ALTERAR_PRODUTO";
	public static final String ALTERAR_CLIENTES = "ALTERAR_CLIENTES";
	
	public static final String CADASTRAR_FUNCIONARIO = "CADASTRAR_FUNCIONARIO";
	public static final String CADASTRAR_PRODUTO = "CADASTRAR_PRODUTO";
	public static final String CADASTRAR_CLIENTES = "CADASTRAR_CLIENTES";
        
        public static final String REMOVER_CLIENTES = "REMOVER_CLIENTES";
        public static final String REMOVER_PAGAVEIS = "REMOVER_PAGAVEIS";
        public static final String REMOVER_PAGAMENTOS = "REMOVER_PAGAMENTOS";
        public static final String REMOVER_PRODUTOS = "REMOVER_PRODUTOS";
        

	public static final String GERAR_RELATORIOS = "GERAR_RELATORIOS";
	
	private static ControllerConfiguracao controller;
	

	public static ControllerConfiguracao getControllerConfiguracao(){
		if (controller == null){
			controller = new ControllerConfiguracao();
		}
		return controller;
	}
	
	public static boolean isAutorizado(Funcionario func, final String acao) throws FuncionarioNaoAutorizadoException{
		if(getControllerConfiguracao().cont(Configuracao.class)== 0){
			configuracoesDefalt();
		}
		
		if(getValor(acao, func.getTipoDeFuncionario())){
			return true;
		}else{
			throw new FuncionarioNaoAutorizadoException("( "+acao+" )");
		}
	}
	
	public static boolean getValor(String acao, TipoDeFuncionario tipo){
		return getControllerConfiguracao().getValor(acao, tipo);
	}
	
	
	public static void putValor(String acao, boolean valor ,TipoDeFuncionario tipo)throws FuncionarioNaoAutorizadoException{
		getControllerConfiguracao().putValor(acao, valor, tipo);
	}
	
	
	public static void configuracoesDefalt(){
		getControllerConfiguracao().removeAll(Configuracao.class);
		
		getControllerConfiguracao().putValor(ALTERAR_FUNCIONARIO, true, TipoDeFuncionario.Gerente);
		getControllerConfiguracao().putValor(ALTERAR_CONFIGURACOES, true, TipoDeFuncionario.Gerente);
		getControllerConfiguracao().putValor(CADASTRAR_FUNCIONARIO, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(ALTERAR_CLIENTES, true, TipoDeFuncionario.Caixa);
		getControllerConfiguracao().putValor(ALTERAR_CLIENTES, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(ALTERAR_CLIENTES, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(ALTERAR_PRODUTO, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(ALTERAR_PRODUTO, true, TipoDeFuncionario.Gerente);
		
		
		getControllerConfiguracao().putValor(CADASTRAR_PRODUTO, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(CADASTRAR_PRODUTO, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(CADASTRAR_CLIENTES, true, TipoDeFuncionario.Caixa);
		getControllerConfiguracao().putValor(CADASTRAR_CLIENTES, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(CADASTRAR_CLIENTES, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(GERAR_RELATORIOS, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(GERAR_RELATORIOS, true, TipoDeFuncionario.Gerente);
                
                getControllerConfiguracao().putValor(REMOVER_CLIENTES, true, TipoDeFuncionario.Gerente);
                getControllerConfiguracao().putValor(REMOVER_PAGAMENTOS, true, TipoDeFuncionario.Gerente);
                getControllerConfiguracao().putValor(REMOVER_PAGAVEIS, true, TipoDeFuncionario.Gerente);
                getControllerConfiguracao().putValor(REMOVER_PRODUTOS, true, TipoDeFuncionario.Gerente);
		
		
	}
	
	
	
}