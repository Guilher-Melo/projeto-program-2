package modelo;

public enum CategoriaItem {
    ENTRADA(1, "Entrada"),
    PRATO_PRINCIPAL(2, "Prato Principal"),
    SOBREMESA(3, "Sobremesa"),
    BEBIDA(4, "Bebida"),
    BEBIDA_ALCOOLICA(5, "Bebida Alcoólica"),
    LANCHE(6, "Lanche");

    private final int id;
    private final String nome;

    CategoriaItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static CategoriaItem getById(int id) {
        for (CategoriaItem categoria : values()) {
            if (categoria.getId() == id) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria não encontrada para ID: " + id);
    }
}