package gui.controlador;

import java.io.File;
import java.time.LocalDate;

import gui.GerenciadorTelas;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea; // Para escolher onde salvar
import javafx.stage.FileChooser;
import negocio.Fachada;

public class RelatoriosController implements IControlador {

    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;
    @FXML private TextArea txtAreaRelatorios;
    @FXML private Button btnRelatorioVendas;
    @FXML private Button btnItensMaisVendidos;
    // Se você adicionar os botões no FXML, declare-os aqui:
    // @FXML private Button btnOcupacao;
    // @FXML private Button btnExportar;

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
        
        // REQ17
        String texto = fachada.gerarRelatorioVendas(dataInicio, dataFim);
        txtAreaRelatorios.setText(texto);
    }

    @FXML
    private void gerarItensMaisVendidosAction() {
        // REQ18
        String texto = fachada.gerarRelatorioItensMaisVendidos();
        txtAreaRelatorios.setText(texto);
    }

    // --- NOVO: Método para o REQ19 (Crie um botão no FXML e ligue a este método) ---
    @FXML
    public void gerarRelatorioOcupacaoAction() {
        String texto = fachada.gerarRelatorioOcupacao();
        txtAreaRelatorios.setText(texto);
    }

    // --- NOVO: Método para o REQ20 (Crie um botão "Exportar" no FXML) ---
    @FXML
    public void exportarRelatorioAction() {
        String conteudo = txtAreaRelatorios.getText();
        
        if (conteudo == null || conteudo.isEmpty()) {
            mostrarAlerta("Aviso", "Gere um relatório primeiro para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivos de Texto", "*.txt"),
                new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            boolean sucesso = fachada.exportarRelatorio(conteudo, file.getAbsolutePath());
            if (sucesso) {
                mostrarAlerta("Sucesso", "Relatório exportado para: " + file.getName());
            } else {
                mostrarAlerta("Erro", "Não foi possível salvar o arquivo.");
            }
        }
    }

    @FXML
    public void voltarParaPrincipal() {
        GerenciadorTelas.getInstance().trocarTela("/view/TelaPrincipal.fxml", "Menu Principal");
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}