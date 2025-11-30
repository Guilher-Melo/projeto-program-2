package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import negocio.Fachada;
import modelo.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            Fachada fachada = Fachada.getInstancia();

            // Inicializar dados de exemplo
            inicializarDadosExemplo(fachada);

            // Inicializar gerenciador de telas
            GerenciadorTelas.getInstance().inicializar(primaryStage);

            // Abrir tela de login
            GerenciadorTelas.getInstance().trocarTela("/view/Login.fxml", "Login - Restaurante");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarDadosExemplo(Fachada fachada) {
        for (int i = 1; i <= 15; i++) {
            int capacidade = (i % 2 == 0) ? 2 : 4;
            fachada.cadastrarMesa(new Mesa(i, capacidade, StatusMesa.LIVRE));
        }

        try {
            fachada.cadastrarFuncionario(new Funcionario("Admin", "Gerente", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Ana Cozinheira", "Cozinha", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Carlos Garçom", "Garçom", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Roberto Garçom", "Garçom", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Juliana Caixa", "Atendente", "1234"));

            System.out.println(">>> Dados de exemplo cadastrados com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar dados de exemplo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}