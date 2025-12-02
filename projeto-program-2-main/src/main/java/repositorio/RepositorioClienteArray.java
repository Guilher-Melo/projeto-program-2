package repositorio;

import modelo.Cliente;
import java.util.ArrayList;
import java.util.List;

public class RepositorioClienteArray implements IRepositorioCliente {

    private List<Cliente> clientes;

    public RepositorioClienteArray() {
        this.clientes = new ArrayList<>();
    }

    @Override
    public void cadastrar(Cliente cliente) {
        this.clientes.add(cliente);
    }

    @Override
    public Cliente buscarPorTelefone(String telefone) {
        for (Cliente cliente : clientes) {
            if (cliente.getTelefone().equals(telefone)) {
                return cliente;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Cliente clienteAtualizado) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getTelefone().equals(clienteAtualizado.getTelefone())) {
                clientes.set(i, clienteAtualizado);
                break;
            }
        }
    }

    @Override
    public void remover(Cliente cliente) {
        this.clientes.remove(cliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }
}