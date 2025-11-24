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
        // Substitua o System.out.println por isso:
        GerenciadorTelas.getInstance().trocarTela("/view/GestaoMesas.fxml", "Gestão de Mesas");
    }

    @FXML
    public void abrirClientes() {
        GerenciadorTelas.getInstance().trocarTela("/view/GestaoClientes.fxml",
                "Gestão de Clientes");
    }

    @FXML
    public void abrirCardapio() {
        // Substitua o System.out.println por:
        GerenciadorTelas.getInstance().trocarTela("/view/GestaoCardapio.fxml", "Gestão de Cardápio");
    }

    @FXML
    public void abrirReservas() {
        GerenciadorTelas.getInstance().trocarTela("/view/Reservas.fxml", "Gestão de Reservas");
    }

    @FXML
    public void abrirRelatorios() {
        System.out.println("Navegando para: Relatórios...");
    }
}