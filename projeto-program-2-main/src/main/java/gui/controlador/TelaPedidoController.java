package gui.controlador;

import java.io.IOException;

import gui.GerenciadorTelas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Cliente;
import modelo.ItemPedido;
import modelo.Pedido;
import negocio.Fachada;

public class TelaPedidoController implements IControlador {

    private Fachada fachada;
    private int idMesaAtual;
    private Pedido pedidoAtual;

    @FXML
    private Label lblTituloMesa;
    @FXML
    private TextField txtTelefoneCliente;
    @FXML
    private Label lblNomeCliente;
    @FXML
    private Label lblValorTotal;

    @FXML
    private TableView<ItemPedido> tabelaItens;
    @FXML
    private TableColumn<ItemPedido, String> colItem;
    @FXML
    private TableColumn<ItemPedido, String> colQtd;
    @FXML
    private TableColumn<ItemPedido, String> colPreco;
    @FXML
    private TableColumn<ItemPedido, String> colTotal;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    // Método chamado pelo GerenciadorTelas para passar o ID da mesa (Checklist Item
    // 2)
    public void setMesa(int idMesa) {
        this.idMesaAtual = idMesa;
        this.lblTituloMesa.setText("Pedido - Mesa " + idMesa);
        inicializarPedido();
    }

    // Lógica para criar ou carregar pedido (Checklist Item 3)
    private void inicializarPedido() {
        // Verifica se já existe um pedido ativo para esta mesa
        this.pedidoAtual = fachada.listarPedidos().stream()
                .filter(ped -> ped.getNumeroMesa() == idMesaAtual
                        && ped.getStatus() != modelo.StatusPedido.ENTREGUE
                        && ped.getStatus() != modelo.StatusPedido.CANCELADO)
                .findFirst()
                .orElse(null);

        // NÃO cria pedido automaticamente - apenas carrega se existir
        // O pedido será criado quando o usuário adicionar o primeiro item ou vincular
        // cliente

        configurarTabela();
        atualizarTela();
    }

    private void configurarTabela() {
        colItem.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getItemCardapio().getNome()));
        colQtd.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantidade())));
        colPreco.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("R$ %.2f", cell.getValue().getItemCardapio().getPreco())));
        colTotal.setCellValueFactory(
                cell -> new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().calcularSubtotal())));
    }

    // Cria o pedido apenas quando realmente necessário
    private boolean garantirPedidoExiste() {
        if (pedidoAtual != null) {
            return true;
        }

        // Cria o pedido
        Pedido p = fachada.criarPedido(idMesaAtual, null);
        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível criar pedido");
            alert.setContentText("A mesa pode já estar ocupada ou indisponível.");
            alert.showAndWait();
            return false;
        }

        this.pedidoAtual = p;
        atualizarTela();
        return true;
    }

    public void atualizarTela() {
        if (pedidoAtual != null) {
            // Atualiza Cliente
            if (pedidoAtual.getCliente() != null) {
                lblNomeCliente.setText(pedidoAtual.getCliente().getNome());
                txtTelefoneCliente.setText(pedidoAtual.getCliente().getTelefone());
            } else {
                lblNomeCliente.setText("Cliente não identificado");
            }

            // Atualiza Tabela (Checklist Item 5)
            tabelaItens.setItems(FXCollections.observableArrayList(pedidoAtual.getItens()));
            tabelaItens.refresh();

            // Atualiza Total
            pedidoAtual.calcularTotal(); // Garante cálculo
            lblValorTotal.setText(String.format("R$ %.2f", pedidoAtual.getValorTotal()));
        }
    }

    // (Checklist Item 4)
    @FXML
    public void buscarCliente() {
        String telefone = txtTelefoneCliente.getText();
        if (telefone.isEmpty())
            return;

        // Garante que o pedido existe antes de vincular cliente
        if (!garantirPedidoExiste()) {
            return;
        }

        Cliente c = fachada.buscarClientePorTelefone(telefone);
        if (c != null) {
            if (pedidoAtual != null) {
                pedidoAtual.setCliente(c);
                fachada.atualizarCliente(c); // Apenas para garantir consistência se necessário
                // Precisaríamos salvar o pedido atualizado no repositório também
                // fachada.atualizarPedido(pedidoAtual); // Se existir esse método
            }
            atualizarTela();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cliente não encontrado.");
            alert.showAndWait();
        }
    }

    // (Checklist Item 6)
    // Trecho do TelaPedidoController.java feito anteriormente
    @FXML
    public void abrirCardapio() {
        // Garante que o pedido existe antes de adicionar itens
        if (!garantirPedidoExiste()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VisualizacaoCardapio.fxml"));
            Parent root = loader.load();

            VisualizacaoCardapioController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            // Passa os dados necessários
            controller.setDados(fachada, pedidoAtual.getId(), stage);

            stage.setScene(new Scene(root));
            stage.showAndWait();

            atualizarTela();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // (Checklist Item 7)
    @FXML
    public void fecharConta() {
        if (pedidoAtual == null || pedidoAtual.getItens().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Pedido Vazio");
            alert.setContentText("Não é possível fechar uma conta sem itens no pedido.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FechamentoConta.fxml"));
            Parent root = loader.load();

            FechamentoContaController controller = loader.getController();
            controller.setFachada(fachada);
            controller.setPedido(pedidoAtual);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Fechamento de Conta");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Se o pagamento foi confirmado, volta para gestão de mesas
            if (controller.isPagamentoConfirmado()) {
                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText("Pagamento Confirmado");
                sucesso.setContentText("O pagamento foi realizado com sucesso!");
                sucesso.showAndWait();

                GerenciadorTelas.getInstance().trocarTela("/view/GestaoMesas.fxml", "Gestão de Mesas");
            }

        } catch (IOException e) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText("Erro ao Abrir Fechamento");
            erro.setContentText("Não foi possível abrir a tela de fechamento: " + e.getMessage());
            erro.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void voltar() {
        GerenciadorTelas.getInstance().trocarTela("/view/GestaoMesas.fxml", "Gestão de Mesas");
    }
}