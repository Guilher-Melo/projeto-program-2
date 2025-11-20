package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import modelo.*;
import controller.*;

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

    // Operações de Cliente
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

    public double consultarHistoricoCliente(Cliente cliente) {
        return cliente.consultarHistorico();
    }

    // Operações de Reserva
    public boolean fazerReserva(String telefoneCliente, Mesa mesa, LocalDateTime dataHora, int numeroPessoas) {
        Cliente cliente = clienteController.buscarClientePorTelefone(telefoneCliente);
        if (cliente == null) {
            return false;
        }

        Reserva novaReserva = new Reserva(dataHora, numeroPessoas, cliente, mesa);
        return reservaController.fazerReserva(novaReserva, mesa);
    }

    public boolean cancelarReserva(Reserva reserva) {
        return reservaController.cancelarReserva(reserva);
    }

    // Operações de Mesa
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

    public boolean alterarStatusMesa(int numeroMesa, StatusMesa novoStatus) {
        return mesaController.alterarStatusMesa(numeroMesa, novoStatus);
    }

    // Operações de Pedido
    public Pedido criarPedido(int numeroMesa, String telefoneCliente) {
        return pedidoController.criarPedido(numeroMesa, telefoneCliente, mesaController, clienteController);
    }

    public boolean adicionarItemPedido(int idPedido, String nomeItem, int quantidade) {
        return pedidoController.adicionarItemAoPedido(idPedido, nomeItem, quantidade, itemCardapioController);
    }

    public boolean registrarPagamento(int idPedido, MetodoPagamento metodo) {
        return pedidoController.registrarPagamento(idPedido, metodo);
    }

    // Operações de Cardápio
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

    // Operações de Funcionário
    public boolean cadastrarFuncionario(String nome, String cargo) {
        Funcionario novo = new Funcionario(nome, cargo);
        return funcionarioController.cadastrarFuncionario(novo);
    }

    // Relatórios
    public void gerarRelatorioVendas(LocalDate inicio, LocalDate fim) {
        relatorio.gerarVendasPorPeriodo(pedidoController.listarTodosPedidos(), inicio, fim);
    }

    public void gerarRelatorioItensMaisVendidos() {
        relatorio.gerarItensMaisVendidos(pedidoController.listarTodosPedidos());
    }
}