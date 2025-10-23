import java.time.LocalDate; // Importa todos os modelos
import modelo.*;
import negocio.Fachada; // Importa todos os repositórios
import repositorio.*;

public class Main {
    public static void main(String[] args) {

        // --- 1. Configuração da Fachada com TODOS os repositórios ---

        // Instancia os repositórios
        IRepositorioCliente repoCliente = new RepositorioClienteArray();
        IRepositorioReserva repoReserva = new RepositorioReservaArray();
        IRepositorioMesa repoMesa = new RepositorioMesaArray();
        IRepositorioPedido repoPedido = new RepositorioPedidoArray();
        IRepositorioItemCardapio repoItem = new RepositorioItemCardapioArray();
        IRepositorioFuncionario repoFunc = new RepositorioFuncionarioArray();

        Fachada fachada = new Fachada(
                repoCliente,
                repoReserva,
                repoMesa,
                repoPedido,
                repoItem,
                repoFunc);

        System.out.println("--- Sistema de Restaurante Iniciado ---");
        System.out.println("\n--- Teste do Pacote 2: Clientes e Reservas (Existente) ---");
        fachada.cadastrarCliente("Guilherme Melo", "98888-0001", "gui@email.com");
        fachada.cadastrarCliente("Maria Silva", "97777-0002", "maria@email.com");

        // --- 2. Testes do Pacote 1: Preparação (Cadastro de Itens e Mesas) ---
        System.out.println("\n--- Teste do Pacote 1: Cadastros Iniciais ---");

        // Cadastrando mesas
        fachada.cadastrarMesa(1, 4); // Mesa 1, 4 pessoas
        fachada.cadastrarMesa(2, 2); // Mesa 2, 2 pessoas

        // Cadastrando itens no cardápio
        fachada.cadastrarItemCardapio("Coca-Cola Lata", "Refrigerante", 6.00, CategoriaItem.BEBIDA);
        fachada.cadastrarItemCardapio("X-Burger", "Pão, carne, queijo", 22.00, CategoriaItem.LANCHE);
        fachada.cadastrarItemCardapio("Batata Frita", "Porção média", 15.00, CategoriaItem.ENTRADA);

        // --- 3. Testes do Pacote 1: Simulação de Pedido ---
        System.out.println("\n--- Teste do Pacote 1: Simulação de Pedido (Mesa 1) ---");

        // 3.1. Cliente chega, funcionário cria o pedido para a Mesa 1
        System.out.println("\nCriando pedido para Mesa 1 (Cliente Guilherme)...");
        // O cliente "Guilherme" tem o telefone "98888-0001"
        Pedido pedidoMesa1 = fachada.criarPedido(1, "98888-0001"); //

        if (pedidoMesa1 != null) {
            int idPedido = pedidoMesa1.getId(); //
            System.out.println("Status da Mesa 1: " + fachada.buscarMesa(1).getStatus().getNome()); // Deve ser OCUPADA

            // 3.2. Adicionando itens ao pedido
            fachada.adicionarItemPedido(idPedido, "X-Burger", 2); //
            fachada.adicionarItemPedido(idPedido, "Coca-Cola Lata", 2);
            fachada.adicionarItemPedido(idPedido, "Batata Frita", 1);

            // 3.3. Teste de item indisponível (deve falhar)
            System.out.println("\nTestando item indisponível...");
            ItemCardapio itemBebida = repoItem.buscarPorNome("Coca-Cola Lata");
            itemBebida.atualizarDisponibilidade(false); //
            fachada.adicionarItemPedido(idPedido, "Coca-Cola Lata", 1); // Deve dar erro

            // 3.4. Registrando o pagamento
            System.out.println("\nRegistrando pagamento do pedido " + idPedido + "...");
            fachada.registrarPagamento(idPedido, MetodoPagamento.CARTAO_DEBITO); //

            // 3.5. Teste de pagamento duplicado (deve falhar)
            System.out.println("\nTestando pagamento duplicado...");
            fachada.registrarPagamento(idPedido, MetodoPagamento.PIX); // Deve dar erro
        }

        // --- 4. Testes do Pacote 1: Simulação de Pedido (Sem Cliente) ---
        System.out.println("\n--- Teste do Pacote 1: Simulação de Pedido (Mesa 2) ---");
        System.out.println("\nCriando pedido para Mesa 2 (Sem cliente)...");
        Pedido pedidoMesa2 = fachada.criarPedido(2, null); //
        fachada.adicionarItemPedido(pedidoMesa2.getId(), "Batata Frita", 1);
        fachada.registrarPagamento(pedidoMesa2.getId(), MetodoPagamento.PIX);

        // --- 5. Testes do Pacote 1: Relatórios ---
        System.out.println("\n--- Teste do Pacote 1: Relatórios ---");
        fachada.gerarRelatorioVendas(LocalDate.now(), LocalDate.now()); //

    }
}