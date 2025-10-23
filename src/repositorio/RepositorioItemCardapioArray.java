package repositorio;

import java.util.ArrayList;
import java.util.List;
import modelo.ItemCardapio;

public class RepositorioItemCardapioArray implements IRepositorioItemCardapio {

    private List<ItemCardapio> itens;

    public RepositorioItemCardapioArray() {
        this.itens = new ArrayList<>();
    }

    @Override
    public void cadastrar(ItemCardapio item) {
        if (buscarPorNome(item.getNome()) == null) {
            this.itens.add(item);
        }
    }

    @Override
    public ItemCardapio buscarPorNome(String nome) {
        for (ItemCardapio item : itens) {
            if (item.getNome().equalsIgnoreCase(nome)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void atualizar(ItemCardapio itemAtualizado) {
        ItemCardapio itemAntigo = buscarPorNome(itemAtualizado.getNome());
        if (itemAntigo != null) {
            // Remove o antigo e adiciona o novo
            // (Numa implementação real, talvez atualizássemos os campos)
            itens.remove(itemAntigo);
            itens.add(itemAtualizado);
        }
    }

    @Override
    public void remover(ItemCardapio item) {
        this.itens.remove(item);
    }

    @Override
    public List<ItemCardapio> listarTodos() {
        return new ArrayList<>(this.itens);
    }
}