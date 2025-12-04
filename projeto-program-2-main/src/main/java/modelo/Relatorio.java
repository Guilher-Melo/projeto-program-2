package modelo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Relatorio {

    // --- REQ17: Relatório de Vendas (Já existia, mantido) ---
    public String gerarVendasPorPeriodo(List<Pedido> todosPedidos, LocalDate inicio, LocalDate fim) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Relatório de Vendas ===\n");
        sb.append("Período: ").append(inicio).append(" até ").append(fim).append("\n\n");

        if (todosPedidos == null || todosPedidos.isEmpty()) {
            sb.append("Nenhum pedido registrado.\n");
            return sb.toString();
        }

        double totalVendas = 0;
        int numPedidos = 0;

        for (Pedido pedido : todosPedidos) {
            // Considera apenas pedidos PAGOS ou ENTREGUES para contabilidade
            if (pedido.getStatus() == StatusPedido.PAGO || pedido.getStatus() == StatusPedido.ENTREGUE) {
                LocalDate dataPedido = pedido.getDataHora().toLocalDate();
                
                if (!dataPedido.isBefore(inicio) && !dataPedido.isAfter(fim)) {
                    sb.append(String.format("Pedido #%d - R$ %.2f - %s\n", 
                            pedido.getId(), pedido.getValorTotal(), dataPedido));
                    totalVendas += pedido.getValorTotal();
                    numPedidos++;
                }
            }
        }

        sb.append("\n---------------------------\n");
        sb.append("Total de Pedidos no Período: ").append(numPedidos).append("\n");
        sb.append(String.format("Faturamento Total: R$ %.2f\n", totalVendas));
        
        return sb.toString();
    }

    // --- REQ18: Itens Mais Vendidos ---
    public String gerarItensMaisVendidos(List<Pedido> todosPedidos) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Itens Mais Vendidos ===\n\n");

        if (todosPedidos == null || todosPedidos.isEmpty()) {
            return "Nenhum pedido para analisar.";
        }

        // Mapa para contar: "Nome do Item" -> Quantidade Total
        Map<String, Integer> contagemItens = new HashMap<>();

        for (Pedido p : todosPedidos) {
            // Considera todos os pedidos que não foram CANCELADOS
            if (p.getStatus() != StatusPedido.CANCELADO) {
                for (ItemPedido item : p.getItens()) {
                    String nomeItem = item.getItemCardapio().getNome();
                    int qtd = item.getQuantidade();
                    contagemItens.put(nomeItem, contagemItens.getOrDefault(nomeItem, 0) + qtd);
                }
            }
        }

        // Ordena o mapa por valor (quantidade) de forma decrescente
        List<Map.Entry<String, Integer>> ranking = contagemItens.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());

        if (ranking.isEmpty()) {
            sb.append("Nenhum item vendido ainda.\n");
        } else {
            int posicao = 1;
            for (Map.Entry<String, Integer> entrada : ranking) {
                sb.append(String.format("%dº. %s - %d unidades\n", 
                        posicao++, entrada.getKey(), entrada.getValue()));
            }
        }

        return sb.toString();
    }

    // --- REQ19: Relatório de Ocupação de Mesas ---
    public String gerarRelatorioOcupacao(List<Mesa> todasMesas) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Ocupação Atual das Mesas ===\n\n");

        if (todasMesas == null || todasMesas.isEmpty()) {
            return "Nenhuma mesa cadastrada.";
        }

        int total = todasMesas.size();
        int livres = 0;
        int ocupadas = 0;
        int reservadas = 0;

        for (Mesa m : todasMesas) {
            switch (m.getStatus()) {
                case LIVRE -> livres++;
                case OCUPADA -> ocupadas++;
                case RESERVADA -> reservadas++;
            }
            sb.append(String.format("Mesa %d (%d lug.) - %s\n", 
                    m.getNumero(), m.getCapacidade(), m.getStatus().getNome()));
        }

        double taxaOcupacao = (double) ocupadas / total * 100;

        sb.append("\n---------------------------\n");
        sb.append("Total de Mesas: ").append(total).append("\n");
        sb.append("Livres: ").append(livres).append("\n");
        sb.append("Ocupadas: ").append(ocupadas).append("\n");
        sb.append("Reservadas: ").append(reservadas).append("\n");
        sb.append(String.format("Taxa de Ocupação Atual: %.1f%%\n", taxaOcupacao));

        return sb.toString();
    }

    // --- REQ20: Exportação (CSV ou TXT) ---
    public boolean exportar(String conteudo, String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write(conteudo);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar relatório: " + e.getMessage());
            return false;
        }
    }
}