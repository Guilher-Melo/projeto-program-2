package modelo;

public enum StatusMesa {
    LIVRE(1, "Livre"),
    OCUPADA(2, "Ocupada"),
    RESERVADA(3, "Reservada"),
    MANUTENCAO(4, "Em Manutenção");

    private final int id;
    private final String nome;

    StatusMesa(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static StatusMesa getById(int id) {
        for (StatusMesa status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de mesa não encontrado para ID: " + id);
    }
}