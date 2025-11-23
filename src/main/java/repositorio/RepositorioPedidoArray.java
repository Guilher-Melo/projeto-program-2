package repositorio;

import modelo.Pedido;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPedidoArray implements IRepositorioPedido {

    private List<Pedido> pedidos;

    public RepositorioPedidoArray() {
        this.pedidos = new ArrayList<>();
    }

    @Override
    public void cadastrar(Pedido pedido) {
        this.pedidos.add(pedido);
    }

    @Override
    public Pedido buscarPorId(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Pedido pedidoAtualizado) {
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId() == pedidoAtualizado.getId()) {
                pedidos.set(i, pedidoAtualizado);
                break;
            }
        }
    }

    @Override
    public void remover(Pedido pedido) {
        this.pedidos.remove(pedido);
    }

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(this.pedidos);
    }
}