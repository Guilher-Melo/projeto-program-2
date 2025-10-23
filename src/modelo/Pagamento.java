package modelo;

import java.time.LocalDateTime;

public class Pagamento {

    private LocalDateTime dataHora;
    private double valor;
    private MetodoPagamento MetodoPagamento;

    public Pagamento(double valor, MetodoPagamento MetodoPagamento) {
        this.dataHora = LocalDateTime.now();
        this.valor = valor;
        this.MetodoPagamento = MetodoPagamento;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public MetodoPagamento getMetodoPagamento() {
        return MetodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento MetodoPagamento) {
        this.MetodoPagamento = MetodoPagamento;
    }

    public void processarPagamento() {
        System.out.println("Pagamento de R$ " + String.format("%.2f", valor)
                + " via " + MetodoPagamento.getNome()
                + " processado em " + dataHora);
    }
}


