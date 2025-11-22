package gui.controlador;

import javafx.fxml.FXML;
import negocio.Fachada;

public class TelaPrincipalController implements IControlador {

    private Fachada fachada;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    // Aqui vamos ligar os botões do menu no futuro
    @FXML
    public void abrirMesas() {
        System.out.println("Navegar para Mesas (Em breve)");
        // GerenciadorTelas.getInstance().trocarTela("/view/Mesas.fxml", "Mesas");
    }

    @FXML
    public void abrirClientes() {
        System.out.println("Navegar para Clientes (Em breve)");
    }
}