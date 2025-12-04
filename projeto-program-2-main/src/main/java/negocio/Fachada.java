package negocio;

import java.time.LocalDate;
import java.util.List;

import controller.ClienteController;
import controller.FuncionarioController;
import controller.ItemCardapioController;
import controller.MesaController;
import controller.PedidoController;
import controller.ReservaController;
import modelo.Cliente;
import modelo.Funcionario;
import modelo.ItemCardapio;
import modelo.Mesa;
import modelo.MetodoPagamento;
import modelo.Pedido;
import modelo.Relatorio;
import modelo.Reserva;
import modelo.StatusMesa;
import repositorio.*;

public class Fachada {

    private static Fachada instancia;

    private ClienteController clienteController;
    private FuncionarioController funcionarioController;
    private ItemCardapioController itemCardapioController;
    private MesaController mesaController;
    private PedidoController pedidoController;
    private ReservaController reservaController;
    private Relatorio relatorio;

    private Fachada() {
        inicializarControllers();
        this.relatorio = new Relatorio();
    }

    public static Fachada getInstancia() {
        if (instancia == null) {
            instancia = new Fachada();
        }
        return instancia;
    }

    private void inicializarControllers() {
        // 1. Inicializa Repositórios
        IRepositorioCliente repositorioCliente = new RepositorioClienteArray();
        IRepositorioFuncionario repositorioFuncionario = new RepositorioFuncionarioArray();
        IRepositorioItemCardapio repositorioItemCardapio = new RepositorioItemCardapioArray();
        IRepositorioMesa repositorioMesa = new RepositorioMesaArray();
        IRepositorioPedido repositorioPedido = new RepositorioPedidoArray();
        IRepositorioReserva repositorioReserva = new RepositorioReservaArray();

        // 2. Inicializa Controllers Básicos
        this.clienteController = new ClienteController(repositorioCliente);
        this.funcionarioController = new FuncionarioController(repositorioFuncionario);
        this.itemCardapioController = new ItemCardapioController(repositorioItemCardapio);
        this.mesaController = new MesaController(repositorioMesa);
        this.reservaController = new ReservaController(repositorioReserva);
        
        // 3. Inicializa PedidoController COM as dependências injetadas
        // (Isso garante que ele possa acessar Mesa, Cliente e Cardápio internamente)
        this.pedidoController = new PedidoController(
            repositorioPedido, 
            this.mesaController, 
            this.clienteController, 
            this.itemCardapioController
        );
    }

    // =======================================================
    // OPERAÇÕES DE FUNCIONÁRIO (Login)
    // =======================================================

    public boolean cadastrarFuncionario(Funcionario funcionario) {
        return funcionarioController.cadastrarFuncionario(funcionario);
    }

    public Funcionario loginFuncionario(String nome, String senha) {
        return funcionarioController.login(nome, senha);
    }

    // =======================================================
    // OPERAÇÕES DE CLIENTE
    // =======================================================

    public boolean cadastrarCliente(Cliente cliente) {
        return clienteController.cadastrarCliente(cliente);
    }

    public Cliente buscarClientePorTelefone(String telefone) {
        return clienteController.buscarClientePorTelefone(telefone);
    }

    public boolean atualizarCliente(Cliente cliente) {
        return clienteController.atualizarCliente(cliente);
    }

    public boolean removerCliente(String telefone) {
        return clienteController.removerCliente(telefone);
    }

    public List<Cliente> listarClientes() {
        return clienteController.listarTodosClientes();
    }

    // =======================================================
    // OPERAÇÕES DE MESA
    // =======================================================

    public boolean cadastrarMesa(Mesa mesa) {
        return mesaController.cadastrarMesa(mesa);
    }

    public Mesa buscarMesa(int numero) {
        return mesaController.buscarMesaPorNumero(numero);
    }

    public boolean atualizarMesa(Mesa mesa) {
        return mesaController.atualizarMesa(mesa);
    }

    public boolean removerMesa(int numero) {
        return mesaController.removerMesa(numero);
    }

    public List<Mesa> listarMesas() {
        return mesaController.listarTodasMesas();
    }

    public boolean alterarStatusMesa(int numeroMesa, StatusMesa novoStatus) {
        return mesaController.alterarStatusMesa(numeroMesa, novoStatus);
    }

    // =======================================================
    // OPERAÇÕES DE CARDÁPIO
    // =======================================================

    public boolean cadastrarItemCardapio(ItemCardapio item) {
        return itemCardapioController.cadastrarItemCardapio(item);
    }

    public boolean atualizarItemCardapio(ItemCardapio item) {
        return itemCardapioController.atualizarItem(item);
    }

    public boolean removerItemCardapio(String nome) {
        return itemCardapioController.removerItem(nome);
    }

    public List<ItemCardapio> listarItensCardapio() {
        return itemCardapioController.listarTodosItens();
    }

    // =======================================================
    // OPERAÇÕES DE PEDIDO (ATUALIZADO)
    // =======================================================

    // Agora não precisamos passar mesaController nem clienteController
    public Pedido criarPedido(int numeroMesa, String telefoneCliente) {
        return pedidoController.criarPedido(numeroMesa, telefoneCliente);
    }

    // Agora não precisamos passar itemCardapioController
    public boolean adicionarItemPedido(int idPedido, String nomeItem, int quantidade) {
        return pedidoController.adicionarItemAoPedido(idPedido, nomeItem, quantidade);
    }

    // Agora não precisamos passar mesaController
    public boolean registrarPagamento(int idPedido, MetodoPagamento metodo) {
        return pedidoController.registrarPagamento(idPedido, metodo);
    }

    public List<Pedido> listarPedidos() {
        return pedidoController.listarTodosPedidos();
    }

    // =======================================================
    // OPERAÇÕES DE RESERVA
    // =======================================================

    public boolean fazerReserva(Reserva reserva, Mesa mesa) {
        // Futuramente, podemos aplicar a mesma lógica de injeção no ReservaController
        return reservaController.fazerReserva(reserva, mesa, mesaController);
    }

    public boolean cancelarReserva(Reserva reserva) {
        return reservaController.cancelarReserva(reserva, mesaController);
    }

    public List<Reserva> listarReservas() {
        return reservaController.listarTodasReservas();
    }

    // =======================================================
    // RELATÓRIOS
    // =======================================================

    public String gerarRelatorioVendas(LocalDate inicio, LocalDate fim) {
        return relatorio.gerarVendasPorPeriodo(pedidoController.listarTodosPedidos(), inicio, fim);
    }

    public String gerarRelatorioItensMaisVendidos() {
        return relatorio.gerarItensMaisVendidos(pedidoController.listarTodosPedidos());
    }
}