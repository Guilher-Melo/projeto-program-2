package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Pedido;
import modelo.StatusPedido;
import negocio.CalculadoraDivisaoConta;

public class DialogoPagamento extends Stage {

    private final Pedido pedido;
    private final CalculadoraDivisaoConta calculadora;
    
    // Componentes da tela
    private TextField txtQtdPessoas;
    private CheckBox chkTaxaServico;
    private Label lblValorPorPessoa;

    public DialogoPagamento(Pedido pedido) {
        this.pedido = pedido;
        this.calculadora = new CalculadoraDivisaoConta();

        // Configurações da Janela
        initModality(Modality.APPLICATION_MODAL); // Impede clicar na janela de trás
        setTitle("Pagamento - Pedido #" + pedido.getId());
        setResizable(false);

        // Layout Principal (Vertical)
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f4f4f4;");

        // 1. Título e Total
        Label lblTitulo = new Label("Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblTitulo.setTextFill(Color.DARKSLATEGRAY);

        // 2. Área de Divisão da Conta (REQ13)
        VBox boxDivisao = new VBox(10);
        boxDivisao.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: white;");
        
        txtQtdPessoas = new TextField("1");
        txtQtdPessoas.setPromptText("Qtd Pessoas");
        
        chkTaxaServico = new CheckBox("Incluir 10% (Serviço)");
        chkTaxaServico.setSelected(true);
        
        Button btnCalcular = new Button("Calcular Divisão");
        btnCalcular.setMaxWidth(Double.MAX_VALUE); // Botão esticado
        btnCalcular.setOnAction(e -> calcular());

        lblValorPorPessoa = new Label("Valor por pessoa: R$ ...");
        lblValorPorPessoa.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblValorPorPessoa.setTextFill(Color.BLUE);

        boxDivisao.getChildren().addAll(new Label("Dividir conta:"), txtQtdPessoas, chkTaxaServico, btnCalcular, lblValorPorPessoa);

        // 3. Botão Finalizar (REQ10)
        Button btnPagar = new Button("CONFIRMAR PAGAMENTO");
        btnPagar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnPagar.setPadding(new Insets(10));
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setOnAction(e -> pagar());

        // Adiciona tudo ao layout
        layout.getChildren().addAll(lblTitulo, boxDivisao, new Separator(), btnPagar);

        // Cria a cena e mostra
        Scene cena = new Scene(layout, 320, 400);
        setScene(cena);
    }

    private void calcular() {
        try {
            int qtd = Integer.parseInt(txtQtdPessoas.getText());
            double valor = calculadora.dividirContaIgualmente(pedido, qtd, chkTaxaServico.isSelected());
            lblValorPorPessoa.setText("Cada um paga: R$ " + String.format("%.2f", valor));
        } catch (NumberFormatException e) {
            lblValorPorPessoa.setText("Erro: Digite um número válido.");
        } catch (Exception e) {
            lblValorPorPessoa.setText("Erro: " + e.getMessage());
        }
    }

    private void pagar() {
        // Confirmação simples
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja confirmar o pagamento?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // REQ10: Atualiza status para PAGO
                pedido.setStatus(StatusPedido.PAGO);
                
                Alert sucesso = new Alert(Alert.AlertType.INFORMATION, "Pagamento registrado com sucesso!");
                sucesso.showAndWait();
                
                close(); // Fecha a janela
            }
        });
    }
}