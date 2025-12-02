package gui.controlador;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Pedido;
import modelo.StatusPedido;
import negocio.Fachada;

import java.util.List;
import java.util.stream.Collectors;

public class TelaCozinhaController implements IControlador {

    private Fachada fachada;
    private ObservableList<Pedido> listaPedidosObservable;

    @FXML
    private TableView<Pedido> tabelaPedidos;

    @FXML
    private TableColumn<Pedido, Integer> colunaId;

    @FXML
    private TableColumn<Pedido, Integer> colunaMesa;

    @FXML
    private TableColumn<Pedido, String> colunaItens; // Exibiremos os itens como texto

    @FXML
    private TableColumn<Pedido, String> colunaStatus; // Exibiremos o nome do status

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
        inicializarTabela();
        atualizarLista();
    }

    private void inicializarTabela() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaMesa.setCellValueFactory(new PropertyValueFactory<>("numeroMesa"));

        // Formata a lista de itens em uma String bonita (Ex: "2x Burger, 1x Refri")
        colunaItens.setCellValueFactory(cellData -> {
            Pedido p = cellData.getValue();
            String resumo = p.getItens().stream()
                    .map(item -> item.getQuantidade() + "x " + item.getItemCardapio().getNome())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(resumo);
        });

        // Mostra o nome do Status
        colunaStatus.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus().getNome())
        );
    }

    @FXML
    public void atualizarLista() {
        List<Pedido> todos = fachada.listarPedidos();

        List<Pedido> pedidosCozinha = todos.stream()
                .filter(p -> p.getStatus() == StatusPedido.PENDENTE || p.getStatus() == StatusPedido.EM_PREPARO)
                .collect(Collectors.toList());

        listaPedidosObservable = FXCollections.observableArrayList(pedidosCozinha);
        tabelaPedidos.setItems(listaPedidosObservable);
    }

    @FXML
    public void avancarParaPreparo() {
        Pedido selecionado = tabelaPedidos.getSelectionModel().getSelectedItem();
        if (selecionado != null && selecionado.getStatus() == StatusPedido.PENDENTE) {
            selecionado.setStatus(StatusPedido.EM_PREPARO);
            atualizarLista();
        } else if (selecionado != null) {
            mostrarAlerta("Aviso", "Apenas pedidos PENDENTES podem ir para preparo.");
        }
    }

    @FXML
    public void avancarParaPronto() {
        Pedido selecionado = tabelaPedidos.getSelectionModel().getSelectedItem();
        if (selecionado != null && selecionado.getStatus() == StatusPedido.EM_PREPARO) {
            selecionado.setStatus(StatusPedido.PRONTO);
            atualizarLista(); // Ele vai sumir da lista da cozinha, o que é correto!
        } else if (selecionado != null) {
            mostrarAlerta("Aviso", "Apenas pedidos EM PREPARO podem ser marcados como prontos.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
