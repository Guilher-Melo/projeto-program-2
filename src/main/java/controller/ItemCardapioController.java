package controller;

import modelo.ItemCardapio;
import repositorio.IRepositorioItemCardapio;
import java.util.List;

public class ItemCardapioController {

    private IRepositorioItemCardapio repositorioItemCardapio;

    public ItemCardapioController(IRepositorioItemCardapio repositorioItemCardapio) {
        this.repositorioItemCardapio = repositorioItemCardapio;
    }

    public boolean cadastrarItemCardapio(ItemCardapio item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        if (item.getNome() == null || item.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do item não pode ser nulo ou vazio");
        }

        if (item.getPreco() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }

        // Verifica se já existe um item com o mesmo nome
        if (repositorioItemCardapio.buscarPorNome(item.getNome()) != null) {
            return false;
        }

        repositorioItemCardapio.cadastrar(item);
        return true;
    }

    public ItemCardapio buscarItemPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        return repositorioItemCardapio.buscarPorNome(nome);
    }

    public boolean atualizarItem(ItemCardapio itemAtualizado) {
        if (itemAtualizado == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        ItemCardapio itemExistente = repositorioItemCardapio.buscarPorNome(itemAtualizado.getNome());
        if (itemExistente == null) {
            return false;
        }

        repositorioItemCardapio.atualizar(itemAtualizado);
        return true;
    }

    public boolean removerItem(String nome) {
        ItemCardapio item = repositorioItemCardapio.buscarPorNome(nome);
        if (item == null) {
            return false;
        }

        repositorioItemCardapio.remover(item);
        return true;
    }

    public List<ItemCardapio> listarTodosItens() {
        return repositorioItemCardapio.listarTodos();
    }
}
