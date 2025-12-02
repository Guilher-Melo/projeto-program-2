package gui.controlador;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelo.ItemCardapio;
import negocio.Fachada;

public class VisualizacaoCardapioController {

    @FXML private TableView<ItemCardapio> tabelaItens;
    @FXML private TableColumn<ItemCardapio, String> colNome;
    @FXML private TableColumn<ItemCardapio, String> colCategoria;
    @FXML private TableColumn<ItemCardapio, String> colPreco;
    @FXML private TableColumn<ItemCardapio, String> colDescricao;
    
    @FXML private Spinner<Integer> spinnerQuantidade;

    private Fachada fachada;
    private int idPedidoAtual;
    private Stage stageAtual; // Referência à janela para poder fechá-la

    // Método especial para inicializar quem chama essa tela
    public void setDados(Fachada fachada, int idPedido, Stage stage) {
        this.fachada = fachada;
        this.idPedidoAtual = idPedido;
        this.stageAtual = stage;
        
        inicializarComponentes();
        carregarItensDisponiveis();
    }

    private void inicializarComponentes() {
        // Configura Spinner para aceitar apenas números de 1 a 50
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        spinnerQuantidade.setValueFactory(valueFactory);

        // Configura Colunas
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colCategoria.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoria().getNome()));
        colPreco.setCellValueFactory(cell -> new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().getPreco())));
        colDescricao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescricao()));
    }

    private void carregarItensDisponiveis() {
        if (fachada == null) return;

        // Filtra apenas itens DISPONÍVEIS para venda
        List<ItemCardapio> itens = fachada.listarItensCardapio().stream()
                .filter(ItemCardapio::isDisponivel)
                .collect(Collectors.toList());

        tabelaItens.setItems(FXCollections.observableArrayList(itens));
    }

    @FXML
    public void confirmarAdicao() {
        ItemCardapio itemSelecionado = tabelaItens.getSelectionModel().getSelectedItem();
        
        if (itemSelecionado == null) {
            mostrarAlerta("Selecione um item", "Clique em um item da tabela para selecioná-lo.");
            return;
        }

        int quantidade = spinnerQuantidade.getValue();

        // CHAMA A FACHADA PARA ADICIONAR O ITEM AO PEDIDO (Conforme Checklist)
        boolean sucesso = fachada.adicionarItemPedido(idPedidoAtual, itemSelecionado.getNome(), quantidade);

        if (sucesso) {
            mostrarAlerta("Sucesso", quantidade + "x " + itemSelecionado.getNome() + " adicionado(s)!");
            // Fecha o pop-up após adicionar
            fecharJanela(); 
        } else {
            mostrarAlerta("Erro", "Não foi possível adicionar o item (verifique disponibilidade ou pedido).");
        }
    }

    @FXML
    public void fecharJanela() {
        if (stageAtual != null) {
            stageAtual.close();
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