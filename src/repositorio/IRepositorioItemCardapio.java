package repositorio;

import java.util.List;
import modelo.ItemCardapio;

public interface IRepositorioItemCardapio {
    void cadastrar(ItemCardapio item);
    ItemCardapio buscarPorNome(String nome);
    void atualizar(ItemCardapio item);
    void remover(ItemCardapio item);
    List<ItemCardapio> listarTodos();
}