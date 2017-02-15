package com.cs.sis.controller.configuracao;

import com.cs.sis.controller.ControllerConfiguracao;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;


public class PermissaoFuncionario {
	
	
	
	public static final String ALTERAR_CONFIGURACOES = "ALTERAR_CONFIGURACOES";
	
	public static final String ALTERAR_FUNCIONARIO = "ALTERAR_FUNCIONARIO";
	public static final String ALTERAR_PRODUTO = "ALTERAR_PRODUTO";
        public static final String ALTERAR_ESTORQUE_PRODUTO = "ALTERAR_ESTORQUE_PRODUTO";
	public static final String ALTERAR_ClienteS = "ALTERAR_ClienteS";
	
	public static final String CADASTRAR_FUNCIONARIO = "CADASTRAR_FUNCIONARIO";
	public static final String CADASTRAR_PRODUTO = "CADASTRAR_PRODUTO";
	public static final String CADASTRAR_ClienteS = "CADASTRAR_ClienteS";
        
        public static final String DESCONTO_NA_VENDA = "DESCONTO_NA_VENDA";
        
        
        public static final String REMOVER_ClienteS = "REMOVER_ClienteS";
        public static final String REMOVER_PAGAVEIS = "REMOVER_PAGAVEIS";
        public static final String REMOVER_PAGAMENTOS = "REMOVER_PAGAMENTOS";
        public static final String REMOVER_PRODUTOS = "REMOVER_PRODUTOS";
        public static final String REMOVER_VENDA_A_VISTA = "REMOVER_VENDA_A_VISTA";
        

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
		//getControllerConfiguracao().removeAll(Configuracao.class);
		
		getControllerConfiguracao().putValor(ALTERAR_FUNCIONARIO, true, TipoDeFuncionario.Gerente);
		getControllerConfiguracao().putValor(ALTERAR_CONFIGURACOES, true, TipoDeFuncionario.Gerente);
		getControllerConfiguracao().putValor(CADASTRAR_FUNCIONARIO, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(ALTERAR_ClienteS, true, TipoDeFuncionario.Caixa);
		getControllerConfiguracao().putValor(ALTERAR_ClienteS, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(ALTERAR_ClienteS, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(ALTERAR_PRODUTO, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(ALTERAR_PRODUTO, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(ALTERAR_ESTORQUE_PRODUTO, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(CADASTRAR_PRODUTO, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(CADASTRAR_PRODUTO, true, TipoDeFuncionario.Gerente);
		
		getControllerConfiguracao().putValor(CADASTRAR_ClienteS, true, TipoDeFuncionario.Caixa);
		getControllerConfiguracao().putValor(CADASTRAR_ClienteS, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(CADASTRAR_ClienteS, true, TipoDeFuncionario.Gerente);
		
                
                //getControllerConfiguracao().putValor(DESCONTO_NA_VENDA, false, TipoDeFuncionario.Caixa);
		//getControllerConfiguracao().putValor(DESCONTO_NA_VENDA, false, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(DESCONTO_NA_VENDA, true, TipoDeFuncionario.Gerente);
		
                
                
		getControllerConfiguracao().putValor(GERAR_RELATORIOS, true, TipoDeFuncionario.Supervisor);
		getControllerConfiguracao().putValor(GERAR_RELATORIOS, true, TipoDeFuncionario.Gerente);
                
                getControllerConfiguracao().putValor(REMOVER_ClienteS, true, TipoDeFuncionario.Gerente);
                getControllerConfiguracao().putValor(REMOVER_PAGAMENTOS, true, TipoDeFuncionario.Gerente);
                getControllerConfiguracao().putValor(REMOVER_PAGAVEIS, true, TipoDeFuncionario.Gerente);
                getControllerConfiguracao().putValor(REMOVER_PRODUTOS, true, TipoDeFuncionario.Gerente);
		getControllerConfiguracao().putValor(REMOVER_VENDA_A_VISTA, true, TipoDeFuncionario.Gerente);
		
		
	}
	
	
	
}