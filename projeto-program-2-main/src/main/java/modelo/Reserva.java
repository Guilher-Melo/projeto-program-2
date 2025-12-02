package modelo;

import java.time.LocalDateTime;

public class Reserva {
    private LocalDateTime dataHora;
    private int numeroPessoas;
    private Cliente cliente;
    private Mesa mesa;

    public Reserva(LocalDateTime dataHora, int numeroPessoas, Cliente cliente, Mesa mesa) {
        this.dataHora = dataHora;
        this.numeroPessoas = numeroPessoas;
        this.cliente = cliente;
        this.mesa = mesa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public int getNumeroPessoas() {
        return numeroPessoas;
    }

    public void setNumeroPessoas(int numeroPessoas) {
        this.numeroPessoas = numeroPessoas;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public void cancelarReserva() {
        // Lógica para cancelar a reserva
        System.out.println(
                "Reserva em nome de " + cliente.getNome() + " para a mesa " + mesa.getNumero() + " foi cancelada.");
    }
}
