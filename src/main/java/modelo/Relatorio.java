package modelo;

import java.time.LocalDate;
import java.util.List;

public class Relatorio {

    public void gerarVendasPorPeriodo(List<Pedido> todosPedidos, LocalDate inicio, LocalDate fim) {
        System.out.println("\n--- Relatório de Vendas de " + inicio + " a " + fim + " ---");

        if (todosPedidos == null || todosPedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }

        double totalVendas = 0;
        int numPedidos = 0;

        for (Pedido pedido : todosPedidos) {
            if (pedido.getPagamento() == null) {
                continue;
            }

            LocalDate dataPedido = pedido.getDataHora().toLocalDate();

            if (!dataPedido.isBefore(inicio) && !dataPedido.isAfter(fim)) {
                totalVendas += pedido.getValorTotal();
                numPedidos++;
            }
        }

        System.out.println("Número de pedidos no período: " + numPedidos);
        System.out.println("Valor total de vendas: R$ " + String.format("%.2f", totalVendas));
    }

    public void gerarItensMaisVendidos(List<Pedido> todosPedidos) {
        System.out.println("\n--- Relatório de Itens Mais Vendidos ---");
        System.out.println("(Lógica de itens mais vendidos a ser implementada)");
    }

    public void exportar(String formato) {
        // Lógica para exportar relatório
    }
}