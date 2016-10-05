package com.cs.sis.controller;

import com.cs.sis.controller.find.FindFuncionario;
import com.cs.sis.model.pessoas.Funcionario;
import com.cs.sis.model.pessoas.TipoDeFuncionario;
import com.cs.sis.model.exception.LoginIncorretoException;
import com.cs.sis.model.exception.SenhaIncorretaException;
import com.cs.sis.util.OperacaoStringUtil;
import javax.persistence.NoResultException;


public class ControllerLogin  {

	static Funcionario logado = null;

	public Funcionario getLogado() {
            //ver algouma coisa no banco de dados pra salvar os funcionarios logados nos caixas
            return logado;
	}

	public void logar(String login, String senha)
			throws SenhaIncorretaException, LoginIncorretoException {
		if (logado == null) {
			Funcionario f = validarSenha(login, senha);
			logado = f;
			//registrar login em algum canto
		} else {
			logoff();
			logar(login,senha);
		}
	}

	public void logoff() {
            //remover e registrar que o funcionario foi desligado
            logado = null;
	}
	
	
	public void atribuirSenhaETipoAoFuncionario(Funcionario f, String senha, TipoDeFuncionario tipoDeFuncionario) throws SenhaIncorretaException{
		if(f.getSenha() == null){
                    f.setSenha(this.criptografar(senha));
                    f.setTipoDeFuncionario(tipoDeFuncionario);
		}else{
                    throw new SenhaIncorretaException("Senha já cadastrada para este funcionario");
		}
	}
	
	public void alterarSenhaDoFuncionario(Funcionario f, String senha, String novaSenha) throws SenhaIncorretaException, LoginIncorretoException{
		validarSenha(f, senha);
		f.setSenha(this.criptografar(novaSenha));
	}

	private Funcionario validarSenha(String login, String senha)
			throws SenhaIncorretaException, LoginIncorretoException {
		Funcionario f = null;
		try {
			f = FindFuncionario.funcionarioComLoginESenha(login,
					criptografar(senha));
		} catch (Exception e) {
			try{
				FindFuncionario.funcionarioComLogin(login);
			}catch(NoResultException nre){
				throw new LoginIncorretoException();
			}
			throw new SenhaIncorretaException();
		}
		return f;
	}
	
	/*valida sem consultar no banco*/
	private void validarSenha(Funcionario f , String senha)
			throws SenhaIncorretaException {
		if(!f.getSenha().equals(criptografar(senha))){
			throw new SenhaIncorretaException();
		}
	}
	
	private String criptografar(String string) {
		return OperacaoStringUtil.criptografar(string);
	}
}
