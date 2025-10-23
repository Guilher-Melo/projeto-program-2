package negocio;

import java.time.Duration; // Importa todos os modelos
import java.time.LocalDate; // Importa todas as interfaces de repositório
import java.time.LocalDateTime;
import java.util.List;
import modelo.*;
import repositorio.*;

public class Fachada {

    private IRepositorioCliente repositorioCliente;
    private IRepositorioReserva repositorioReserva;

    // --- ADICIONADO ---
    private IRepositorioMesa repositorioMesa;
    private IRepositorioPedido repositorioPedido;
    private IRepositorioItemCardapio repositorioItemCardapio;
    private IRepositorioFuncionario repositorioFuncionario;
    private Relatorio relatorio; // Objeto para lidar com relatórios

    // --- CONSTRUTOR MODIFICADO ---
    public Fachada(IRepositorioCliente repositorioCliente,
            IRepositorioReserva repositorioReserva,
            IRepositorioMesa repositorioMesa,
            IRepositorioPedido repositorioPedido,
            IRepositorioItemCardapio repositorioItemCardapio,
            IRepositorioFuncionario repositorioFuncionario) {

        this.repositorioCliente = repositorioCliente;
        this.repositorioReserva = repositorioReserva;
   
        this.repositorioMesa = repositorioMesa;
        this.repositorioPedido = repositorioPedido;
        this.repositorioItemCardapio = repositorioItemCardapio;
        this.repositorioFuncionario = repositorioFuncionario;
        this.relatorio = new Relatorio(); 
    }

    public void cadastrarCliente(String nome, String telefone, String email) {
        Cliente clienteExistente = this.repositorioCliente.buscarPorTelefone(telefone);

        if (clienteExistente != null) {
            System.err.println("Erro (REQ01): Já existe um cliente com o telefone " + telefone);
            return;
        }

        Cliente novoCliente = new Cliente(nome, telefone, email);
        this.repositorioCliente.cadastrar(novoCliente);
        System.out.println("Cliente " + nome + " cadastrado com sucesso.");
    }

    public Cliente buscarClientePorTelefone(String telefone) {
        return this.repositorioCliente.buscarPorTelefone(telefone);
    }

    public double consultarHistoricoCliente(Cliente cliente) {
        System.out.println("Consultando histórico do cliente: " + cliente.getNome());
        return cliente.consultarHistorico();
    }

    public void fazerReserva(String telefoneCliente, Mesa mesa, LocalDateTime dataHora, int numeroPessoas) {
        Cliente cliente = this.repositorioCliente.buscarPorTelefone(telefoneCliente);

        if (cliente == null) {
            System.err.println("Erro (REQ14): Cliente não encontrado.");
            return;
        }

        if (numeroPessoas > mesa.getCapacidade()) {
            System.err.println("Erro (REQ23): Número de pessoas (" + numeroPessoas +
                    ") excede a capacidade da mesa (" + mesa.getCapacidade() + ").");
            return;
        }

        if (!mesa.getStatus().getNome().equals("Livre")) {
            System.err.println("Erro (REQ14): Mesa " + mesa.getNumero() + " não está 'Livre'.");
            return;
        }

        Reserva novaReserva = new Reserva(dataHora, numeroPessoas, cliente, mesa);
        this.repositorioReserva.cadastrar(novaReserva);

        System.out.println("Sucesso (REQ14): Reserva para " + cliente.getNome() +
                " na Mesa " + mesa.getNumero() + " agendada.");
    }

    public void cancelarReserva(Reserva reserva) {
        LocalDateTime agora = LocalDateTime.now();
        Duration duracao = Duration.between(agora, reserva.getDataHora());

        if (duracao.toHours() < 1) {
            System.err.println("Erro (REQ16): Cancelamento não permitido. " +
                    "A reserva é em menos de 1 hora.");
            return;
        }

        this.repositorioReserva.remover(reserva);

        System.out.println("Sucesso (REQ16): Reserva para " + reserva.getCliente().getNome() + " foi cancelada.");
    }


    public void cadastrarMesa(int numero, int capacidade) {
        Mesa mesaExistente = this.repositorioMesa.buscarPorNumero(numero);
        if (mesaExistente != null) {
            System.err.println("Erro: Já existe uma mesa com o número " + numero);
            return;
        }
        Mesa novaMesa = new Mesa(numero, capacidade, StatusMesa.LIVRE); //
        this.repositorioMesa.cadastrar(novaMesa);
        System.out.println("Mesa " + numero + " cadastrada.");
    }

    public Mesa buscarMesa(int numero) {
        return this.repositorioMesa.buscarPorNumero(numero);
    }

