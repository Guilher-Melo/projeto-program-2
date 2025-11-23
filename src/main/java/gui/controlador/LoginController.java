package gui.controlador; // Note que o pacote reflete as pastas

import gui.GerenciadorTelas; // Importe sua classe Gerenciador
import gui.controlador.IControlador; // Importe sua interface
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import negocio.Fachada;

public class LoginController implements IControlador {

    private Fachada fachada;

    @FXML
    private TextField campoNome;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    @FXML
    public void fazerLogin() {
        String nome = campoNome.getText();
        // Apenas um teste para ver se a fachada responde
        // No futuro, você usaria fachada.buscarFuncionario(nome)
        if (nome != null && !nome.isEmpty()) {
            // ... dentro do if (sucesso) ...
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login");
            alert.setHeaderText("Sucesso");
            alert.showAndWait(); // Espera o usuário dar OK

            // A MÁGICA DA NAVEGAÇÃO:
            // Como estamos dentro do pacote gui.controlador, precisamos importar o GerenciadorTelas de gui
            gui.GerenciadorTelas.getInstance().trocarTela("/view/TelaPrincipal.fxml", "Menu Principal");
             // Exemplo de navegação para uma próxima tela (se ela existisse)
             // GerenciadorTelas.getInstance().trocarTela("/view/Principal.fxml", "Menu Principal");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Campo vazio");
            alert.showAndWait();
        }
    }
}