package gui.controlador;

import java.time.LocalDate; // Import necessário

import gui.GerenciadorTelas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import negocio.Fachada;

public class RelatoriosController implements IControlador {

    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;
    @FXML private TextArea txtAreaRelatorios;
    @FXML private Button btnRelatorioVendas;
    @FXML private Button btnItensMaisVendidos;

    private Fachada fachada;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    @FXML
    private void gerarRelatorioVendasAction() {
        LocalDate dataInicio = dataInicioPicker.getValue();
        LocalDate dataFim = dataFimPicker.getValue();

        if (dataInicio == null || dataFim == null) {
            txtAreaRelatorios.setText("ERRO: Por favor, selecione as duas datas.");
            return;
        }

        String relatorio = fachada.gerarRelatorioVendas(dataInicio, dataFim);
        txtAreaRelatorios.setText(relatorio);
    }

    @FXML
    private void gerarItensMaisVendidosAction() {
        String relatorio = fachada.gerarRelatorioItensMaisVendidos();
        txtAreaRelatorios.setText(relatorio);
    }

    // --- NOVO MÉTODO PARA O BOTÃO VOLTAR ---
    @FXML
    public void voltarParaPrincipal() {
        GerenciadorTelas.getInstance().trocarTela("/view/TelaPrincipal.fxml", "Menu Principal");
    }
}