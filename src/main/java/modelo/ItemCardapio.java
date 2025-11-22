package modelo;

import java.util.ArrayList;
import java.util.List;

public class ItemCardapio {

    private String nome;
    private String descricao;
    private double preco;
    private boolean disponivel;
    private CategoriaItem categoria;
    private List<CardapioItem> cardapioItens;

    public ItemCardapio(String nome, String descricao, double preco, CategoriaItem categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.disponivel = true;
        this.categoria = categoria;
        this.cardapioItens = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public CategoriaItem getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaItem categoria) {
        this.categoria = categoria;
    }

    public List<CardapioItem> getCardapioItens() {
        return cardapioItens;
    }

    public void atualizarDisponibilidade(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public void adicionarCardapioItem(CardapioItem cardapioItem) {
        if (cardapioItem != null && !cardapioItens.contains(cardapioItem)) {
            cardapioItens.add(cardapioItem);
            cardapioItem.setItemCardapio(this);
        }
    }

    public void removerCardapioItem(CardapioItem cardapioItem) {
        if (cardapioItem != null && cardapioItens.contains(cardapioItem)) {
            cardapioItens.remove(cardapioItem);
            cardapioItem.setItemCardapio(null);
        }
    }
}