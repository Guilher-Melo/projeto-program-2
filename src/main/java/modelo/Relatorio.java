package modelo;

import java.time.LocalDate;
import java.util.List;

public class Relatorio {

    public String gerarVendasPorPeriodo(List<Pedido> todosPedidos, LocalDate inicio, LocalDate fim) {
        StringBuilder sb = new StringBuilder(); // Usado para montar o texto
        sb.append("--- Relatório de Vendas de ").append(inicio).append(" a ").append(fim).append(" ---\n\n");

        if (todosPedidos == null || todosPedidos.isEmpty()) {
            sb.append("Nenhum pedido encontrado.\n");
            return sb.toString();
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

        sb.append("Número de pedidos no período: ").append(numPedidos).append("\n");
        sb.append("Valor total de vendas: R$ ").append(String.format("%.2f", totalVendas)).append("\n");
        
        return sb.toString();
    }

    public String gerarItensMaisVendidos(List<Pedido> todosPedidos) {
        return "\n--- Relatório de Itens Mais Vendidos ---\n(Lógica de itens mais vendidos a ser implementada)\n";
    }

    public void exportar(String formato) {
        // Lógica para exportar relatório
    }
}