package controller;

import java.util.List;

import modelo.Cliente;
import modelo.ItemCardapio;
import modelo.ItemPedido;
import modelo.Mesa;
import modelo.MetodoPagamento;
import modelo.Pagamento;
import modelo.Pedido;
import modelo.StatusMesa;
import modelo.StatusPedido;
import repositorio.IRepositorioPedido;

public class PedidoController {

    private IRepositorioPedido repositorioPedido;
    
    // Dependências necessárias para as regras de negócio
    private MesaController mesaController;
    private ClienteController clienteController;
    private ItemCardapioController itemCardapioController;

    // ✅ CONSTRUTOR ATUALIZADO: Recebe todas as dependências da Fachada
    public PedidoController(IRepositorioPedido repositorioPedido, 
                            MesaController mesaController,
                            ClienteController clienteController,
                            ItemCardapioController itemCardapioController) {
        this.repositorioPedido = repositorioPedido;
        this.mesaController = mesaController;
        this.clienteController = clienteController;
        this.itemCardapioController = itemCardapioController;
    }

    // --- Métodos Básicos (CRUD) ---

    public void cadastrarPedido(Pedido pedido) {
        if (pedido == null) throw new IllegalArgumentException("Pedido não pode ser nula");
        repositorioPedido.cadastrar(pedido);
    }

    public Pedido buscarPedidoPorId(int id) {
        return repositorioPedido.buscarPorId(id);
    }

    public boolean atualizarPedido(Pedido pedidoAtualizado) {
        if (pedidoAtualizado == null) return false;
        if (repositorioPedido.buscarPorId(pedidoAtualizado.getId()) == null) return false;

        repositorioPedido.atualizar(pedidoAtualizado);
        return true;
    }

    public boolean removerPedido(int id) {
        Pedido pedido = repositorioPedido.buscarPorId(id);
        if (pedido == null) return false;
        repositorioPedido.remover(pedido);
        return true;
    }

    public List<Pedido> listarTodosPedidos() {
        return repositorioPedido.listarTodos();
    }
    
    public boolean atualizarStatusPedido(int idPedido, StatusPedido novoStatus) {
       Pedido pedido = repositorioPedido.buscarPorId(idPedido);
       if (pedido == null) return false;
       
       pedido.atualizarStatus(novoStatus);
       repositorioPedido.atualizar(pedido);
       return true;
   }

    // --- MÉTODOS DE REGRA DE NEGÓCIO ---

    // Cria um pedido e já muda o status da mesa para OCUPADA
    public Pedido criarPedido(int numeroMesa, String telefoneCliente) {
        // Usa o controller injetado no construtor
        Mesa mesa = this.mesaController.buscarMesaPorNumero(numeroMesa);
        if (mesa == null) {
            return null;
        }

        // Regra: Só pode abrir pedido em mesa LIVRE ou RESERVADA
        if (mesa.getStatus() != StatusMesa.LIVRE && mesa.getStatus() != StatusMesa.RESERVADA) {
            return null; 
        }

        Cliente cliente = null;
        if (telefoneCliente != null && !telefoneCliente.isEmpty()) {
            cliente = this.clienteController.buscarClientePorTelefone(telefoneCliente);
        }

        Pedido novoPedido = new Pedido(java.time.LocalDateTime.now(), cliente);
        novoPedido.setNumeroMesa(numeroMesa);

        // ✅ ATUALIZAÇÃO IMPORTANTE: Muda a mesa para OCUPADA
        this.mesaController.alterarStatusMesa(numeroMesa, StatusMesa.OCUPADA);

        repositorioPedido.cadastrar(novoPedido);
        return novoPedido;
    }

    // Adiciona item buscando no controller de cardápio injetado
    public boolean adicionarItemAoPedido(int idPedido, String nomeItem, int quantidade) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) return false;

        ItemCardapio itemCardapio = this.itemCardapioController.buscarItemPorNome(nomeItem);
        
        if (itemCardapio == null) return false;
        if (!itemCardapio.isDisponivel()) return false;

        ItemPedido novoItemPedido = new ItemPedido(quantidade, "", itemCardapio);
        pedido.adicionarItem(novoItemPedido);
        pedido.calcularTotal(); // Recalcula o total do pedido
        
        repositorioPedido.atualizar(pedido);
        return true;
    }

    // Registra pagamento e LIBERA A MESA
    public boolean registrarPagamento(int idPedido, MetodoPagamento metodo) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);

        // Permite pagar se estiver Confirmado, Pronto ou Entregue.
        // Só bloqueia se estiver Pendente (ainda anotando), Cancelado ou já Pago.
        if (pedido.getStatus() == StatusPedido.PENDENTE || 
            pedido.getStatus() == StatusPedido.CANCELADO || 
            pedido.getStatus() == StatusPedido.PAGO) {
            return false; 
        }       
        
        if (pedido == null) return false;
        if (pedido.getPagamento() != null) return false; // Já está pago

        double total = pedido.fecharConta();
        Pagamento pagamento = new Pagamento(total, metodo);
        pagamento.processarPagamento();

        pedido.setPagamento(pagamento);
        pedido.atualizarStatus(StatusPedido.ENTREGUE);

        // ✅ LIBERAÇÃO DA MESA
        if (pedido.getNumeroMesa() > 0) {
            System.out.println("DEBUG: Tentando liberar mesa " + pedido.getNumeroMesa());
            
            // Chama o MesaController para mudar para LIVRE
            boolean liberou = this.mesaController.alterarStatusMesa(pedido.getNumeroMesa(), StatusMesa.LIVRE);
            
            if(liberou) {
                System.out.println(">>> Mesa " + pedido.getNumeroMesa() + " liberada com sucesso!");
            } else {
                System.err.println(">>> Erro ao liberar a mesa.");
            }
        }

        repositorioPedido.atualizar(pedido);
        return true;
    }
}