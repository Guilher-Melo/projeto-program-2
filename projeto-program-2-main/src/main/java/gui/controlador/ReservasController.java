package gui.controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import gui.GerenciadorTelas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import modelo.Cliente;
import modelo.Mesa;
import modelo.Reserva;
import negocio.Fachada;

public class ReservasController implements IControlador {

    @FXML
    private ComboBox<Cliente> comboCliente;
    @FXML
    private ComboBox<Mesa> comboMesa;
    @FXML
    private DatePicker dtData;
    @FXML
    private TextField txtHora;
    @FXML
    private TextField txtPessoas;

    @FXML
    private TableView<Reserva> tabelaReservas;
    @FXML
    private TableColumn<Reserva, String> colDataHora;
    @FXML
    private TableColumn<Reserva, String> colCliente;
    @FXML
    private TableColumn<Reserva, String> colMesa;
    @FXML
    private TableColumn<Reserva, String> colPessoas;
    @FXML
    private TableColumn<Reserva, Void> colAcoes;

    private Fachada fachada;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
        inicializar();
    }

    private void inicializar() {
        configurarComboBoxes();
        configurarTabela();
        carregarDados();
    }

    private void configurarComboBoxes() {
        // Configura como o Cliente aparece no ComboBox
        comboCliente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Cliente c) {
                return (c == null) ? "" : c.getNome() + " (" + c.getTelefone() + ")";
            }

            @Override
            public Cliente fromString(String string) {
                return null;
            }
        });

        // Configura como a Mesa aparece no ComboBox
        comboMesa.setConverter(new StringConverter<>() {
            @Override
            public String toString(Mesa m) {
                return (m == null) ? "" : "Mesa " + m.getNumero() + " (" + m.getCapacidade() + " lug.)";
            }

            @Override
            public Mesa fromString(String string) {
                return null;
            }
        });
    }

    private void configurarTabela() {
        // Formata a data para exibir bonito na tabela
        colDataHora
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDataHora().format(formatter)));

        colCliente.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCliente().getNome()));

        colMesa.setCellValueFactory(cell -> new SimpleStringProperty("Mesa " + cell.getValue().getMesa().getNumero()));

        colPessoas.setCellValueFactory(
                cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getNumeroPessoas())));

        // Botão Cancelar
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnCancelar = new Button("Cancelar");

            {
                btnCancelar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 11px;");
                btnCancelar.setOnAction(e -> cancelarReserva(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnCancelar);
            }
        });
    }

    private void carregarDados() {
        // Carrega Clientes e Mesas para os ComboBoxes
        comboCliente.setItems(FXCollections.observableArrayList(fachada.listarClientes()));
        comboMesa.setItems(FXCollections.observableArrayList(fachada.listarMesas()));

        carregarReservas();
    }

    @FXML
    public void carregarReservas() {
        List<Reserva> reservas = fachada.listarReservas();
        tabelaReservas.setItems(FXCollections.observableArrayList(reservas));
        tabelaReservas.refresh();
    }

    @FXML
    public void cadastrarReserva() {
        try {
            Cliente cliente = comboCliente.getValue();
            Mesa mesa = comboMesa.getValue();
            LocalDate data = dtData.getValue();
            String horaStr = txtHora.getText();
            String pessoasStr = txtPessoas.getText();

            // Validações básicas
            if (cliente == null || mesa == null || data == null || horaStr.isEmpty() || pessoasStr.isEmpty()) {
                mostrarAlerta("Erro", "Preencha todos os campos.");
                return;
            }

            // Tenta converter horário
            LocalTime horario = LocalTime.parse(horaStr); // Espera formato HH:mm
            LocalDateTime dataHora = LocalDateTime.of(data, horario);
            int numPessoas = Integer.parseInt(pessoasStr);

            if (numPessoas > mesa.getCapacidade()) {
                mostrarAlerta("Capacidade Excedida", "A mesa " + mesa.getNumero() +
                        " só comporta " + mesa.getCapacidade() + " pessoas.");
                return;
            }

            // Cria objeto Reserva e passa para a Fachada
            modelo.Reserva novaReserva = new modelo.Reserva(dataHora, numPessoas, cliente, mesa);
            boolean sucesso = fachada.fazerReserva(novaReserva, mesa);

            if (sucesso) {
                mostrarAlerta("Sucesso", "Reserva realizada com sucesso!");
                limparFormulario();
                carregarReservas();
            } else {
                mostrarAlerta("Erro", "Não foi possível realizar a reserva (Verifique se a mesa está livre).");
            }

        } catch (DateTimeParseException e) {
            mostrarAlerta("Formato Inválido", "Digite a hora no formato HH:mm (Ex: 19:30).");
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Número de pessoas deve ser um valor inteiro.");
        } catch (Exception e) {
            mostrarAlerta("Erro", e.getMessage());
        }
    }

    private void cancelarReserva(Reserva reserva) {
        boolean sucesso = fachada.cancelarReserva(reserva);

        if (sucesso) {
            mostrarAlerta("Sucesso", "Reserva cancelada.");
            carregarReservas();
        } else {
            mostrarAlerta("Erro ao Cancelar", "Só é possível cancelar com 1 hora de antecedência.");
        }
    }

    @FXML
    public void limparFormulario() {
        comboCliente.setValue(null);
        comboMesa.setValue(null);
        dtData.setValue(null);
        txtHora.clear();
        txtPessoas.clear();
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