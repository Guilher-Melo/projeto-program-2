import java.time.LocalDate;
import modelo.*;
import negocio.Fachada;
import repositorio.*;
import controller.*;

public class Main {
    public static void main(String[] args) {

        // --- 1. Configuração da Fachada com Controllers ---

        IRepositorioCliente repoCliente = new RepositorioClienteArray();
        IRepositorioReserva repoReserva = new RepositorioReservaArray();
        IRepositorioMesa repoMesa = new RepositorioMesaArray();
        IRepositorioPedido repoPedido = new RepositorioPedidoArray();
        IRepositorioItemCardapio repoItem = new RepositorioItemCardapioArray();
        IRepositorioFuncionario repoFunc = new RepositorioFuncionarioArray();

        ClienteController clienteController = new ClienteController(repoCliente);
        FuncionarioController funcionarioController = new FuncionarioController(repoFunc);
        ItemCardapioController itemCardapioController = new ItemCardapioController(repoItem);
        MesaController mesaController = new MesaController(repoMesa);
        PedidoController pedidoController = new PedidoController(repoPedido);
        ReservaController reservaController = new ReservaController(repoReserva);

        // Instancia a fachada com os controllers
        Fachada fachada = new Fachada(
                clienteController,
                funcionarioController,
                itemCardapioController,
                mesaController,
                pedidoController,
                reservaController);

        System.out.println("--- Sistema de Restaurante Iniciado ---");
        System.out.println("\n--- Teste do Pacote 2: Clientes e Reservas (Existente) ---");

        if (fachada.cadastrarCliente("Guilherme Melo", "98888-0001", "gui@email.com")) {
            System.out.println("Cliente Guilherme Melo cadastrado com sucesso.");
        } else {
            System.err.println("Erro (REQ01): Já existe um cliente com o telefone 98888-0001");
        }

        if (fachada.cadastrarCliente("Maria Silva", "97777-0002", "maria@email.com")) {
            System.out.println("Cliente Maria Silva cadastrado com sucesso.");
        } else {
            System.err.println("Erro (REQ01): Já existe um cliente com o telefone 97777-0002");
        }

        // --- 2. Testes do Pacote 1: Preparação (Cadastro de Itens e Mesas) ---
        System.out.println("\n--- Teste do Pacote 1: Cadastros Iniciais ---");

        // Cadastrando mesas
        if (fachada.cadastrarMesa(1, 4)) {
            System.out.println("Mesa 1 cadastrada.");
        } else {
            System.err.println("Erro: Já existe uma mesa com o número 1");
        }

        if (fachada.cadastrarMesa(2, 2)) {
            System.out.println("Mesa 2 cadastrada.");
        } else {
            System.err.println("Erro: Já existe uma mesa com o número 2");
        }

        // Cadastrando itens no cardápio
        if (fachada.cadastrarItemCardapio("Coca-Cola Lata", "Refrigerante", 6.00, CategoriaItem.BEBIDA)) {
            System.out.println("Item 'Coca-Cola Lata' cadastrado no cardápio.");
        }
        if (fachada.cadastrarItemCardapio("X-Burger", "Pão, carne, queijo", 22.00, CategoriaItem.LANCHE)) {
            System.out.println("Item 'X-Burger' cadastrado no cardápio.");
        }
        if (fachada.cadastrarItemCardapio("Batata Frita", "Porção média", 15.00, CategoriaItem.ENTRADA)) {
            System.out.println("Item 'Batata Frita' cadastrado no cardápio.");
        }

        // --- 3. Testes do Pacote 1: Simulação de Pedido ---
        System.out.println("\n--- Teste do Pacote 1: Simulação de Pedido (Mesa 1) ---");

        // 3.1. Cliente chega, funcionário cria o pedido para a Mesa 1
        System.out.println("\nCriando pedido para Mesa 1 (Cliente Guilherme)...");
        // O cliente "Guilherme" tem o telefone "98888-0001"
        Pedido pedidoMesa1 = fachada.criarPedido(1, "98888-0001"); //

        if (pedidoMesa1 != null) {
            int idPedido = pedidoMesa1.getId();
            System.out.println("Pedido " + idPedido + " criado para a mesa 1");
            System.out.println("Status da Mesa 1: " + fachada.buscarMesa(1).getStatus().getNome()); // Deve ser OCUPADA

            // 3.2. Adicionando itens ao pedido
            if (fachada.adicionarItemPedido(idPedido, "X-Burger", 2)) {
                System.out.println("2x X-Burger adicionado(s) ao pedido " + idPedido);
            }
            if (fachada.adicionarItemPedido(idPedido, "Coca-Cola Lata", 2)) {
                System.out.println("2x Coca-Cola Lata adicionado(s) ao pedido " + idPedido);
            }
            if (fachada.adicionarItemPedido(idPedido, "Batata Frita", 1)) {
                System.out.println("1x Batata Frita adicionado(s) ao pedido " + idPedido);
            }

            // 3.3. Teste de item indisponível (deve falhar)
            System.out.println("\nTestando item indisponível...");
            ItemCardapio itemBebida = repoItem.buscarPorNome("Coca-Cola Lata");
            itemBebida.atualizarDisponibilidade(false);
            if (!fachada.adicionarItemPedido(idPedido, "Coca-Cola Lata", 1)) {
                System.err.println("Erro: Item 'Coca-Cola Lata' não está disponível.");
            }

            // 3.4. Registrando o pagamento
            System.out.println("\nRegistrando pagamento do pedido " + idPedido + "...");
            if (fachada.registrarPagamento(idPedido, MetodoPagamento.CARTAO_DEBITO)) {
                Pedido pedidoAtualizado = pedidoController.buscarPedidoPorId(idPedido);
                System.out.println("Pagamento de R$ " + pedidoAtualizado.getValorTotal() + " para o pedido " + idPedido
                        + " registrado.");
            }

            // 3.5. Teste de pagamento duplicado (deve falhar)
            System.out.println("\nTestando pagamento duplicado...");
            if (!fachada.registrarPagamento(idPedido, MetodoPagamento.PIX)) {
                System.err.println("Erro: Pedido " + idPedido + " já foi pago.");
            }
        } else {
            System.err.println("Erro: Mesa não encontrada ou não está livre.");
        }

        // --- 4. Testes do Pacote 1: Simulação de Pedido (Sem Cliente) ---
        System.out.println("\n--- Teste do Pacote 1: Simulação de Pedido (Mesa 2) ---");
        System.out.println("\nCriando pedido para Mesa 2 (Sem cliente)...");
        Pedido pedidoMesa2 = fachada.criarPedido(2, null);
        if (pedidoMesa2 != null) {
            System.out.println("Pedido " + pedidoMesa2.getId() + " criado para a mesa 2");
            fachada.adicionarItemPedido(pedidoMesa2.getId(), "Batata Frita", 1);
            if (fachada.registrarPagamento(pedidoMesa2.getId(), MetodoPagamento.PIX)) {
                System.out.println("Pagamento registrado com sucesso.");
            }
        }

        // --- 5. Testes do Pacote 1: Relatórios ---
        System.out.println("\n--- Teste do Pacote 1: Relatórios ---");
        fachada.gerarRelatorioVendas(LocalDate.now(), LocalDate.now());
    }
}