    public void alterarStatusMesa(int numeroMesa, StatusMesa novoStatus) {
        Mesa mesa = this.repositorioMesa.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            System.err.println("Erro: Mesa " + numeroMesa + " não encontrada.");
            return;
        }
        mesa.alterarStatus(novoStatus); //
        this.repositorioMesa.atualizar(mesa);
        System.out.println("Status da Mesa " + numeroMesa + " alterado para " + novoStatus.getNome());
    }



    public Pedido criarPedido(int numeroMesa, String telefoneCliente) {
        Mesa mesa = this.repositorioMesa.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            System.err.println("Erro: Mesa não encontrada.");
            return null;
        }
        
        Cliente cliente = null;
        if (telefoneCliente != null && !telefoneCliente.isEmpty()) {
                cliente = this.repositorioCliente.buscarPorTelefone(telefoneCliente);
                if (cliente == null) {
                    System.out.println("Aviso: Cliente não cadastrado. Pedido seguirá sem cliente.");
                }
        }

        if (mesa.getStatus() != StatusMesa.LIVRE) {
            System.err.println("Erro: Mesa " + numeroMesa + " não está 'Livre'. Status atual: " + mesa.getStatus().getNome());
            return null;
        }

        Pedido novoPedido = new Pedido(LocalDateTime.now(), cliente); //

        
        this.repositorioPedido.cadastrar(novoPedido);
        

        alterarStatusMesa(numeroMesa, StatusMesa.OCUPADA);
        
        System.out.println("Pedido " + novoPedido.getId() + " criado para a mesa " + numeroMesa);
        return novoPedido;
    }

    public void adicionarItemPedido(int idPedido, String nomeItem, int quantidade) {
        Pedido pedido = this.repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            System.err.println("Erro: Pedido " + idPedido + " não encontrado.");
            return;
        }

        ItemCardapio itemCardapio = this.repositorioItemCardapio.buscarPorNome(nomeItem);
        if (itemCardapio == null) {
            System.err.println("Erro: Item '" + nomeItem + "' não encontrado no cardápio.");
            return;
        }


        if (!itemCardapio.isDisponivel()) { 
            System.err.println("Erro: Item '" + nomeItem + "' não está disponível.");
            return;
        }

        ItemPedido novoItemPedido = new ItemPedido(quantidade, "", itemCardapio);
        pedido.adicionarItem(novoItemPedido); 
        pedido.calcularTotal(); 
        this.repositorioPedido.atualizar(pedido);

        System.out.println(quantidade + "x " + nomeItem + " adicionado(s) ao pedido " + idPedido);
    }

    public void registrarPagamento(int idPedido, MetodoPagamento metodo) {
        Pedido pedido = this.repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            System.err.println("Erro: Pedido " + idPedido + " não encontrado.");
            return;
        }
        

        if(pedido.getPagamento() != null) {
                System.err.println("Erro: Pedido " + idPedido + " já foi pago.");
                return;
        }

        double total = pedido.fecharConta(); 
        Pagamento pagamento = new Pagamento(total, metodo); 
        pagamento.processarPagamento(); 
        
        pedido.setPagamento(pagamento);
        pedido.atualizarStatus(StatusPedido.ENTREGUE); 
        this.repositorioPedido.atualizar(pedido);

        System.out.println("Pagamento de R$ " + total + " para o pedido " + idPedido + " registrado.");
        

    }


    public void cadastrarItemCardapio(String nome, String desc, double preco, CategoriaItem categoria) {
        if (this.repositorioItemCardapio.buscarPorNome(nome) != null) {
            System.err.println("Erro: Item de cardápio '" + nome + "' já existe.");
            return;
        }
        ItemCardapio novoItem = new ItemCardapio(nome, desc, preco, categoria); //
        this.repositorioItemCardapio.cadastrar(novoItem);
        System.out.println("Item '" + nome + "' cadastrado no cardápio.");
    }
    
    public void cadastrarFuncionario(String nome, String cargo) {
            if (this.repositorioFuncionario.buscarPorNome(nome) != null) {
            System.err.println("Erro: Funcionario '" + nome + "' já existe.");
            return;
        }
        Funcionario novo = new Funcionario(nome, cargo); //
        this.repositorioFuncionario.cadastrar(novo);
        System.out.println("Funcionario '" + nome + "' cadastrado.");
    }

    
    
    public void gerarRelatorioVendas(LocalDate inicio, LocalDate fim) {

        List<Pedido> todosPedidos = this.repositorioPedido.listarTodos();
        

        System.out.println("\n--- Relatório de Vendas de " + inicio + " a " + fim + " ---");
        
        if (todosPedidos == null || todosPedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }

        double totalVendas = 0;
        int numPedidos = 0;

        for (Pedido pedido : todosPedidos) {

            if (pedido.getPagamento() == null) {
                continue;
            }
            
            LocalDate dataPedido = pedido.getDataHora().toLocalDate();

            if (!dataPedido.isBefore(inicio) && !dataPedido.isAfter(fim)) {
                totalVendas += pedido.getValorTotal();
                numPedidos++;
            }
        }

        System.out.println("Número de pedidos no período: " + numPedidos);
        System.out.println("Valor total de vendas: R$ " + String.format("%.2f", totalVendas));

        this.relatorio.gerarVendasPorPeriodo(); 
    }

    public void gerarRelatorioItensMaisVendidos() {
        List<Pedido> todosPedidos = this.repositorioPedido.listarTodos();
        System.out.println("\n--- Relatório de Itens Mais Vendidos ---");

        this.relatorio.gerarItensMaisVendidos(); 
        System.out.println("(Lógica de itens mais vendidos a ser implementada em Relatorio.java)");
    }
}