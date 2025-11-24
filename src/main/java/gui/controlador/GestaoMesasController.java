package gui.controlador;

import gui.GerenciadorTelas;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import modelo.Mesa;
import modelo.StatusMesa;
import negocio.Fachada;

import java.util.List;

public class GestaoMesasController implements IControlador {

    @FXML
    private FlowPane painelMesas;

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

        List<Mesa> mesas = fachada.listarMesas();

        if (mesas.isEmpty()) {
            mostrarAlerta("Informação", "Nenhuma mesa cadastrada no sistema.");
            return;
        }

        for (Mesa mesa : mesas) {
            Button btnMesa = criarBotaoMesa(mesa);
            painelMesas.getChildren().add(btnMesa);
        }
    }

    private Button criarBotaoMesa(Mesa mesa) {
        // Texto do botão: Número e Capacidade
        String texto = "Mesa " + mesa.getNumero() + "\n(" + mesa.getCapacidade() + " lug.)\n" + mesa.getStatus().getNome();
        Button btn = new Button(texto);

        // Estilo fixo (quadrado)
        btn.setPrefSize(140, 140);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setWrapText(true);
        btn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10;");

        // Definição de Cores baseada no Status
        if (mesa.getStatus() == StatusMesa.LIVRE) {
            // VERDE
            btn.setStyle(btn.getStyle() + "-fx-background-color: #4CAF50; -fx-text-fill: white;");
            
            // Ação: Navegar para Pedido
            btn.setOnAction(e -> abrirTelaPedido(mesa));
            
        } else if (mesa.getStatus() == StatusMesa.OCUPADA) {
            // VERMELHO
            btn.setStyle(btn.getStyle() + "-fx-background-color: #F44336; -fx-text-fill: white;");
            
            // Ação: Ver detalhes ou avisar que está ocupada
            btn.setOnAction(e -> mostrarAlerta("Mesa Ocupada", "A Mesa " + mesa.getNumero() + " já está ocupada."));
            
        } else if (mesa.getStatus() == StatusMesa.RESERVADA) {
            // LARANJA
            btn.setStyle(btn.getStyle() + "-fx-background-color: #FF9800; -fx-text-fill: white;");
             btn.setOnAction(e -> mostrarAlerta("Mesa Reservada", "A Mesa " + mesa.getNumero() + " está reservada."));
        } else {
            // CINZA (Manutenção, etc)
            btn.setStyle(btn.getStyle() + "-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        }

        return btn;
    }

    private void abrirTelaPedido(Mesa mesa) {
        System.out.println(">>> Navegando para Tela de Pedido da Mesa " + mesa.getNumero());
        
        // TODO: Você precisará criar a "TelaPedido.fxml" e seu controlador.
        // Como o GerenciadorTelas atual é simples, ele não passa parâmetros (como o ID da mesa).
        // Sugestão temporária: Guardar o ID da mesa selecionada na Fachada ou num Singleton "Sessao"
        // antes de trocar de tela.
        
        // Exemplo de navegação (crie o arquivo FXML depois):
        // GerenciadorTelas.getInstance().trocarTela("/view/TelaPedido.fxml", "Pedido - Mesa " + mesa.getNumero());
        
        mostrarAlerta("Em Desenvolvimento", "Ir para tela de pedido da Mesa " + mesa.getNumero());
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