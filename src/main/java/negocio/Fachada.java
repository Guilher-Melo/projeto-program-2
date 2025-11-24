package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import controller.ClienteController;
import controller.FuncionarioController;
import controller.ItemCardapioController;
import controller.MesaController;
import controller.PedidoController;
import controller.ReservaController;
import modelo.CategoriaItem;
import modelo.Cliente;
import modelo.Funcionario;
import modelo.ItemCardapio;
import modelo.Mesa;
import modelo.MetodoPagamento;
import modelo.Pedido;
import modelo.Relatorio;
import modelo.Reserva;
import modelo.StatusMesa;

public class Fachada {

    private ClienteController clienteController;
    private FuncionarioController funcionarioController;
    private ItemCardapioController itemCardapioController;
    private MesaController mesaController;
    private PedidoController pedidoController;
    private ReservaController reservaController;
    private Relatorio relatorio;

    public Fachada(ClienteController clienteController,
                   FuncionarioController funcionarioController,
                   ItemCardapioController itemCardapioController,
                   MesaController mesaController,
                   PedidoController pedidoController,
                   ReservaController reservaController) {

        this.clienteController = clienteController;
        this.funcionarioController = funcionarioController;
        this.itemCardapioController = itemCardapioController;
        this.mesaController = mesaController;
        this.pedidoController = pedidoController;
        this.reservaController = reservaController;
        this.relatorio = new Relatorio();
    }

    // =======================================================
    //                OPERAÇÕES DE FUNCIONÁRIO (Login)
    // =======================================================

    public boolean cadastrarFuncionario(String nome, String cargo, String senha) {
        Funcionario novo = new Funcionario(nome, cargo, senha);
        return funcionarioController.cadastrarFuncionario(novo);
    }

    public Funcionario loginFuncionario(String nome, String senha) {
        return funcionarioController.login(nome, senha);
    }

    // =======================================================
    //                  OPERAÇÕES DE CLIENTE
    // =======================================================

    public boolean cadastrarCliente(String nome, String telefone, String email) {
        Cliente novoCliente = new Cliente(nome, telefone, email);
        return clienteController.cadastrarCliente(novoCliente);
    }

    public Cliente buscarClientePorTelefone(String telefone) {
        return this.clienteController.buscarClientePorTelefone(telefone);
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
    //                  OPERAÇÕES DE MESA
    // =======================================================

    public boolean cadastrarMesa(int numero, int capacidade) {
        Mesa novaMesa = new Mesa(numero, capacidade, StatusMesa.LIVRE);
        return mesaController.cadastrarMesa(novaMesa);
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
    //                  OPERAÇÕES DE CARDÁPIO
    // =======================================================

    public boolean cadastrarItemCardapio(String nome, String desc, double preco, CategoriaItem categoria) {
        ItemCardapio novoItem = new ItemCardapio(nome, desc, preco, categoria);
        return itemCardapioController.cadastrarItemCardapio(novoItem);
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
    //                  OPERAÇÕES DE PEDIDO
    // =======================================================

    public Pedido criarPedido(int numeroMesa, String telefoneCliente) {
        return pedidoController.criarPedido(numeroMesa, telefoneCliente, mesaController, clienteController);
    }

    public boolean adicionarItemPedido(int idPedido, String nomeItem, int quantidade) {
        return pedidoController.adicionarItemAoPedido(idPedido, nomeItem, quantidade, itemCardapioController);
    }

    public boolean registrarPagamento(int idPedido, MetodoPagamento metodo) {
        return pedidoController.registrarPagamento(idPedido, metodo, this.mesaController);
    }

    // IMPORTANTE PARA A TELA DA COZINHA:
    public List<Pedido> listarPedidos() {
        return pedidoController.listarTodosPedidos();
    }

    // =======================================================
    //                  OPERAÇÕES DE RESERVA
    // =======================================================

    public boolean fazerReserva(String telefoneCliente, Mesa mesa, LocalDateTime dataHora, int numeroPessoas) {
        Cliente cliente = clienteController.buscarClientePorTelefone(telefoneCliente);
        if (cliente == null) {
            return false;
        }
        Reserva novaReserva = new Reserva(dataHora, numeroPessoas, cliente, mesa);
        
        // Tenta realizar a reserva no controlador de reservas
        boolean sucesso = reservaController.fazerReserva(novaReserva, mesa);
        
        // CORREÇÃO: Se reservou com sucesso, altera o status da mesa para RESERVADA
        if (sucesso) {
            mesaController.alterarStatusMesa(mesa.getNumero(), StatusMesa.RESERVADA);
        }
        
        return sucesso;
    }

    public boolean cancelarReserva(Reserva reserva) {
        // Tenta cancelar a reserva
        boolean sucesso = reservaController.cancelarReserva(reserva);
        
        // CORREÇÃO: Se cancelou com sucesso, libera a mesa (status LIVRE)
        if (sucesso && reserva.getMesa() != null) {
            mesaController.alterarStatusMesa(reserva.getMesa().getNumero(), StatusMesa.LIVRE);
        }
        
        return sucesso;
    }

    public List<Reserva> listarReservas() {
        return reservaController.listarTodasReservas();
    }

    // =======================================================
    //                      RELATÓRIOS
    // =======================================================

    public String gerarRelatorioVendas(LocalDate inicio, LocalDate fim) {
        // Retorna a String gerada pela classe Relatorio
        return relatorio.gerarVendasPorPeriodo(pedidoController.listarTodosPedidos(), inicio, fim);
    }

    public String gerarRelatorioItensMaisVendidos() {
        // Retorna a String gerada pela classe Relatorio
        return relatorio.gerarItensMaisVendidos(pedidoController.listarTodosPedidos());
    }
}