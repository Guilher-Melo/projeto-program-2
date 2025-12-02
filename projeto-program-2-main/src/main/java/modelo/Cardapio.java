package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cardapio {
    private String nome;
    private String descricao;
    private boolean disponivel;
    private List<CardapioItem> cardapioItens;

    public Cardapio(String nome, String descricao, boolean disponivel) {
        this.nome = nome;
        this.descricao = descricao;
        this.disponivel = disponivel;
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

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public List<CardapioItem> getCardapioItens() {
        return cardapioItens;
    }

    public void adicionarCardapioItem(CardapioItem cardapioItem) {
        if (cardapioItem != null && !cardapioItens.contains(cardapioItem)) {
            cardapioItens.add(cardapioItem);
            cardapioItem.setCardapio(this);
        }
    }

    public void removerCardapioItem(CardapioItem cardapioItem) {
        if (cardapioItem != null && cardapioItens.contains(cardapioItem)) {
            cardapioItens.remove(cardapioItem);
            cardapioItem.setCardapio(null);
        }
    }
}
