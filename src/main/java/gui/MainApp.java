package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import negocio.Fachada;
import repositorio.*;
import controller.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Inicialização do Sistema
            // Criar Repositórios
            IRepositorioCliente repoCliente = new RepositorioClienteArray();
            IRepositorioReserva repoReserva = new RepositorioReservaArray();
            IRepositorioMesa repoMesa = new RepositorioMesaArray();
            IRepositorioPedido repoPedido = new RepositorioPedidoArray();
            IRepositorioItemCardapio repoItem = new RepositorioItemCardapioArray();
            IRepositorioFuncionario repoFunc = new RepositorioFuncionarioArray();

            // Criar Controllers de Negócio
            ClienteController clienteController = new ClienteController(repoCliente);
            FuncionarioController funcionarioController = new FuncionarioController(repoFunc);
            ItemCardapioController itemCardapioController = new ItemCardapioController(repoItem);
            MesaController mesaController = new MesaController(repoMesa);
            PedidoController pedidoController = new PedidoController(repoPedido);
            ReservaController reservaController = new ReservaController(repoReserva);

            // Criar Fachada
            Fachada fachada = new Fachada(
                    clienteController,
                    funcionarioController,
                    itemCardapioController,
                    mesaController,
                    pedidoController,
                    reservaController
            );

            // Testes iniciais
            fachada.cadastrarMesa(1, 4);
            fachada.cadastrarFuncionario("Admin", "Gerente");

            // 2. Configurar o Gerenciador de Telas
            GerenciadorTelas.getInstance().inicializar(primaryStage, fachada);

            // 3. Carregar a primeira tela (Login)
            // Certifique-se de criar o arquivo Login.fxml na pasta resources/telas
            GerenciadorTelas.getInstance().trocarTela("/view/Login.fxml", "Login - Restaurante");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}