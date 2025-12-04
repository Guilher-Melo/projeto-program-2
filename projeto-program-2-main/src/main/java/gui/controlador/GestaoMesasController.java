package gui.controlador;

import java.util.List;

import gui.GerenciadorTelas;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField; // <--- O IMPORT QUE PROVAVELMENTE FALTOU
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import modelo.Mesa;
import modelo.StatusMesa;
import negocio.Fachada;

public class GestaoMesasController implements IControlador {

    @FXML
    private TilePane painelMesas; 
    
    @FXML 
    private TextField txtQtdMesas; // <--- O NOVO CAMPO

    private Fachada fachada;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
        atualizarMesas();
    }

    @FXML
    public void atualizarMesas() {
        if (painelMesas == null) return;

        painelMesas.getChildren().clear();

        // Antes de listar, verifica os horários e atualiza status (Reservada / Livre)
        fachada.atualizarStatusReservas(); 
        // --------------------

        List<Mesa> mesas = fachada.listarMesas();
        
        // Atualiza o campo de texto lá em cima com a quantidade atual
        if (txtQtdMesas != null) {
            txtQtdMesas.setText(String.valueOf(mesas.size()));
        }

        if (mesas.isEmpty()) {
            return;
        }

        for (Mesa mesa : mesas) {
            Button btnMesa = criarBotaoMesa(mesa);
            painelMesas.getChildren().add(btnMesa);
        }
    }
    
    // --- NOVO MÉTODO PARA O BOTÃO OK ---
    @FXML
    public void mudarQuantidade() {
        try {
            int novaQtd = Integer.parseInt(txtQtdMesas.getText());
            if (novaQtd <= 0) {
                mostrarAlerta("Erro", "A quantidade deve ser maior que zero.");
                return;
            }

            // Chama a fachada para redimensionar
            fachada.atualizarQuantidadeMesas(novaQtd);
            
            // Recarrega a tela para mostrar os novos botões
            atualizarMesas();
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Digite um número válido.");
        } catch (Exception e) {
            // Captura o erro se tentar remover mesa ocupada
            mostrarAlerta("Não permitido", e.getMessage());
            // Reseta o campo para o valor real atual
            atualizarMesas();
        }
    }

    private Button criarBotaoMesa(Mesa mesa) {
        // Texto do botão
        String texto = "Mesa " + mesa.getNumero() + "\n(" + mesa.getCapacidade() + " lug.)\n" + mesa.getStatus().getNome();
        Button btn = new Button(texto);

        // Estilo fixo
        btn.setPrefSize(140, 140);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setWrapText(true);
        btn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10;");

        // Definição de Cores baseada no Status
        if (mesa.getStatus() == StatusMesa.LIVRE) {
            // VERDE
            btn.setStyle(btn.getStyle() + "-fx-background-color: #4CAF50; -fx-text-fill: white;");
        } else if (mesa.getStatus() == StatusMesa.OCUPADA) {
            // VERMELHO
            btn.setStyle(btn.getStyle() + "-fx-background-color: #F44336; -fx-text-fill: white;");
        } else if (mesa.getStatus() == StatusMesa.RESERVADA) {
            // LARANJA
            btn.setStyle(btn.getStyle() + "-fx-background-color: #FF9800; -fx-text-fill: white;");
        } else {
            // CINZA
            btn.setStyle(btn.getStyle() + "-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        }

        // AÇÃO UNIFICADA: Independente do status, abre a tela de pedido
        btn.setOnAction(e -> abrirTelaPedido(mesa));

        return btn;
    }

    private void abrirTelaPedido(Mesa mesa) {
        // Chama o gerenciador para abrir a tela passando o ID da mesa
        GerenciadorTelas.getInstance().abrirTelaPedido(mesa.getNumero());
    }

    @FXML
    private void voltarParaPrincipal() {
        GerenciadorTelas.getInstance().trocarTela("/view/TelaPrincipal.fxml", "Menu Principal");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}