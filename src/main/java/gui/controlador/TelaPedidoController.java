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

    @FXML private Label lblTituloMesa;
    @FXML private TextField txtTelefoneCliente;
    @FXML private Label lblNomeCliente;
    @FXML private Label lblValorTotal;

    @FXML private TableView<ItemPedido> tabelaItens;
    @FXML private TableColumn<ItemPedido, String> colItem;
    @FXML private TableColumn<ItemPedido, String> colQtd;
    @FXML private TableColumn<ItemPedido, String> colPreco;
    @FXML private TableColumn<ItemPedido, String> colTotal;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }

    // Método chamado pelo GerenciadorTelas para passar o ID da mesa (Checklist Item 2)
    public void setMesa(int idMesa) {
        this.idMesaAtual = idMesa;
        this.lblTituloMesa.setText("Pedido - Mesa " + idMesa);
        inicializarPedido();
    }

    // Lógica para criar ou carregar pedido (Checklist Item 3)
    private void inicializarPedido() {
        // Tenta achar um pedido existente para essa mesa (se já estiver ocupada)
        // Como sua fachada não tem "buscarPedidoPorMesa", vamos simplificar:
        // Sempre tentamos criar. A fachada deve tratar se a mesa já está ocupada no backend.
        // Se a mesa já tiver pedido, precisamos recuperá-lo. 
        // OBS: Para simplificar aqui, vamos assumir criação de novo pedido se não tivermos referência.
        
        // Tenta criar pedido sem cliente inicialmente
        Pedido p = fachada.criarPedido(idMesaAtual, null); 
        
        if (p == null) {
            // Se retornou null, a mesa provavelmente já está ocupada. 
            // Precisaríamos de um método fachada.buscarPedidoPorMesa(idMesa).
            // Workaround temporário: procurar na lista de todos os pedidos um que seja dessa mesa e esteja ABERTO.
            this.pedidoAtual = fachada.listarPedidos().stream()
                    .filter(ped -> ped.getNumeroMesa() == idMesaAtual && ped.getStatus() != modelo.StatusPedido.ENTREGUE && ped.getStatus() != modelo.StatusPedido.CANCELADO)
                    .findFirst()
                    .orElse(null);
        } else {
            this.pedidoAtual = p;
        }

        configurarTabela();
        atualizarTela();
    }

    private void configurarTabela() {
        colItem.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getItemCardapio().getNome()));
        colQtd.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantidade())));
        colPreco.setCellValueFactory(cell -> new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().getItemCardapio().getPreco())));
        colTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().calcularSubtotal())));
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
        if (telefone.isEmpty()) return;

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
        // Navega para tela de fechamento (ainda a ser criada ou apenas lógica)
        System.out.println("Indo para fechamento do pedido: " + pedidoAtual.getId());
        
        // Exemplo: GerenciadorTelas.getInstance().trocarTelaFechamento(pedidoAtual.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Funcionalidade de Fechamento será implementada na próxima etapa.");
        alert.showAndWait();
    }

    @FXML
    public void voltar() {
        GerenciadorTelas.getInstance().trocarTela("/view/GestaoMesas.fxml", "Gestão de Mesas");
    }
}