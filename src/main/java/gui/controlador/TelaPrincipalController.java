package gui.controlador;

import gui.GerenciadorTelas;
import javafx.fxml.FXML;
import negocio.Fachada;

public class TelaPrincipalController implements IControlador {

    private Fachada fachada;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    @FXML
    public void abrirMesas() {
        System.out.println("Navegando para: Gestão de Mesas...");
    }

    @FXML
    public void abrirClientes() {
        GerenciadorTelas.getInstance().trocarTela("/view/GestaoClientes.fxml",
                "Gestão de Clientes");
    }

    @FXML
    public void abrirCardapio() {
        System.out.println("Navegando para: Gestão do Cardápio...");
    }

    @FXML
    public void abrirReservas() {
        System.out.println("Navegando para: Reservas...");
    }

    @FXML
    public void abrirRelatorios() {
        System.out.println("Navegando para: Relatórios...");
    }
}