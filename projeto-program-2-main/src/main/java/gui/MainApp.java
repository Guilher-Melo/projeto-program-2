package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import modelo.CategoriaItem;
import modelo.Cliente;
import modelo.Funcionario;
import modelo.ItemCardapio;
import modelo.Mesa;
import modelo.StatusMesa;
import negocio.Fachada;

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
            // 2. Cadastrando Funcionários
            fachada.cadastrarFuncionario(new Funcionario("Admin", "Gerente", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Ana Cozinheira", "Cozinha", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Carlos Garçom", "Garçom", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Roberto Garçom", "Garçom", "1234"));
            fachada.cadastrarFuncionario(new Funcionario("Juliana Caixa", "Atendente", "1234"));
            
            // 3. Cadastrando Cliente Anônimo (NOVO)
            // Usamos um telefone padrão "00000-0000" para identificá-lo internamente
            fachada.cadastrarCliente(new Cliente("Cliente Anônimo", "00000-0000", "sem_email"));

            // --- 4. Populando o Cardápio (3 itens por categoria) ---
            // ENTRADAS
            fachada.cadastrarItemCardapio(new ItemCardapio("Bruschetta Italiana", "Pão tostado com tomate, manjericão e azeite", 18.00, CategoriaItem.ENTRADA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Dadinho de Tapioca", "Cubos de tapioca com queijo coalho e geleia", 22.00, CategoriaItem.ENTRADA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Carpaccio Clássico", "Lâminas finas de carne com alcaparras", 35.00, CategoriaItem.ENTRADA));

            // PRATOS PRINCIPAIS
            fachada.cadastrarItemCardapio(new ItemCardapio("Risoto de Camarão", "Arroz arbóreo cremoso com camarões frescos", 65.00, CategoriaItem.PRATO_PRINCIPAL));
            fachada.cadastrarItemCardapio(new ItemCardapio("Filé ao Molho Madeira", "Medalhão de filé mignon com purê", 72.00, CategoriaItem.PRATO_PRINCIPAL));
            fachada.cadastrarItemCardapio(new ItemCardapio("Tilápia Grelhada", "Filé de tilápia com legumes salteados", 48.00, CategoriaItem.PRATO_PRINCIPAL));

            // SOBREMESAS
            fachada.cadastrarItemCardapio(new ItemCardapio("Petit Gâteau", "Bolo de chocolate com recheio e sorvete", 25.00, CategoriaItem.SOBREMESA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Mousse de Maracujá", "Mousse aerado com calda da fruta", 15.00, CategoriaItem.SOBREMESA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Tiramisu", "Sobremesa italiana com café e mascarpone", 28.00, CategoriaItem.SOBREMESA));

            // BEBIDAS
            fachada.cadastrarItemCardapio(new ItemCardapio("Suco de Laranja", "Suco natural da fruta 500ml", 12.00, CategoriaItem.BEBIDA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Água Mineral", "Com ou sem gás 500ml", 5.00, CategoriaItem.BEBIDA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Refrigerante Lata", "Coca-Cola, Guaraná ou Sprite", 7.00, CategoriaItem.BEBIDA));

            // BEBIDAS ALCOÓLICAS
            fachada.cadastrarItemCardapio(new ItemCardapio("Cerveja Artesanal", "IPA ou Weiss 600ml", 28.00, CategoriaItem.BEBIDA_ALCOOLICA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Vinho Tinto", "Taça de vinho Cabernet Sauvignon", 30.00, CategoriaItem.BEBIDA_ALCOOLICA));
            fachada.cadastrarItemCardapio(new ItemCardapio("Caipirinha", "Limão, açúcar e cachaça", 20.00, CategoriaItem.BEBIDA_ALCOOLICA));

            // LANCHES
            fachada.cadastrarItemCardapio(new ItemCardapio("X-Salada", "Hambúrguer, queijo, alface, tomate e maionese", 25.00, CategoriaItem.LANCHE));
            fachada.cadastrarItemCardapio(new ItemCardapio("Sanduíche Natural", "Pão integral, frango desfiado e ricota", 18.00, CategoriaItem.LANCHE));
            fachada.cadastrarItemCardapio(new ItemCardapio("Burger Artesanal", "Blend de carnes 180g, cheddar e bacon", 35.00, CategoriaItem.LANCHE));

            System.out.println(">>> Dados de exemplo (Mesas, Funcionários e Cardápio) cadastrados com sucesso!");

            
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar dados de exemplo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}