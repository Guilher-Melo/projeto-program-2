package negocio;

import modelo.Pedido;
import modelo.StatusPedido;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe responsável pela lógica de negócio referente à divisão de contas (REQ13).
 * Utiliza BigDecimal para garantir precisão monetária e evitar erros de arredondamento.
 */
public class CalculadoraDivisaoConta {

    // Taxa de serviço padrão (10%)
    private static final BigDecimal TAXA_SERVICO_PERCENTUAL = new BigDecimal("0.10");

    /**
     * Divide o valor total do pedido igualmente entre o número de pessoas informado.
     * * @param pedido O pedido a ser pago.
     * @param numeroPessoas A quantidade de pessoas que dividirão a conta.
     * @param incluirServico Se deve ou não incluir os 10% do garçom.
     * @return O valor exato que cada pessoa deve pagar.
     */
    public double dividirContaIgualmente(Pedido pedido, int numeroPessoas, boolean incluirServico) {
        // 1. Validações de segurança (Programação Defensiva)
        validarPedidoParaPagamento(pedido);
        
        if (numeroPessoas <= 0) {
            throw new IllegalArgumentException("O número de pessoas para divisão deve ser maior que zero.");
        }

        // 2. Obtém o valor total do pedido
        BigDecimal valorTotal = BigDecimal.valueOf(pedido.getValorTotal());

        // 3. Aplica a taxa de serviço se solicitado
        if (incluirServico) {
            valorTotal = aplicarTaxaServico(valorTotal);
        }

        // 4. Realiza a divisão
        // RoundingMode.HALF_UP arredonda para cima (ex: 2.555 -> 2.56)
        BigDecimal valorPorPessoa = valorTotal.divide(
                BigDecimal.valueOf(numeroPessoas), 
                2, 
                RoundingMode.HALF_UP
        );

        return valorPorPessoa.doubleValue();
    }

    /**
     * Calcula o valor restante a ser pago caso alguém já tenha pago uma parte.
     * Útil para divisões desproporcionais (Ex: "Eu pago 50, vocês dividem o resto").
     * * @param pedido O pedido total.
     * @param valorJaPago O montante que já foi quitado.
     * @param pessoasRestantes O número de pessoas que vão dividir o saldo.
     * @param incluirServico Se a taxa de serviço incide sobre o total.
     * @return O valor para cada uma das pessoas restantes.
     */
    public double dividirRestante(Pedido pedido, double valorJaPago, int pessoasRestantes, boolean incluirServico) {
        validarPedidoParaPagamento(pedido);

        if (valorJaPago < 0) {
            throw new IllegalArgumentException("O valor já pago não pode ser negativo.");
        }

        BigDecimal valorTotal = BigDecimal.valueOf(pedido.getValorTotal());

        if (incluirServico) {
            valorTotal = aplicarTaxaServico(valorTotal);
        }

        BigDecimal valorPagoBig = BigDecimal.valueOf(valorJaPago);
        BigDecimal saldoRestante = valorTotal.subtract(valorPagoBig);

        // Se já foi tudo pago (ou pago a mais), retorna zero
        if (saldoRestante.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.00;
        }

        if (pessoasRestantes <= 0) {
             throw new IllegalArgumentException("Não há pessoas suficientes para dividir o saldo restante.");
        }

        BigDecimal valorIndividualRestante = saldoRestante.divide(
                BigDecimal.valueOf(pessoasRestantes), 
                2, 
                RoundingMode.HALF_UP
        );

        return valorIndividualRestante.doubleValue();
    }

    /**
     * Apenas calcula quanto seria os 10% de um pedido específico, para exibição na tela.
     */
    public double calcularValorDaTaxa(double valorTotal) {
        if (valorTotal < 0) {
            return 0.0;
        }
        BigDecimal total = BigDecimal.valueOf(valorTotal);
        BigDecimal taxa = total.multiply(TAXA_SERVICO_PERCENTUAL);
        
        // Retorna arredondado
        return taxa.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // --- MÉTODOS AUXILIARES PRIVADOS (ENCAPSULAMENTO) ---

    private BigDecimal aplicarTaxaServico(BigDecimal valorOriginal) {
        // Fórmula: Valor + (Valor * 0.10)
        BigDecimal valorDaTaxa = valorOriginal.multiply(TAXA_SERVICO_PERCENTUAL);
        return valorOriginal.add(valorDaTaxa);
    }

    private void validarPedidoParaPagamento(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido informado é inválido (nulo).");
        }
        
        // Impede divisão de conta se o pedido já foi cancelado (usando a lógica do REQ10)
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Não é possível processar pagamentos de um pedido CANCELADO.");
        }

        // Impede divisão de conta vazia (valor zerado)
        if (pedido.getValorTotal() <= 0) {
             throw new IllegalStateException("O pedido não possui valor a ser cobrado.");
        }
    }
}