package controller;

import modelo.Pedido;
import modelo.ItemPedido;
import modelo.StatusPedido;
import modelo.StatusMesa;
import modelo.Mesa;
import modelo.Cliente;
import modelo.ItemCardapio;
import modelo.MetodoPagamento;
import modelo.Pagamento;
import repositorio.IRepositorioPedido;
import java.util.List;

public class PedidoController {

    private IRepositorioPedido repositorioPedido;

    public PedidoController(IRepositorioPedido repositorioPedido) {
        this.repositorioPedido = repositorioPedido;
    }

    public void cadastrarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        repositorioPedido.cadastrar(pedido);
    }

    public Pedido buscarPedidoPorId(int id) {
        return repositorioPedido.buscarPorId(id);
    }

    public boolean atualizarPedido(Pedido pedidoAtualizado) {
        if (pedidoAtualizado == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }

        Pedido pedidoExistente = repositorioPedido.buscarPorId(pedidoAtualizado.getId());
        if (pedidoExistente == null) {
            return false;
        }

        repositorioPedido.atualizar(pedidoAtualizado);
        return true;
    }

    public boolean adicionarItemAoPedido(int idPedido, ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            return false;
        }

        pedido.adicionarItem(item);
        pedido.calcularTotal();
        repositorioPedido.atualizar(pedido);
        return true;
    }

    public boolean atualizarStatusPedido(int idPedido, StatusPedido novoStatus) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            return false;
        }

        pedido.atualizarStatus(novoStatus);
        repositorioPedido.atualizar(pedido);
        return true;
    }

    public boolean removerPedido(int id) {
        Pedido pedido = repositorioPedido.buscarPorId(id);
        if (pedido == null) {
            return false;
        }

        repositorioPedido.remover(pedido);
        return true;
    }

    public List<Pedido> listarTodosPedidos() {
        return repositorioPedido.listarTodos();
    }

    public Pedido criarPedido(int numeroMesa, String telefoneCliente,
            MesaController mesaController,
            ClienteController clienteController) {
        Mesa mesa = mesaController.buscarMesaPorNumero(numeroMesa);
        if (mesa == null) {
            return null;
        }

        Cliente cliente = null;
        if (telefoneCliente != null && !telefoneCliente.isEmpty()) {
            cliente = clienteController.buscarClientePorTelefone(telefoneCliente);
        }

        // Permite criar pedido apenas para mesas LIVRES ou RESERVADAS
        if (mesa.getStatus() != StatusMesa.LIVRE && mesa.getStatus() != StatusMesa.RESERVADA) {
            return null;
        }

        Pedido novoPedido = new Pedido(java.time.LocalDateTime.now(), cliente);

        novoPedido.setNumeroMesa(numeroMesa);

        repositorioPedido.cadastrar(novoPedido);
        // Apenas muda para OCUPADA se estava LIVRE ou RESERVADA
        mesaController.alterarStatusMesa(numeroMesa, StatusMesa.OCUPADA);

        return novoPedido;
    }

    public boolean adicionarItemAoPedido(int idPedido, String nomeItem, int quantidade,
            ItemCardapioController itemCardapioController) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            return false;
        }

        ItemCardapio itemCardapio = itemCardapioController.buscarItemPorNome(nomeItem);
        if (itemCardapio == null) {
            return false;
        }

        if (!itemCardapio.isDisponivel()) {
            return false;
        }

        ItemPedido novoItemPedido = new ItemPedido(quantidade, "", itemCardapio);
        pedido.adicionarItem(novoItemPedido);
        pedido.calcularTotal();
        repositorioPedido.atualizar(pedido);
        return true;
    }

    public boolean registrarPagamento(int idPedido, MetodoPagamento metodo, MesaController mesaController) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            return false;
        }

        if (pedido.getPagamento() != null) {
            return false;
        }

        double total = pedido.fecharConta();
        Pagamento pagamento = new Pagamento(total, metodo);
        pagamento.processarPagamento();

        pedido.setPagamento(pagamento);
        pedido.atualizarStatus(StatusPedido.ENTREGUE);

        if (mesaController != null && pedido.getNumeroMesa() > 0) {
            mesaController.alterarStatusMesa(pedido.getNumeroMesa(), StatusMesa.LIVRE);
            System.out.println(">>> Mesa " + pedido.getNumeroMesa() + " liberada com sucesso após pagamento.");
        }

        repositorioPedido.atualizar(pedido);
        return true;
    }
}