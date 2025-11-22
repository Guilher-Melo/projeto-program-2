package modelo;

public class CardapioItem {
    private double precoEspecial;
    private Cardapio cardapio;
    private ItemCardapio itemCardapio;

    public CardapioItem(double precoEspecial, Cardapio cardapio, ItemCardapio itemCardapio) {
        this.precoEspecial = precoEspecial;
        this.cardapio = cardapio;
        this.itemCardapio = itemCardapio;
    }

    public double getPrecoEspecial() {
        return precoEspecial;
    }

    public void setPrecoEspecial(double precoEspecial) {
        this.precoEspecial = precoEspecial;
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    public void setCardapio(Cardapio cardapio) {
        this.cardapio = cardapio;
    }

    public ItemCardapio getItemCardapio() {
        return itemCardapio;
    }

    public void setItemCardapio(ItemCardapio itemCardapio) {
        this.itemCardapio = itemCardapio;
    }
}
