package modelo;

public enum StatusPedido {
    PENDENTE(1, "Pendente"),
    CONFIRMADO(2, "Confirmado"),
    EM_PREPARO(3, "Em Preparo"),
    PRONTO(4, "Pronto"),
    ENTREGUE(5, "Entregue"),
    CANCELADO(6, "Cancelado"),
    PAGO(7, "Pago");

    private final int id;
    private final String nome;

    StatusPedido(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static StatusPedido getById(int id) {
        for (StatusPedido status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pedido não encontrado para ID: " + id);
    }
}