package modelo;

public enum MetodoPagamento {
    DINHEIRO(1, "Dinheiro"),
    CARTAO_CREDITO(2, "Cartão de Crédito"),
    CARTAO_DEBITO(3, "Cartão de Débito"),
    PIX(4, "PIX"),
    VALE_REFEICAO(5, "Vale Refeição");

    private final int id;
    private final String nome;

    MetodoPagamento(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static MetodoPagamento getById(int id) {
        for (MetodoPagamento metodo : values()) {
            if (metodo.getId() == id) {
                return metodo;
            }
        }
        throw new IllegalArgumentException("Método de pagamento não encontrado para ID: " + id);
    }
}