package com.cs;

import com.cs.sis.model.exception.VariasVendasPendentesException;
import com.cs.sis.model.exception.SenhaIncorretaException;
import com.cs.sis.model.exception.EntidadeNaoExistenteException;
import com.cs.sis.model.exception.EstadoInvalidoDaVendaAtualException;
import com.cs.sis.model.exception.FuncionarioNaoAutorizadoException;
import com.cs.sis.model.exception.VendaPendenteException;
import com.cs.sis.model.exception.LoginIncorretoException;
import com.cs.sis.model.exception.ParametrosInvalidosException;
import com.cs.sis.controller.*;
import com.cs.sis.controller.configuracao.PermissaoFuncionario;
import com.cs.sis.controller.find.*;
import com.cs.sis.controller.gerador.*;
import com.cs.sis.controller.impressora.ControllerImpressoraLocal;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.*;
import com.cs.sis.model.pessoas.*;
import com.cs.sis.util.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Facede {
    
    
    private int limit = 100;

    private ControllerEstoque est;
    private ControllerPessoa pes;
    private ControllerLogin lg;
    private ControllerPagamento pagam;
    private ControllerVenda vend;
    private GeradorRelatorio rel;
    private GeradorPDF pdf;
    private GeradorPlanilha pla;
    private ControllerImpressora imp;
    private ControllerConfiguracao config;
    private Backup bac;
    private Arquivo arq;
    private Registro reg;

    private static Facede instance = null;

    public static Facede getInstance() {
        if (instance == null) {
            instance = new Facede();
        }
        return instance;
    }
    public static Facede getInstanceNoConection() {
        if (instance == null) {
            instance = new Facede(1);
        }
        return instance;
    }
    
    private Facede(int n) {
        reg = Registro.getIntance();
        arq = new Arquivo();
        
        try {
            imp = ControllerImpressora.getInstance();
        } catch (Error | Exception e) {
        }
        new Thread(){
            public void run(){
                try{
                    int retorno = imp.testeConectividadeImpressora();
                    if(imp.testeConectividadeImpressora() < 0){
                        System.err.println("impressora não conectada... Retorno: " + retorno);
                    }
                }catch(Throwable e){
                        System.err.println("Não foi possivel carregar a DLL da impressora...");
                }
            }
        }.start();
    }

    private Facede() {
        est = new ControllerEstoque();
        pes = new ControllerPessoa();
        pagam = new ControllerPagamento();
        vend = new ControllerVenda();
        rel = new GeradorRelatorio();
        lg = new ControllerLogin();
        pdf = new GeradorPDF();
        pla = new GeradorPlanilha();
        bac = new Backup();
        config = new ControllerConfiguracao();
        arq = new Arquivo();
        reg = Registro.getIntance();

        try {
            imp = ControllerImpressora.getInstance();
        } catch (Error | Exception e) {
        }
        new Thread(){
            public void run(){
                try{
                    int retorno = imp.testeConectividadeImpressora();
                    if(imp.testeConectividadeImpressora() < 0){
                        System.err.println("impressora não conectada... Retorno: " + retorno);
                    }
                }catch(Throwable e){
                        System.err.println("Não foi possivel carregar a DLL da impressora...");
                }
            }
        }.start();
        
        try {
            if (!(boolean) arq.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.ATIVAR_LIMITE_REGISTRO_MOSTRADOS)) {
                VariaveisDeConfiguracaoUtil.LIMITE_DE_REGISTROS_EXIBIDOS = Integer.MAX_VALUE;
            }
        } catch (Error | Exception e) {
        }
    }

    public void adicionarCliente(Cliente c) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.CADASTRAR_ClienteS);
        pes.create(c);
    }

    public void removerCliente(Cliente c) throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.REMOVER_ClienteS);
        pes.destroy(c);
    }

    public void atualizarCliente(Cliente c)
            throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_ClienteS);
        pes.edit(c);
    }

    public double recalcularDebitoDoCliente(Cliente c) throws EntidadeNaoExistenteException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_ClienteS);
        return pes.recalcularDebitoDoCliente(c);
    }

    public Cliente buscarClientePorId(int id) {
        return FindCliente.ClienteComId(id);
    }

    public List<Cliente> getListaClientes() {
        return FindCliente.listClientes();
    }

    public List<Cliente> getListaClientesLimitada() {
        return FindCliente.listClientes(limit);
    }

    public Cliente buscarClientePorCPFOuNomeIqualA(String cpfOuNome) {
        return FindCliente.ClientesQueNomeOuCPFIqualA(cpfOuNome);
    }

    public List<Cliente> buscarClientePorCPFOuNomeQueIniciam(String cpfOuNome) {
        return FindCliente.ClientesQueNomeOuCPFIniciam(cpfOuNome);
    }

    public List<Cliente> buscarClientePorCPFOuNomeQueIniciam(String cpfOuNome, int maxResult) {
        return FindCliente.ClientesQueNomeOuCPFIniciam(cpfOuNome, maxResult);
    }

    public List<String> buscarNomeClientePorNomeQueInicia(String nome) {
        return FindCliente.nomeClientesQueNomeInicia(nome);
    }

    public Cliente buscarClientePorNome(String nome) {
        return FindCliente.ClienteComNome(nome);
    }

    public Cliente buscarClientePorCPF(String cpf) {
        return FindCliente.ClienteComCPF(cpf);
    }

    /*-------------------------
     * Metodos do Usu��rio/Funcionario
     --------------------------*/
    public List<Funcionario> getListaFuncionarios() {
        return FindFuncionario.listFuncionarios();
    }

    public void adicionarFuncionario(Funcionario f, String senha,
            TipoDeFuncionario tipoDeFuncionario) throws SenhaIncorretaException, FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.CADASTRAR_FUNCIONARIO);
        lg.atribuirSenhaETipoAoFuncionario(f, senha, tipoDeFuncionario);
        pes.create(f);
    }

    /*Não salva o funcionario no banco*/
    public void alterarSenhaDoFuncionario(Funcionario f, String senha, String novaSenha) throws SenhaIncorretaException, LoginIncorretoException {
        lg.alterarSenhaDoFuncionario(f, senha, novaSenha);
    }

    public List<Funcionario> buscarFuncionarioPorNomeQueInicia(String nome) {
        return FindFuncionario.funcionariosQueNomeInicia(nome);
    }

    public Funcionario buscarFuncionarioPorNome(String nome) {
        return FindFuncionario.funcionarioComNome(nome);
    }

    public Funcionario buscarFuncionarioPorCPF(String cpf) {
        return FindFuncionario.funcionarioComCPF(cpf);
    }

    public Funcionario buscarFuncionarioPorLogin(String login) {
        return FindFuncionario.funcionarioComLogin(login);
    }

    public Funcionario buscarFuncionarioPorId(int id) {
        return FindFuncionario.funcionarioComId(id);
    }

    public void removerFuncionario(Funcionario u)
            throws EntidadeNaoExistenteException {
        pes.destroy(u);
    }

    public Funcionario buscarFuncionarioPeloLoginESenha(String login,
            String senha) {
        return FindFuncionario.funcionarioComLoginESenha(login, senha);
    }

    public void atualizarFuncionario(Funcionario f)
            throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_FUNCIONARIO);
        pes.edit(f);
    }

    public Funcionario buscarUsuarioPorNome(String nome) {
        return FindFuncionario.funcionarioComNome(nome);
    }

    /*
     * Metodos de produto
     */
    public void adicionarProduto(Produto p) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.CADASTRAR_PRODUTO);
        est.create(p);
    }

    public void removerProduto(Produto p) throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.REMOVER_PRODUTOS);
        est.destroy(p);
    }

    public void atualizarProduto(Produto p)
            throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_PRODUTO);
        est.edit(p);
    }

    public List<Produto> getListaProdutos() {
        return FindProduto.todosProdutos();
    }

    public List<Produto> getListaProdutosLimitada() {
        return FindProduto.todosProdutos(limit);
    }

    public List<Object[]> getInformacaoProdutos() {
        return FindProduto.informacaoTodosProdutos();
    }

    public List<Produto> buscarProdutoPorDescricaoQueInicia(String descricao) {
        return FindProduto.produtosQueDescricaoLike(descricao);
    }

    public List<Produto> buscarProdutoPorDescricaoOuCodigoQueInicia(String descricaoOuCodigo) {
        return FindProduto.produtosQueDescricaoOuCodigoDeBarrasIniciam(descricaoOuCodigo);
    }

    public List<String> buscarDescricaoProdutoPorDescricaoQueInicia(String descricao) {
        return FindProduto.drecricaoProdutoQueIniciam(descricao);
    }
    
    public List<String> buscarDescricaoProdutos() {
        return FindProduto.drecricaoProdutos();
    }

    public List<String> buscarDescricaoProdutoPorDescricaoQueInicia(String descricao, int maxReult) {
        return FindProduto.drecricaoProdutoQueIniciam(descricao, maxReult);
    }

    public List<String> buscarCodigoProdutoPorCodigoQueInicia(String codigo) {
        return FindProduto.codigoProdutoQueIniciam(codigo);
    }

    public List<String> buscarDescricaoEPrecoProdutoPorDescricaoQueInicia(String descricao, int maxReult) {
        return FindProduto.drecricaoEPrecoProdutoQueIniciam(descricao, maxReult);
    }

    public List<String> buscarDescricaoEPrecoProdutoPorDescricaoQueInicia(String descricao) {
        return FindProduto.drecricaoEPrecoProdutoQueIniciam(descricao);
    }

    public List<String> buscarDescricaoEPrecoProdutos() {
        return FindProduto.drecricaoEPrecoProdutos();
    }

    public Produto buscarProdutoPorId(int idProduto) {
        return FindProduto.produtoComId(idProduto);
    }

    public Produto buscarProdutoPorCodigo(String codigo) {
        return FindProduto.produtoComCodigoDeBarras(codigo);
    }

    public Produto buscarProdutoPorDescricao(String descricao) {
        return FindProduto.produtoComDescricao(descricao);
    }

    public Produto buscarProdutoPorDescricaoOuCodigo(String nomeOuCodigo) {
        return FindProduto.produtoComCodigoEDescricao(nomeOuCodigo);
    }

    public List<Produto> buscarListaProdutoPorDescricaoOuCodigo(String nomeOuCodigo) {
        return FindProduto.produtosQueDescricaoOuCodigoDeBarrasIniciam(nomeOuCodigo);
    }

    public List<Produto> buscarListaProdutoPorDescricaoOuCodigo(String nomeOuCodigo, int limit) {
        return FindProduto.produtosQueDescricaoOuCodigoDeBarrasIniciam(nomeOuCodigo, limit);
    }

    /*
     * Negocio
     */
    /**
     * Efetua login de um funcionario no sistema com o login e senha dele<br/>
     *
     * @exception SenhaIncorretaException Funcionario existente porem a senha
     * esta incorreta
     * @exception LoginIncorretoException Login do funcionario n��o existe caso
     * senha de errado, retorna falso
     */
    public void login(String login, String senha)
            throws SenhaIncorretaException, LoginIncorretoException {
        lg.logar(login, senha);
    }

    /**
     * Encerra as atividades do sistema,<br/>
     * impossibilita que sejam efetuadas qualquer atividades do sistema
     */
    public void logoff() {// utiliza o ControllerFuncionario
        lg.logoff();
    }

    /**
     * Retorna o usuario que esta logado no momento de invocacao do metodo
     *
     * @return
     */
    public Funcionario getFuncionarioLogado() {
        return lg.getLogado();
    }

    /*
     * metodo para iniciar o processo de venda
     */
    public void inicializarVenda() throws VendaPendenteException {
        vend.iniciarNovaVenda();
    }

    public void recuperarVendaPendenteDoFuncionarioLogado() throws VariasVendasPendentesException, Exception {
        vend.recuperarVendaPendente(lg.getLogado());
    }

    public void addItemAVenda(ItemDeVenda it) throws EntidadeNaoExistenteException, Exception {
        vend.addItem(it);
    }

    public void removerItemDaVenda(ItemDeVenda it) throws EntidadeNaoExistenteException, Exception {
        vend.removerItem(it);
    }

    public void removerPagavel(Pagavel v) throws EntidadeNaoExistenteException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.REMOVER_PAGAVEIS);
        vend.destroy(v);
    }
    
    public void atualizarStatusPagaveis(Cliente c) throws EntidadeNaoExistenteException{
        List<Pagavel> pagaveis = this.buscarPagaveisNaoPagoDoCliente(c);
        vend.atualizarStatusPagaveis(pagaveis);
    }

    
    public void removerPagamento(Pagamento p) throws EntidadeNaoExistenteException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.REMOVER_PAGAMENTOS);
        pagam.destroy(p);
    }

    
    public void refreshValorDeVendaAtual() throws EntidadeNaoExistenteException, Exception {
        vend.refreshValorVendaAtual();
    }

    public Venda getVendaAtual() {
        return vend.getAtual();
    }

    public void atualizarDataVendaAtual() throws EntidadeNaoExistenteException, Exception {
        vend.atualizarDataVendaAtual();
    }

    /**
     * Metodo utilizado para finalizar uma venda a vista<br/>
     *
     *
     * @param A venda a ser finalizada
     * @throws Exception
     * @throws EntidadeNaoExistenteException
     */
    public void finalizarVendaAVista(Venda v)
            throws EntidadeNaoExistenteException, Exception {
        vend.finalizarVendaAVista(v);
        double valor = est.retirarItensDoEstoque(v.getItensDeVenda());
        if (valor != v.getTotal()) {
            System.err.println("venda com valor errado");
        }
    }
    
    public void finalizarVendaAVista(Venda v, double desconto)
            throws EntidadeNaoExistenteException,ParametrosInvalidosException, Exception {
        vend.finalizarVendaAVista(v, desconto);
        double valor = est.retirarItensDoEstoque(v.getItensDeVenda());
        if (valor != v.getTotal()) {
            System.err.println("venda com valor errado");
        }
    }
    
    /**
     * Metodo utilizado para finalizar uma venda a prazo<br/>
     *
     * @return Retorna o valor da conta do Cliente
     * @param Venda e Valor que o Cliente pagou
     * @throws Exception
     * @throws EntidadeNaoExistenteException
     * @throws ParametrosInvalidosException
     */
    public double finalizarVendaAprazo(Cliente c, double partePaga, String observacao)
            throws ParametrosInvalidosException, EstadoInvalidoDaVendaAtualException {
        double deb = 0;
        double oud_deb = this.buscarClientePorId(c.getId()).getDebito();
        int id_venda = vend.getAtual().getId();
        try {
            if(observacao !=null && !observacao.trim().isEmpty()){
                vend.getAtual().setObservacao(observacao);
            }
            
            deb = vend.finalizarVendaAPrazo(c, partePaga);
            
            est.retirarItensDoEstoque(FindVenda.itemDeVendaIdDaVenda(id_venda));
            
            try {
                c.acrecentarDebito(deb);
                pes.edit(c);
                if (this.buscarClientePorId(c.getId()).getDebito() != oud_deb + deb) {
                    System.out.println("oud_deb: " + oud_deb+" + deb: " + deb + " = new: "+ (oud_deb + deb) );
                    System.out.println("New: " + this.buscarClientePorId(c.getId()).getDebito());
                    //throw new ParametrosInvalidosException("Falha na conexão com o banco de dados \nDebito não atualizado!");
                }
            } catch (Exception e) {// ver se esse codigo ta complicando, se tiver excue
                System.err.println("ERRO ao tentar editar o Cliente\nQuando acrecentar o Débito");
                e.printStackTrace();
                throw new Exception("Venda salva, porém o débito não inserido na conta do Cliente");
                /*
                if (this.buscarClientePorId(c.getId()).getDebito() != oud_deb + deb) {
                    List<Pagavel> di_ve = FindVenda.pagavelNaoPagoDoCliente(c);
                    double deb_real = 0;
                    boolean existe = false;
                    for (Pagavel p : di_ve) {
                        deb_real += p.getValorNaoPago();
                        if (p instanceof Venda && p.getDia().equals(v.getDia()) && p.getTotal() == v.getTotal()) {
                            existe = true;
                        }
                    }
                    if (!existe) {
                        //venda não salva tentar salvar de novo
                        deb = vend.finalizarVendaAPrazo(v, c, partePaga);
                    }

                    try {
                        c.acrecentarDebito(c.getDebito() - deb_real);
                        pes.edit(c);
                        if (this.buscarClientePorId(c.getId()).getDebito() != deb_real) {
                            throw new ParametrosInvalidosException("Falha na conexão com o banco de dados \nDebito não atualizado!");
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        throw new ParametrosInvalidosException("Falha na conexão com o banco de dados \nDebito não atualizado!");
                    }

                }*/
            }

        } catch (EstadoInvalidoDaVendaAtualException ei) {
            deb = FindVenda.vendaId(id_venda).getValorNaoPago();
            c.acrecentarDebito(deb);
            try {
                pes.edit(c);
            } catch (EntidadeNaoExistenteException ex) {
                throw new ParametrosInvalidosException("Falha na conexão com o banco de dados \nDebito não atualizado!");
            }
            if (this.buscarClientePorId(c.getId()).getDebito() != oud_deb + deb) {
                throw new ParametrosInvalidosException("Falha na conexão com o banco de dados \nDebito não atualizado!");
            }
            throw ei;
        } catch (Exception e) {
            throw new ParametrosInvalidosException("Venda não salva, verifique na tela de pagamentos!");
        }
        return this.buscarClientePorId(c.getId()).getDebito();
    }

    public void adicionarDivida(Divida d, Cliente c) throws EntidadeNaoExistenteException, Exception {
        c.acrecentarDebito(d.getTotal());
        pes.edit(c);
        d.setCliente(c);
        vend.create(d);
    }

    public void removerVendaAVista(Venda v) throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException, EntidadeNaoExistenteException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.REMOVER_VENDA_A_VISTA);
        est.devolverItensDoEstoque(FindVenda.itemDeVendaIdDaVenda(v.getId()));
        try{
            vend.removerVendaAVista(v);
        }catch(EstadoInvalidoDaVendaAtualException e){
            est.retirarItensDoEstoque(FindVenda.itemDeVendaIdDaVenda(v.getId()));
            throw e;
        }
    }
    
    public void selecionarVendaPendente(int idVenda) throws EntidadeNaoExistenteException, Exception {
        vend.selecionarVendaPendente(idVenda);
    }

    public void removerVendaPendente(Venda v) throws EntidadeNaoExistenteException {
        vend.removerVendaPendente(v);
    }

    public void setAtualComVendaPendenteTemporariamente() {
        vend.setAtualComVendaPendenteTemporariamente();
    }

    public List<Pagamento> getListaPagamentoHoje() {
        return FindPagamento.pagamentosDeHoje();
    }

    public List<Pagamento> getListaPagamentoDoCliente(Cliente c) {
        return FindPagamento.pagamentosDoCliente(c);
    }

    public List<Pagamento> getListaPagamentoDoCliente(Cliente c, Date diaInicio, Date diaFim) {
        return FindPagamento.pagamentosDoCliente(c, diaInicio, diaFim);
    }

    public List<Pagamento> getListaPagamentoDosClientes(List<Cliente> Clientes) {
        return FindPagamento.pagamentosDosClientes(Clientes);
    }

    public double adicionarPagamento(Pagamento pagamento)
            throws EntidadeNaoExistenteException, ParametrosInvalidosException, Exception {
        Cliente c = FindCliente.ClienteComId(pagamento.getCliente().getId());
        double troco = 0;
        if (c.getDebito() < pagamento.getValor()) {
            troco = c.getDebito() - pagamento.getValor();
            pagamento.setValor(c.getDebito());
        }
        pagam.create(pagamento);
        vend.abaterValorDoPagamentoNaVenda(pagamento);
        c.diminuirDebito(pagamento.getValor());
        pes.edit(c);

        return troco;
    }
    
    public double adicionarPagamento(Pagamento pagamento, List<Pagavel> pagaveis)
            throws EntidadeNaoExistenteException, ParametrosInvalidosException, Exception {
        Cliente c = FindCliente.ClienteComId(pagamento.getCliente().getId());
        double troco = 0;
        if (c.getDebito() < pagamento.getValor()) {
            troco = c.getDebito() - pagamento.getValor();
            pagamento.setValor(c.getDebito());
        }
        pagam.create(pagamento);
        vend.abaterValorDoPagamentoNaVenda(pagamento, pagaveis);
        c.diminuirDebito(pagamento.getValor());
        pes.edit(c);

        return troco;
    }

    public List<Venda> getListaVendasNaoPagasDeHoje() {
        return FindVenda.vendasNaoPagaDeHoje();
    }

    public List<Venda> buscarVendasNaoPagasDosClientes(List<Cliente> Clientes) {
        return FindVenda.vendasNaoPagasDosClientes(Clientes);
    }

    public List<Venda> buscarVendaNaoPagaDoCliente(Cliente Cliente) {
        return FindVenda.vendasNaoPagaDoCliente(Cliente);
    }

    public List<Venda> buscarVendaNaoFinalizadasPorFuncionario(Funcionario f) {
        return FindVenda.getVendasNaoFinalizadasPorFuncionario(f);
    }

    public List<Venda> buscarVendaNaoFinalizadas() {
        return FindVenda.getVendasNaoFinalizadas();
    }

    public List<Venda> buscarVendaAVista(Calendar dia) {
        return FindVenda.vendasAVista(dia);
    }
    public List<Venda> buscarVendaAPrazo(Calendar dia) {
        return FindVenda.vendasAPrazo(dia);
    }

    public List<ItemDeVenda> buscarItensDaVendaPorIdDaVenda(int id) {
        return FindVenda.itemDeVendaIdDaVenda(id);
    }

    public List<Pagavel> buscarPagaveisDoCliente(Cliente Cliente, Date diaInicio, Date diaFim) {
        return FindVenda.pagavelCliente(Cliente, diaInicio, diaFim);
    }
    
    public List<Pagavel> buscarPagaveisDoCliente(Cliente Cliente) {
        return FindVenda.pagavelCliente(Cliente);
    }

    public List<Pagavel> buscarPagaveisNaoPagosDosClientes(List<Cliente> Clientes) {
        return FindVenda.pagavelNaoPagoDosClientes(Clientes);
    }

    public List<Pagavel> buscarPagaveisNaoPagoDoCliente(Cliente Cliente) {
        return FindVenda.pagavelNaoPagoDoCliente(Cliente);
    }

    public List<Pagavel> buscarPagaveisNaoPagoDoCliente(String nome) {
        return FindVenda.pagavelNaoPagoDoCliente(nome);
    }

    public Venda buscarVendaPeloId(int id_venda) {
        return FindVenda.vendaId(id_venda);
    }

    public boolean getValor(String chave, TipoDeFuncionario tipo) {
        return config.getValor(chave, tipo);
    }

    public void putValor(String chave, boolean valor,
            TipoDeFuncionario tipo) {
        config.putValor(chave, valor, tipo);
    }

    public void permissoesDeFuncionariosDefalt() {
        PermissaoFuncionario.configuracoesDefalt();
    }

    //balanco
    public String getMinDiaRegistroVenda() {
        return GeradorRelatorio.getMinDia();
    }

    public double[] getRelatorioDeEntradaDeCaixa(Date diaInicio,
            Date diaFim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioDeEntradaDeCaixa(diaInicio, diaFim);
    }

    public double[] getRelatorioDeVendas(Date diaInicio, Date diaFim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioDeVendas(diaInicio, diaFim);
    }

    public double[] getRelatorioDeProduto(Date diaInicio, Date diaFim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioProduto(diaInicio, diaFim);
    }
    
    public List<Object[]> getRelatorioDebitoDosClientes() throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioDebitoDosClientes();
    }
    
    public double getRelatorioSomaTotalDeDebitos() throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioSomaTotalDeDebitos();
    }
    
    public List<Object[]> getRelatorioClientesComDebitoMaiorQue(double debito) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioClientesComDebitoMaiorQue(debito);
    }
    
    public double getRelatorioSomaTotalDeDebitosMaiorQue(double debito) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return GeradorRelatorio.getRelatorioSomaTotalDeDebitosMaiorQue(debito);
    }

    public double getRelatorioDeProduto30Dias(int idProduto) {
        return GeradorRelatorio.getRelatorioDeProduto30Dias(idProduto);
    }
    
    public String gerarPdfRelatorioDebitoClientes(double maiorQue, File destino) throws FuncionarioNaoAutorizadoException{
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfRelatorioDebitoClientes(maiorQue, destino);
    }
    
    public String gerarPdfRelatorioBalancoProdutos(Date inicio, Date fim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfRelatorioBalancoProdutos(inicio, fim);
    }
    
    public String gerarPdfRelatorioBalancoProdutos(Date inicio, Date fim, File f) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfRelatorioBalancoProdutos(inicio, fim, f);
    }
    
    public String gerarPdfRelatorioEstoqueProdutos(boolean valoresNegativos ,File f) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfRelatorioEstoqueProdutos(valoresNegativos, f);
    }
    
    public String gerarPdfRelatorioEstoqueProdutos(boolean valoresNegativos, boolean pararLista, Calendar dataInicio, Calendar dataFim, List<String> codigos_retirados, double valorLimite, File f) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfRelatorioEstoqueProdutos(valoresNegativos, pararLista, dataInicio, dataFim, codigos_retirados, valorLimite, f);
    }

    public String gerarPdfDaVendaVenda(Venda v, List<ItemDeVenda> itens) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfDaVenda(v, itens);
    }

    public String gerarPdfDaVendaVenda(Venda v, List<ItemDeVenda> itens, File destino) throws FuncionarioNaoAutorizadoException, IOException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfDaVenda(v, itens, destino);
    }
    
    
    public String gerarPlanilhaRelatorioEstoqueProdutos(boolean estoqueNegativo, File destino) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pla.gerarPlanilhaRelatorioEstoqueProdutos(estoqueNegativo, destino);
    }
    
    public String gerarPlanilhaRelatorioBalancoProdutos(Date inicio, Date fim,File destino) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pla.gerarPlanilhaRelatorioBalancoProdutos(inicio, fim, destino);
    }

    public boolean restaurarBancoDeDados(File tempFile) throws FuncionarioNaoAutorizadoException, IOException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CONFIGURACOES);
        return bac.restoreBanco(tempFile);
    }

    public String realizarBackupBancoDeDados() throws FuncionarioNaoAutorizadoException, IOException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CONFIGURACOES);
        return bac.criarBackup();
    }

    public String realizarBackupBancoDeDados(File file) throws FuncionarioNaoAutorizadoException, IOException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CONFIGURACOES);
        return bac.criarBackup(file);
    }

    public boolean isCopactarBackup() {
        return bac.isCompactarBackup();
    }

    public List<File> getArquivosDestinoBackup() {
        return bac.getAquivos_destinos();
    }

    public void salvarConfiguracoesDeBackup(String nomeArquivoDestinoDefalt, boolean compactarBackup) throws IOException {
        bac.salvarConfiguracoes(nomeArquivoDestinoDefalt, compactarBackup);
    }

    public void inserirConfiguracaoSistema(String key, Object value) {
        arq.addConfiguracaoSistema(key, value);
    }
    
    public void salvarTemporarios(String nome, Object value) {
        arq.salvarTemporaria(value, nome);
    }
    
    public Object recuperarTemporario(String nome) {
        return arq.recuperarTemporaria(nome);
    }

    public Object lerConfiguracaoSistema(String key) {
        return arq.lerConfiguracaoSistema(key);
    }

    public Map<String, Object> lerConfiguracoesSistema() {
        return arq.lerConfiguracoesSistema();
    }

    //Operacao de Risco
    public List<String> dividaSistemaAntigo() throws EntidadeNaoExistenteException, Exception {
        List<Cliente> Clientes = FindCliente.listClientes();
        List<String> msg = new ArrayList<String>();
        for (Cliente c : Clientes) {
            double deb = c.getDebito();
            List<Pagavel> di_ve = FindVenda.pagavelNaoPagoDoCliente(c);
            double consta = 0;
            for (Pagavel p : di_ve) {
                consta += p.getValorNaoPago();
            }
            if (deb > (consta + 0.09)) {
                double dif = new BigDecimal(deb - consta).setScale(2,
                        RoundingMode.HALF_UP).doubleValue();;
                Divida d = new Divida();
                d.setTotal(dif);
                d.setDescricao(OperacaoStringUtil.DESCRICAO_DIVIDA_ANTIGO_SISTEMA);
                d.setDia(Calendar.getInstance());
                d.getDia().set(2015, 8, 1);
                d.setCliente(c);
                vend.create(d);
                //System.out.println("Divida adicionada no Cliente "+c.getNome()+" no valor de "+OperacaoStringUtil.formatarStringValorMoedaComDescricao(d.getValorNaoPago()));
                msg.add("Divida adicionada no Cliente " + c.getNome() + " no valor de " + OperacaoStringUtil.formatarStringValorMoedaComDescricao(d.getValorNaoPago()));
            }
        }
        //System.out.println(msg.size() +" Clientes do antigo sistema com debito.");
        return msg;
    }

    /* Apaga todas as vendas já pagas antes do dia informado 
     * para melhorar o desempenho do banco de dados.
     * */
    public int limparBancoDeDados(Date dia) {
        return vend.limparBancoDeDados(dia);
    }

    public boolean imprimirVenda(Venda v) {
        try {
            return imp.imprimirVenda(v);
        } catch (NullPointerException ne) {
            return false;
        }
    }
    
    public boolean imprimirVendaLocal(Venda v, String impressora) {
        try {
            return ControllerImpressoraLocal.imprimir(impressora, imp.criarTexto(v));
        } catch (NullPointerException ne) {
            return false;
        }
    }
    
    //sem tratamento de erros
    public int retornoImpressora() {
        return imp.testeConectividadeImpressora();
    }
    
    //sem tratamento de erros
    public boolean imprimirTexto(String texto) {
        return imp.imprimirTexto(texto);
    }

    public String gerarCodigo() {
        return est.gerarCodigo();
    }
    
    public String getChaveRegistro(){
        return reg.getChaveRegistro();
    }
    
    public boolean isBloqueado(){
        return reg.isBloqueado();
    }
    
    public boolean isRegistrado(){
        return reg.isRegistrado();
    }
    
    public boolean registrar(String registro,String razao,String endereco,String proprietario){
        return reg.registrar(registro, razao, endereco, proprietario);
    }
    
    public Registro gerRegistro(){
        return reg;
    }

}
