package gui.controlador;

import java.util.List;
import java.util.Optional;

import gui.GerenciadorTelas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import modelo.CategoriaItem;
import modelo.ItemCardapio;
import negocio.Fachada;

public class GestaoCardapioController implements IControlador {

    @FXML private TextField txtNome;
    @FXML private TextField txtDescricao;
    @FXML private TextField txtPreco;
    @FXML private ComboBox<CategoriaItem> comboCategoria;
    @FXML private CheckBox chkDisponivel;
    @FXML private Button btnSalvar;

    @FXML private TableView<ItemCardapio> tabelaItens;
    @FXML private TableColumn<ItemCardapio, String> colNome;
    @FXML private TableColumn<ItemCardapio, String> colCategoria;
    @FXML private TableColumn<ItemCardapio, String> colPreco;
    @FXML private TableColumn<ItemCardapio, String> colDisponivel;
    @FXML private TableColumn<ItemCardapio, Void> colAcoes;

    private Fachada fachada;
    private ItemCardapio itemSelecionado;

    @Override
    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
        inicializar();
    }

    private void inicializar() {
        // Popula o ComboBox com as categorias do Enum
        comboCategoria.getItems().setAll(CategoriaItem.values());

        configurarColunas();
        carregarItens();
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        
        colCategoria.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoria().getNome()));
        
        colPreco.setCellValueFactory(cell -> new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().getPreco())));
        
        colDisponivel.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isDisponivel() ? "Sim" : "Não"));

        // Coluna de Ações (Editar, Remover, Alternar Disp)
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnRemover = new Button("X");
            private final Button btnDisp = new Button("Disp.");
            private final HBox container = new HBox(5, btnEditar, btnDisp, btnRemover);

            {
                // Estilos
                btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10px;");
                btnRemover.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 10px;");
                btnDisp.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 10px;");
                container.setAlignment(Pos.CENTER);

                btnEditar.setOnAction(e -> editarItem(getTableView().getItems().get(getIndex())));
                btnRemover.setOnAction(e -> removerItem(getTableView().getItems().get(getIndex())));
                btnDisp.setOnAction(e -> alternarDisponibilidade(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    @FXML
    public void carregarItens() {
        List<ItemCardapio> itens = fachada.listarItensCardapio();
        tabelaItens.setItems(FXCollections.observableArrayList(itens));
        tabelaItens.refresh(); // Importante para atualizar visualmente
    }

    @FXML
    public void cadastrarItem() {
        try {
            String nome = txtNome.getText();
            String desc = txtDescricao.getText();
            String precoStr = txtPreco.getText().replace(",", "."); // Aceita vírgula
            CategoriaItem cat = comboCategoria.getValue();
            boolean disponivel = chkDisponivel.isSelected();

            if (nome.isEmpty() || precoStr.isEmpty() || cat == null) {
                mostrarAlerta("Erro", "Preencha Nome, Preço e Categoria.");
                return;
            }

            double preco = Double.parseDouble(precoStr);

            // OBS: O método da fachada original pede apenas (nome, desc, preco, cat).
            // A disponibilidade padrão é true.
            boolean sucesso = fachada.cadastrarItemCardapio(nome, desc, preco, cat);

            if (sucesso) {
                // Se precisar ajustar a disponibilidade logo após criar:
                ItemCardapio item = fachada.listarItensCardapio().stream()
                        .filter(i -> i.getNome().equals(nome)).findFirst().orElse(null);
                if (item != null && !disponivel) item.atualizarDisponibilidade(false);

                mostrarAlerta("Sucesso", "Item cadastrado!");
                limparFormulario();
                carregarItens();
            } else {
                mostrarAlerta("Erro", "Já existe um item com esse nome.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Preço inválido. Digite apenas números.");
        } catch (Exception e) {
            mostrarAlerta("Erro", e.getMessage());
        }
    }

    @FXML
    public void atualizarItem() {
        if (itemSelecionado == null) return;

        try {
            itemSelecionado.setDescricao(txtDescricao.getText());
            itemSelecionado.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
            itemSelecionado.setCategoria(comboCategoria.getValue());
            itemSelecionado.atualizarDisponibilidade(chkDisponivel.isSelected());
            // Nome não mudamos pois é a chave no seu sistema atual

            boolean sucesso = fachada.atualizarItemCardapio(itemSelecionado);
            
            if (sucesso) {
                mostrarAlerta("Sucesso", "Item atualizado!");
                limparFormulario();
                carregarItens();
            } else {
                mostrarAlerta("Erro", "Falha ao atualizar.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Preço inválido.");
        }
    }

    private void editarItem(ItemCardapio item) {
        this.itemSelecionado = item;
        txtNome.setText(item.getNome());
        txtNome.setDisable(true); // Bloqueia edição do nome (chave)
        txtDescricao.setText(item.getDescricao());
        txtPreco.setText(String.valueOf(item.getPreco()));
        comboCategoria.setValue(item.getCategoria());
        chkDisponivel.setSelected(item.isDisponivel());

        btnSalvar.setDisable(false);
    }

    private void alternarDisponibilidade(ItemCardapio item) {
        item.atualizarDisponibilidade(!item.isDisponivel());
        fachada.atualizarItemCardapio(item);
        carregarItens(); // Atualiza a tabela visualmente (Sim/Não)
    }

    private void removerItem(ItemCardapio item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remover Item");
        alert.setHeaderText("Tem certeza que deseja remover " + item.getNome() + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            fachada.removerItemCardapio(item.getNome());
            carregarItens();
        }
    }

    @FXML
    public void limparFormulario() {
        txtNome.clear();
        txtNome.setDisable(false);
        txtDescricao.clear();
        txtPreco.clear();
        comboCategoria.setValue(null);
        chkDisponivel.setSelected(true);
        itemSelecionado = null;
        btnSalvar.setDisable(true);
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