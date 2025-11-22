package gui;

import gui.controlador.IControlador;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import negocio.Fachada;

import java.io.IOException;

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
            // Carrega o arquivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            // INJEÇÃO DE DEPENDÊNCIA: Passa a fachada para o controlador
            Object controlador = loader.getController();
            if (controlador instanceof gui.controlador.IControlador) {
                ((gui.controlador.IControlador) controlador).setFachada(this.fachada);
            }

            // Mostra a nova cena
            Scene scene = new Scene(root);
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle(titulo);
            stagePrincipal.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Mantemos apenas este erro real, pois é útil para saber se falhou
            System.err.println("Erro ao carregar tela: " + caminhoFxml);
        }
    }

    public Fachada getFachada() {
        return fachada;
    }
}