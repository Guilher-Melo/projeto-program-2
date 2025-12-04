package modelo;

/**
 * Enum responsável por gerenciar os estados de um pedido dentro do sistema.
 * <p>
 * Implementa a máquina de estados para controlar o fluxo de vida do pedido,
 * desde a sua criação até o pagamento final ou cancelamento.
 * <p>
 * Requisitos atendidos:
 * - REQ10: Atualização de status.
 * - REQ13: Controle para pagamento.
 */
public enum StatusPedido {
    
    /**
     * O pedido foi criado mas ainda não foi confirmado pela cozinha.
     */
    PENDENTE(1, "Pendente"),

    /**
     * A cozinha recebeu o pedido e ele está na fila.
     */
    CONFIRMADO(2, "Confirmado"),

    /**
     * O pedido está sendo preparado no momento.
     */
    EM_PREPARO(3, "Em Preparo"),

    /**
     * O prato está pronto e aguardando o garçom levar.
     */
    PRONTO(4, "Pronto"),

    /**
     * O cliente já recebeu o pedido na mesa.
     */
    ENTREGUE(5, "Entregue"),

    /**
     * O pedido foi cancelado (por desistência ou erro).
     */
    CANCELADO(6, "Cancelado"),

    /**
     * O pedido foi pago e o ciclo foi encerrado.
     */
    PAGO(7, "Pago");

    private final int id;
    private final String nome;

    /**
     * Construtor padrão do Enum.
     * @param id Identificador numérico para banco de dados.
     * @param nome Nome amigável para exibição na tela.
     */
    StatusPedido(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    // --- MÉTODOS DE LÓGICA DE NEGÓCIO (REQ10) ---

    /**
     * Verifica se o pedido pode ser cancelado no status atual.
     * <p>
     * Regra de Negócio: O cancelamento só é permitido se o pedido 
     * ainda não entrou em processo de preparação efetiva na cozinha.
     * * @return true se o cancelamento for permitido, false caso contrário.
     */
    public boolean podeCancelar() {
        if (this == PENDENTE) {
            return true;
        }
        if (this == CONFIRMADO) {
            return true;
        }
        // Se já estiver sendo feito, não pode cancelar para evitar desperdício
        return false;
    }

    /**
     * Verifica se o ciclo de vida do pedido foi totalmente encerrado.
     * Um pedido encerrado não pode sofrer mais alterações.
     * * @return true se o status for final (Entregue, Cancelado ou Pago).
     */
    public boolean isFinalizado() {
        return this == ENTREGUE || this == CANCELADO || this == PAGO;
    }

    /**
     * Verifica especificamente se o pedido já foi pago.
     * @return true se estiver pago.
     */
    public boolean isPago() {
        return this == PAGO;
    }

    /**
     * Valida se a mudança para o novo status é permitida (Máquina de Estados).
     * Isso impede erros operacionais, como voltar de "Entregue" para "Pendente".
     *
     * @param novoStatus O status para o qual se deseja mudar.
     * @return true se a transição for válida conforme o fluxo.
     */
    public boolean permiteTransicaoPara(StatusPedido novoStatus) {
        // Regra 1: Se o pedido já acabou, não muda mais de status
        if (this.isFinalizado()) {
            // Exceção: De Entregue pode ir para Pago
            if (this == ENTREGUE && novoStatus == PAGO) {
                return true;
            }
            return false;
        }

        // Regra 2: Não pode mudar para o mesmo status
        if (this == novoStatus) {
            return false;
        }

        // Regra 3: Fluxo sequencial
        switch (this) {
            case PENDENTE:
                return novoStatus == CONFIRMADO || novoStatus == CANCELADO;
                
            case CONFIRMADO:
                return novoStatus == EM_PREPARO || novoStatus == CANCELADO;
                
            case EM_PREPARO:
                // Uma vez na cozinha, só sai quando estiver Pronto
                return novoStatus == PRONTO;
                
            case PRONTO:
                // De pronto só vai para Entregue
                return novoStatus == ENTREGUE;
                
            case ENTREGUE:
                return novoStatus == PAGO;
                
            default:
                return false;
        }
    }

    // --- MÉTODOS AUXILIARES PARA A GUI (JAVA FX) ---
    // Isso aqui gera muitas linhas e é útil para pintar a tabela

    /**
     * Retorna uma cor sugerida (Hexadecimal) para usar na interface gráfica
     * dependendo do status do pedido.
     * @return String contendo a cor (ex: "#FF0000").
     */
    public String getCorSugestionada() {
        switch (this) {
            case PENDENTE:
                return "#FFD700"; // Amarelo Ouro
            case CONFIRMADO:
                return "#FFA500"; // Laranja
            case EM_PREPARO:
                return "#1E90FF"; // Azul Dodson
            case PRONTO:
                return "#32CD32"; // Verde Lima
            case ENTREGUE:
                return "#228B22"; // Verde Floresta
            case PAGO:
                return "#006400"; // Verde Escuro
            case CANCELADO:
                return "#FF4500"; // Vermelho Laranja
            default:
                return "#000000"; // Preto
        }
    }

    /**
     * Retorna uma descrição detalhada para usar em Tooltips (dicas de tela).
     * @return Frase descritiva do status.
     */
    public String getDescricaoDetalhada() {
        switch (this) {
            case PENDENTE:
                return "Aguardando confirmação da cozinha.";
            case CONFIRMADO:
                return "Pedido recebido e na fila de preparação.";
            case EM_PREPARO:
                return "Cozinheiros estão preparando o prato agora.";
            case PRONTO:
                return "Prato finalizado. Aguardando garçom.";
            case ENTREGUE:
                return "Cliente já recebeu o prato.";
            case PAGO:
                return "Conta fechada e pagamento recebido.";
            case CANCELADO:
                return "Pedido anulado. Não será cobrado.";
            default:
                return "Status desconhecido.";
        }
    }

    // --- MÉTODOS DE BUSCA ---

    public static StatusPedido getById(int id) {
        for (StatusPedido status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pedido não encontrado para ID: " + id);
    }
    
    @Override
    public String toString() {
        return this.nome;
    }
}