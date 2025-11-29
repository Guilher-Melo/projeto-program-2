package gui.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.MetodoPagamento;
import modelo.Pedido;
import negocio.Fachada;

public class FechamentoContaController implements IControlador {

    @FXML
    private Label lblNumeroMesa;
    @FXML
    private Label lblNomeCliente;
    @FXML
    private Label lblValorTotal;
    @FXML
    private ComboBox<String> cbMetodoPagamento;
    @FXML
    private TextField txtValorPago;
    @FXML
    private Label lblTroco;
    @FXML
    private Button btnConfirmarPagamento;
    @FXML
    private Button btnCancelar;

    private Fachada fachada;
    private Pedido pedido;
    private boolean pagamentoConfirmado = false;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
        inicializar();
    }

    private void inicializar() {
        // Configurar labels com informações do pedido
        lblNumeroMesa.setText("Mesa " + pedido.getNumeroMesa());

        if (pedido.getCliente() != null) {
            lblNomeCliente.setText(pedido.getCliente().getNome());
        } else {
            lblNomeCliente.setText("Cliente não identificado");
        }

        lblValorTotal.setText(String.format("R$ %.2f", pedido.getValorTotal()));
        lblTroco.setText("R$ 0,00");

        // Configurar ComboBox com métodos de pagamento
        cbMetodoPagamento.getItems().addAll(
                "Dinheiro",
                "Cartão de Crédito",
                "Cartão de Débito",
                "PIX",
                "Vale Refeição");
        cbMetodoPagamento.getSelectionModel().selectFirst();

        // Listener para calcular troco quando o método for Dinheiro
        cbMetodoPagamento.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Dinheiro".equals(newVal)) {
                txtValorPago.setDisable(false);
                txtValorPago.clear();
                lblTroco.setText("R$ 0,00");
            } else {
                txtValorPago.setDisable(true);
                txtValorPago.setText(String.format("%.2f", pedido.getValorTotal()));
                lblTroco.setText("R$ 0,00");
            }
        });

        // Listener para calcular troco em tempo real
        txtValorPago.textProperty().addListener((obs, oldVal, newVal) -> {
            calcularTroco();
        });

        // Inicializar valor pago desabilitado (até selecionar Dinheiro)
        if (!"Dinheiro".equals(cbMetodoPagamento.getValue())) {
            txtValorPago.setDisable(true);
            txtValorPago.setText(String.format("%.2f", pedido.getValorTotal()));
        }
    }

    private void calcularTroco() {
        if ("Dinheiro".equals(cbMetodoPagamento.getValue())) {
            try {
                String valorTexto = txtValorPago.getText().replace(",", ".");
                double valorPago = Double.parseDouble(valorTexto);
                double troco = valorPago - pedido.getValorTotal();

                if (troco < 0) {
                    lblTroco.setText("Valor insuficiente!");
                    lblTroco.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else {
                    lblTroco.setText(String.format("R$ %.2f", troco));
                    lblTroco.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException e) {
                lblTroco.setText("R$ 0,00");
                lblTroco.setStyle("-fx-text-fill: black;");
            }
        }
    }

    @FXML
    private void confirmarPagamento() {
        try {
            // Validar método de pagamento
            if (cbMetodoPagamento.getValue() == null) {
                mostrarErro("Selecione um método de pagamento");
                return;
            }

            // Validar valor pago (se for dinheiro)
            if ("Dinheiro".equals(cbMetodoPagamento.getValue())) {
                try {
                    String valorTexto = txtValorPago.getText().replace(",", ".");
                    double valorPago = Double.parseDouble(valorTexto);

                    if (valorPago < pedido.getValorTotal()) {
                        mostrarErro("Valor pago é insuficiente!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarErro("Valor pago inválido!");
                    return;
                }
            }

            // Converter string do método para o enum
            MetodoPagamento metodo = converterMetodoPagamento(cbMetodoPagamento.getValue());

            // Chamar a fachada para registrar o pagamento
            boolean sucesso = fachada.registrarPagamento(pedido.getId(), metodo);

            if (sucesso) {
                pagamentoConfirmado = true;
                mostrarSucesso("Pagamento registrado com sucesso!\n" +
                        "Mesa liberada.");
                fecharJanela();
            } else {
                mostrarErro("Erro ao registrar pagamento.\n" +
                        "O pedido pode já ter sido pago.");
            }

        } catch (Exception e) {
            mostrarErro("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private MetodoPagamento converterMetodoPagamento(String metodoStr) {
        switch (metodoStr) {
            case "Dinheiro":
                return MetodoPagamento.DINHEIRO;
            case "Cartão de Crédito":
                return MetodoPagamento.CARTAO_CREDITO;
            case "Cartão de Débito":
                return MetodoPagamento.CARTAO_DEBITO;
            case "PIX":
                return MetodoPagamento.PIX;
            case "Vale Refeição":
                return MetodoPagamento.VALE_REFEICAO;
            default:
                return MetodoPagamento.DINHEIRO;
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public boolean isPagamentoConfirmado() {
        return pagamentoConfirmado;
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Erro no Pagamento");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
