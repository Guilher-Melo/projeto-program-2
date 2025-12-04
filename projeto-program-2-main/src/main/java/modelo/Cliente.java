package modelo;

import java.util.List;

public class Cliente {

    private String nome;
    private String telefone;
    private String email;

    public Cliente(String nome, String telefone, String email) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double consultarHistorico(List<Pedido> todosPedidos) {
    return todosPedidos.stream()
        .filter(p -> p.getCliente() != null && p.getCliente().getTelefone().equals(this.telefone))
        .mapToDouble(Pedido::getValorTotal)
        .sum();
}
}