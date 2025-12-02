package repositorio;

import java.util.List;
import modelo.Pedido;

public interface IRepositorioPedido {
    void cadastrar(Pedido pedido);
    Pedido buscarPorId(int id);
    void atualizar(Pedido pedido);
    void remover(Pedido pedido);
    List<Pedido> listarTodos();
}