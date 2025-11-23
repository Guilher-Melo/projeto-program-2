package gui.controlador;

import gui.GerenciadorTelas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import modelo.Cliente;
import negocio.Fachada;

import java.util.List;
import java.util.Optional;

public class GestaoClientesController implements IControlador {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtTelefone;

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnAtualizar;

    @FXML
    private TableView<Cliente> tabelaClientes;

    @FXML
    private TableColumn<Cliente, String> colNome;

    @FXML
    private TableColumn<Cliente, String> colTelefone;

    @FXML
    private TableColumn<Cliente, String> colEmail;

    @FXML
    private TableColumn<Cliente, Void> colAcoes;

    private Fachada fachada;
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSelecionado = null;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
        inicializar();
    }

    private void inicializar() {
        configurarTabela();
        carregarClientes();
    }

    private void configurarTabela() {
        // Configurar colunas da tabela
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));

        colTelefone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefone()));

        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        // Configurar coluna de ações com botões
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnRemover = new Button("Remover");
            private final HBox container = new HBox(5, btnEditar, btnRemover);

            {
                // Estilos inline simples
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11px;");
                btnRemover.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 11px;");
                container.setAlignment(Pos.CENTER);

                btnEditar.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    selecionarClienteParaEdicao(cliente);
                });

                btnRemover.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    removerCliente(cliente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

    }

    @FXML
    private void carregarClientes() {
        try {
            List<Cliente> clientes = fachada.listarClientes();
            listaClientes = FXCollections.observableArrayList(clientes);
            tabelaClientes.setItems(listaClientes);
        } catch (Exception e) {
            mostrarErro("Erro ao carregar clientes", e.getMessage());
        }
    }

    @FXML
    private void cadastrarCliente() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }

            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();

            // Chamar fachada para cadastrar
            boolean sucesso = fachada.cadastrarCliente(nome, telefone, email);

            if (sucesso) {
                mostrarSucesso("Cliente cadastrado com sucesso!");
                limparFormulario();
                carregarClientes();
            } else {
                mostrarErro("Erro ao cadastrar",
                        "Já existe um cliente cadastrado com o telefone: " + telefone);
            }

        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de validação", e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro ao cadastrar cliente", e.getMessage());
        }
    }

    @FXML
    private void atualizarCliente() {
        try {
            if (clienteSelecionado == null) {
                mostrarErro("Nenhum cliente selecionado",
                        "Selecione um cliente na tabela para editar.");
                return;
            }

            if (!validarCampos()) {
                return;
            }

            // Atualizar dados do cliente selecionado
            clienteSelecionado.setNome(txtNome.getText().trim());
            clienteSelecionado.setEmail(txtEmail.getText().trim());
            // Telefone não pode ser alterado (é a chave)

            boolean sucesso = fachada.atualizarCliente(clienteSelecionado);

            if (sucesso) {
                mostrarSucesso("Cliente atualizado com sucesso!");
                limparFormulario();
                carregarClientes();
            } else {
                mostrarErro("Erro ao atualizar", "Cliente não encontrado no sistema.");
            }

        } catch (Exception e) {
            mostrarErro("Erro ao atualizar cliente", e.getMessage());
        }
    }

    private void removerCliente(Cliente cliente) {
        // Confirmar remoção
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover Cliente");
        confirmacao.setContentText("Deseja realmente remover o cliente:\n" +
                cliente.getNome() + " (" + cliente.getTelefone() + ")?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean sucesso = fachada.removerCliente(cliente.getTelefone());

                if (sucesso) {
                    mostrarSucesso("Cliente removido com sucesso!");
                    limparFormulario();
                    carregarClientes();
                } else {
                    mostrarErro("Erro ao remover", "Cliente não encontrado no sistema.");
                }

            } catch (Exception e) {
                mostrarErro("Erro ao remover cliente", e.getMessage());
            }
        }
    }

    private void selecionarClienteParaEdicao(Cliente cliente) {
        clienteSelecionado = cliente;

        // Preencher formulário
        txtNome.setText(cliente.getNome());
        txtTelefone.setText(cliente.getTelefone());
        txtEmail.setText(cliente.getEmail());

        // Desabilitar campo telefone (é a chave)
        txtTelefone.setDisable(true);

        // Habilitar botão atualizar
        btnAtualizar.setDisable(false);
    }

    @FXML
    private void limparFormulario() {
        txtNome.clear();
        txtTelefone.clear();
        txtEmail.clear();

        txtTelefone.setDisable(false);
        btnAtualizar.setDisable(true);
        clienteSelecionado = null;

        tabelaClientes.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarParaTelaPrincipal() {
        GerenciadorTelas.getInstance().trocarTela("/view/TelaPrincipal.fxml",
                "Sistema de Restaurante");
    }

    // ============= Métodos Auxiliares =============

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            erros.append("- Nome é obrigatório\n");
        }

        if (txtTelefone.getText() == null || txtTelefone.getText().trim().isEmpty()) {
            erros.append("- Telefone é obrigatório\n");
        } else if (!txtTelefone.getText().matches("\\d{10,11}|\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}")) {
            erros.append("- Telefone deve estar no formato correto\n");
        }

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            erros.append("- Email é obrigatório\n");
        } else if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            erros.append("- Email deve estar no formato correto\n");
        }

        if (erros.length() > 0) {
            mostrarErro("Campos inválidos", erros.toString());
            return false;
        }

        return true;
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
