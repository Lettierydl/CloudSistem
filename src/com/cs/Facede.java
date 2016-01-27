package com.cs;

import com.cs.sis.controller.*;
import com.cs.sis.controller.configuracao.PermissaoFuncionario;
import com.cs.sis.controller.find.*;
import com.cs.sis.controller.gerador.*;
import com.cs.sis.model.estoque.Produto;
import com.cs.sis.model.financeiro.*;
import com.cs.sis.model.pessoas.*;
import com.cs.sis.model.pessoas.exception.*;
import com.cs.sis.util.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Facede {
    
    private int limit =  100;
    
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

    private static Facede instance = null;

    public static Facede getInstance() {
        if (instance == null) {
            instance = new Facede();
        }
        return instance;
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
        
        

        try {
            imp = ControllerImpressora.getInstance();
        } catch (Error | Exception e) {
        }
    }

    public void adicionarCliente(Cliente c) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.CADASTRAR_CLIENTES);
        pes.create(c);
    }

    public void removerCliente(Cliente c) throws EntidadeNaoExistenteException {
        pes.destroy(c);
    }

    public void atualizarCliente(Cliente c)
            throws EntidadeNaoExistenteException, FuncionarioNaoAutorizadoException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CLIENTES);
        pes.edit(c);
    }

    public double recalcularDebitoDoCliente(Cliente c) throws EntidadeNaoExistenteException, Exception {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CLIENTES);
        return pes.recalcularDebitoDoCliente(c);
    }

    public Cliente buscarClientePorId(int id) {
        return FindCliente.clienteComId(id);
    }

    public List<Cliente> getListaClientes() {
        return FindCliente.listClientes();
    }
    
    public List<Cliente> getListaClientesLimitada() {
        return FindCliente.listClientes(limit);
    }

    public Cliente buscarClientePorCPFOuNomeIqualA(String cpfOuNome) {
        return FindCliente.clientesQueNomeOuCPFIqualA(cpfOuNome);
    }

    public List<Cliente> buscarClientePorCPFOuNomeQueIniciam(String cpfOuNome) {
        return FindCliente.clientesQueNomeOuCPFIniciam(cpfOuNome);
    }

    public List<Cliente> buscarClientePorCPFOuNomeQueIniciam(String cpfOuNome, int maxResult) {
        return FindCliente.clientesQueNomeOuCPFIniciam(cpfOuNome, maxResult);
    }

    public List<String> buscarNomeClientePorNomeQueInicia(String nome) {
        return FindCliente.nomeClientesQueNomeInicia(nome);
    }

    public Cliente buscarClientePorNome(String nome) {
        return FindCliente.clienteComNome(nome);
    }

    // Verificar necessidade
    public Cliente buscarClientePorCPF(String cpf) {
        return FindCliente.clienteComCPF(cpf);
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

    public void removerProduto(Produto p) throws EntidadeNaoExistenteException {
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
    }

    /**
     * Metodo utilizado para finalizar uma venda a prazo<br/>
     *
     * @return Retorna o valor da conta do cliente
     * @param Venda e Valor que o cliente pagou
     * @throws Exception
     * @throws EntidadeNaoExistenteException
     * @throws ParametrosInvalidosException
     */
    public synchronized double finalizarVendaAprazo(Venda v, Cliente c, double partePaga)
            throws ParametrosInvalidosException, EstadoInvalidoDaVendaAtualException {
        double deb = 0;

        try {
            deb = vend.finalizarVendaAPrazo(v, c, partePaga);

            double oud_deb = c.getDebito();

            try {
                c.acrecentarDebito(deb);
                pes.edit(c);
                if (this.buscarClientePorId(c.getId()).getDebito() != oud_deb + deb) {
                    throw new ParametrosInvalidosException("Falha na conexão com o banco de dados \nDebito não atualizado!");
                }
            } catch (Exception e) {
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

                }
            }

        } catch (EstadoInvalidoDaVendaAtualException ei) {
            throw ei;
        } catch (Exception e) {
            throw new EstadoInvalidoDaVendaAtualException("Venda não salva, verifique na tela de pagamentos!");
        }
        return this.buscarClientePorId(c.getId()).getDebito();
    }

    public void adicionarDivida(Divida d, Cliente c) throws EntidadeNaoExistenteException, Exception {
        c.acrecentarDebito(d.getTotal());
        pes.edit(c);
        vend.create(d);
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

    public List<Pagamento> getListaPagamentoDosClientes(List<Cliente> clientes) {
        return FindPagamento.pagamentosDosClientes(clientes);
    }

    public double adicionarPagamento(Pagamento pagamento)
            throws EntidadeNaoExistenteException, ParametrosInvalidosException, Exception {
        Cliente c = FindCliente.clienteComId(pagamento.getCliente().getId());
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

    public List<Venda> getListaVendasNaoPagasDeHoje() {
        return FindVenda.vendasNaoPagaDeHoje();
    }

    public List<Venda> buscarVendasNaoPagasDosClientes(List<Cliente> clientes) {
        return FindVenda.vendasNaoPagasDosClientes(clientes);
    }

    public List<Venda> buscarVendaNaoPagaDoCliente(Cliente cliente) {
        return FindVenda.vendasNaoPagaDoCliente(cliente);
    }
    
    public List<Venda> buscarVendaNaoFinalizadasPorFuncionario(Funcionario f) {
        return FindVenda.getVendasNaoFinalizadasPorFuncionario(f);
    }
    
    public List<Venda> buscarVendaNaoFinalizadas() {
        return FindVenda.getVendasNaoFinalizadas();
    }

    public List<ItemDeVenda> buscarItensDaVendaPorIdDaVenda(int id) {
        return FindVenda.itemDeVendaIdDaVenda(id);
    }

    public List<Pagavel> buscarPagaveisDoCliente(Cliente cliente, Date diaInicio, Date diaFim) {
        return FindVenda.pagavelCliente(cliente, diaInicio, diaFim);
    }

    public List<Pagavel> buscarPagaveisNaoPagosDosClientes(List<Cliente> clientes) {
        return FindVenda.pagavelNaoPagoDosClientes(clientes);
    }

    public List<Pagavel> buscarPagaveisNaoPagoDoCliente(Cliente cliente) {
        return FindVenda.pagavelNaoPagoDoCliente(cliente);
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
        return rel.getMinDia();
    }

    public double[] getRelatorioDeEntradaDeCaixa(Date diaInicio,
            Date diaFim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return rel.getRelatorioDeEntradaDeCaixa(diaInicio, diaFim);
    }

    public double[] getRelatorioDeVendas(Date diaInicio, Date diaFim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return rel.getRelatorioDeVendas(diaInicio, diaFim);
    }

    public double[] getRelatorioDeProduto(Date diaInicio, Date diaFim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return rel.getRelatorioProduto(diaInicio, diaFim);
    }

    public double getRelatorioDeProduto30Dias(int idProduto) {
        return GeradorRelatorio.getRelatorioDeProduto30Dias(idProduto);
    }

    public String gerarPdfRelatorioBalancoProdutos(Date inicio, Date fim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfRelatorioBalancoProdutos(inicio, fim);
    }

    public String gerarPdfDaVendaVenda(Venda v, List<ItemDeVenda> itens) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pdf.gerarPdfDaVenda(v, itens);
    }

    public String gerarPlanilhaRelatorioBalancoProdutos(Date inicio, Date fim) throws FuncionarioNaoAutorizadoException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.GERAR_RELATORIOS);
        return pla.gerarPlanilhaRelatorioBalancoProdutos(inicio, fim);
    }

    public boolean resteurarBancoDeDados(File tempFile) throws FuncionarioNaoAutorizadoException, IOException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CONFIGURACOES);
        return bac.restoreBanco(tempFile);
    }

    public String realizarBackupBancoDeDados() throws FuncionarioNaoAutorizadoException, IOException {
        PermissaoFuncionario.isAutorizado(lg.getLogado(), PermissaoFuncionario.ALTERAR_CONFIGURACOES);
        return bac.criarBackup();
    }

    public void salvarConfiguracoesDeBackup(String nomeArquivoDestinoDefalt, boolean compactarBackup) throws IOException {
        Backup bac = new Backup();
        bac.salvarConfiguracoes(nomeArquivoDestinoDefalt, compactarBackup);
    }

    //Operacao de Risco
    public List<String> dividaSistemaAntigo() throws EntidadeNaoExistenteException, Exception {
        List<Cliente> clientes = FindCliente.listClientes();
        List<String> msg = new ArrayList<String>();
        for (Cliente c : clientes) {
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
                //System.out.println("Divida adicionada no cliente "+c.getNome()+" no valor de "+OperacaoStringUtil.formatarStringValorMoedaComDescricao(d.getValorNaoPago()));
                msg.add("Divida adicionada no cliente " + c.getNome() + " no valor de " + OperacaoStringUtil.formatarStringValorMoedaComDescricao(d.getValorNaoPago()));
            }
        }
        //System.out.println(msg.size() +" clientes do antigo sistema com debito.");
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

}
