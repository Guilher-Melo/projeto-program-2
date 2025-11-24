package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import negocio.Fachada;

public class GerenciadorTelas {

    private static GerenciadorTelas instancia;
    private Stage stagePrincipal;
    private Fachada fachada;

    private GerenciadorTelas() {}

    public static GerenciadorTelas getInstance() {
        if (instancia == null) {
            instancia = new GerenciadorTelas();
        }
        return instancia;
    }

    public void inicializar(Stage stage, Fachada fachada) {
        this.stagePrincipal = stage;
        this.fachada = fachada;
    }

    public void trocarTela(String caminhoFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            Object controlador = loader.getController();
            if (controlador instanceof gui.controlador.IControlador) {
                ((gui.controlador.IControlador) controlador).setFachada(this.fachada);
            }

            Scene scene = new Scene(root);
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle(titulo);
            stagePrincipal.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela: " + caminhoFxml);
        }
    }


    public void abrirTelaPedido(int idMesa) {
        try {
            // Carrega o FXML do Pedido
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPedido.fxml"));
            Parent root = loader.load();

            // Pega o controlador específico da TelaPedido
            gui.controlador.TelaPedidoController controller = loader.getController();
            
            // Passa a fachada (padrão)
            controller.setFachada(this.fachada);
            
            // Passa o ID da mesa (Novo!)
            controller.setMesa(idMesa);

            // Troca a cena
            Scene scene = new Scene(root);
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle("Pedido - Mesa " + idMesa);
            stagePrincipal.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir Tela de Pedido para a mesa " + idMesa);
        }
    }
    // -------------------------------------

    public Fachada getFachada() {
        return fachada;
    }
}