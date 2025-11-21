package controller;

import modelo.Pedido;
import modelo.ItemPedido;
import modelo.StatusPedido;
import modelo.StatusMesa;
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
        modelo.Mesa mesa = mesaController.buscarMesaPorNumero(numeroMesa);
        if (mesa == null) {
            return null;
        }

        modelo.Cliente cliente = null;
        if (telefoneCliente != null && !telefoneCliente.isEmpty()) {
            cliente = clienteController.buscarClientePorTelefone(telefoneCliente);
        }

        if (mesa.getStatus() != modelo.StatusMesa.LIVRE) {
            return null;
        }

        modelo.Pedido novoPedido = new modelo.Pedido(java.time.LocalDateTime.now(), cliente);

        novoPedido.setNumeroMesa(numeroMesa);

        repositorioPedido.cadastrar(novoPedido);
        mesaController.alterarStatusMesa(numeroMesa, modelo.StatusMesa.OCUPADA);

        return novoPedido;
    }

    public boolean adicionarItemAoPedido(int idPedido, String nomeItem, int quantidade,
            ItemCardapioController itemCardapioController) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            return false;
        }

        modelo.ItemCardapio itemCardapio = itemCardapioController.buscarItemPorNome(nomeItem);
        if (itemCardapio == null) {
            return false;
        }

        if (!itemCardapio.isDisponivel()) {
            return false;
        }

        modelo.ItemPedido novoItemPedido = new modelo.ItemPedido(quantidade, "", itemCardapio);
        pedido.adicionarItem(novoItemPedido);
        pedido.calcularTotal();
        repositorioPedido.atualizar(pedido);
        return true;
    }

    public boolean registrarPagamento(int idPedido, modelo.MetodoPagamento metodo, MesaController mesaController) {
        Pedido pedido = repositorioPedido.buscarPorId(idPedido);
        if (pedido == null) {
            return false;
        }

        if (pedido.getPagamento() != null) {
            return false;
        }

        double total = pedido.fecharConta();
        modelo.Pagamento pagamento = new modelo.Pagamento(total, metodo);
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