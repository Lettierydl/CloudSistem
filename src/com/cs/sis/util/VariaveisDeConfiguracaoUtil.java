package com.cs.sis.util;

import javafx.util.Duration;

public class VariaveisDeConfiguracaoUtil {
    
        public static final String VERSION_SYSTEM = "2.7.0";
    
	public static final boolean LIBERAR_VENDA_DE_PRODUTO_SEM_ESTOQUE = true;
	
	public static final boolean ATIVAR_DESCONTO_DE_PROMOCOES = true;
	
	public static final String DESCRICAO_MOEDA = "R$";
        
        public static final Duration DURACAO_FADE = new Duration(300);
	
        public static int LIMITE_DE_REGISTROS_EXIBIDOS = 100;
        
        //Chaves de configuracao
        
        public static final String ID_DO_CAIXA = "ID_DO_CAIXA";
        public static final String QUANTIDADE_CAIXA = "QUANTIDADE_CAIXA";
        public static final String IP_DO_BANCO = "IP_DO_BANCO";
        public static final String EXTRATEGIA_DE_CONEXAO = "EXTRATEGIA_DE_CONEXCAO";
        
        public static final String COMPACTAR_BACKUP = "COMPACTAR_BACKUP";
        public static final String ARQUIVOS_DEFALT_BACKUP = "ARQUIVOS_DEFALT_BACKUP";
        
        public static final String ATIVAR_LIMITE_REGISTRO_MOSTRADOS = "ATIVAR_LIMITE_REGISTRO_MOSTRADOS";
        
        
        
        public static final String INFORMACAO_ATALHOS ="Atalhos das Telas\n"
                + "Login"
                + "\n\t\tShift + Esc: Configurações do Sistema"
                + "\nPagamento"
                + "\n\t\tF5: Atualiza débito do Cliente"
                + "\nVenda"
                + "\n\t\tEsc: Tela Principal"
                + "\n\t\tShift + Esc: Tela de Mercadorias"
                + "\n\t\tControl: Finalizar Venda à Vista"
                + "\n\t\tAlt: Finalizar Venda à Prazo"
                + "\nMercadorias"
                + "\n\t\tEsc: Tela de Vendas"
                + "\n\t\tShift + P: Tela Principal"
                + "\nVendas à Prazo"
                + "\n\t\tEsc: Tela de Vendas"
                + "\nVendas à Vista"
                + "\n\t\tEsc: Tela de Vendas";
        
        public static String getNomeEstabelecimento(){
            return Registro.getIntance().getRazao();
        }
        
        public static String INFORMACAO_SISTEMA = "Versão do Sistema "+ VERSION_SYSTEM+"\n\n"+INFORMACAO_ATALHOS;
       
       
}        
