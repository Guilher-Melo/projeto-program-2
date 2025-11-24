package gui;

import controller.ClienteController;
import controller.FuncionarioController;
import controller.ItemCardapioController;
import controller.MesaController;
import controller.PedidoController;
import controller.ReservaController;
import javafx.application.Application;
import javafx.stage.Stage;
import negocio.Fachada;
import repositorio.IRepositorioCliente;
import repositorio.IRepositorioFuncionario;
import repositorio.IRepositorioItemCardapio;
import repositorio.IRepositorioMesa;
import repositorio.IRepositorioPedido;
import repositorio.IRepositorioReserva;
import repositorio.RepositorioClienteArray;
import repositorio.RepositorioFuncionarioArray;
import repositorio.RepositorioItemCardapioArray;
import repositorio.RepositorioMesaArray;
import repositorio.RepositorioPedidoArray;
import repositorio.RepositorioReservaArray;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Inicialização do Sistema (Repositórios)
            IRepositorioCliente repoCliente = new RepositorioClienteArray();
            IRepositorioReserva repoReserva = new RepositorioReservaArray();
            IRepositorioMesa repoMesa = new RepositorioMesaArray();
            IRepositorioPedido repoPedido = new RepositorioPedidoArray();
            IRepositorioItemCardapio repoItem = new RepositorioItemCardapioArray();
            IRepositorioFuncionario repoFunc = new RepositorioFuncionarioArray();

            // 2. Inicialização dos Controllers
            ClienteController clienteController = new ClienteController(repoCliente);
            FuncionarioController funcionarioController = new FuncionarioController(repoFunc);
            ItemCardapioController itemCardapioController = new ItemCardapioController(repoItem);
            MesaController mesaController = new MesaController(repoMesa);
            PedidoController pedidoController = new PedidoController(repoPedido);
            ReservaController reservaController = new ReservaController(repoReserva);

            // 3. Inicialização da Fachada
            Fachada fachada = new Fachada(
                    clienteController,
                    funcionarioController,
                    itemCardapioController,
                    mesaController,
                    pedidoController,
                    reservaController
            );

            // Cadastra algumas mesas
            //fachada.cadastrarMesa(1, 4);
            //fachada.cadastrarMesa(2, 2);
            for (int i = 1; i <= 12; i++) {
                int capacidade = (i % 2 == 0) ? 2 : 4;
                fachada.cadastrarMesa(i, capacidade);
            }

            try {
                fachada.cadastrarFuncionario("Admin", "Gerente", "1234");
                fachada.cadastrarFuncionario("Ana Cozinheira", "Cozinha", "1234");
                fachada.cadastrarFuncionario("Carlos Garçom", "Garçom", "1234");
                fachada.cadastrarFuncionario("Roberto Garçom", "Garçom", "1234");
                fachada.cadastrarFuncionario("Juliana Caixa", "Atendente", "1234");

                System.out.println(">>> Funcionários de teste cadastrados com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao cadastrar funcionário inicial: " + e.getMessage());
            }

            GerenciadorTelas.getInstance().inicializar(primaryStage, fachada);

            GerenciadorTelas.getInstance().trocarTela("/view/Login.fxml", "Login - Restaurante");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}