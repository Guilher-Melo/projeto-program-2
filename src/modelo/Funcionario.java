package modelo;

public class Funcionario {

    private String nome;
    private String cargo;

    public Funcionario(String nome, String cargo) {
        this.nome = nome;
        this.cargo = cargo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    // A lógica de criarPedido, adicionarItemPedido e registrarPagamento
    // foi movida para a camada de Negócio (Fachada),
    // pois são regras de negócio e não responsabilidade do "Funcionario".
}