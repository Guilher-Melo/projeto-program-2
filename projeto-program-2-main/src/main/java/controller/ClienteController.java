package controller;

import modelo.Cliente;
import repositorio.IRepositorioCliente;
import java.util.List;

public class ClienteController {

    private IRepositorioCliente repositorioCliente;

    public ClienteController(IRepositorioCliente repositorioCliente) {
        this.repositorioCliente = repositorioCliente;
    }

    public boolean cadastrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode ser nulo ou vazio");
        }

        // Verifica se já existe um cliente com o mesmo telefone
        if (repositorioCliente.buscarPorTelefone(cliente.getTelefone()) != null) {
            return false;
        }

        repositorioCliente.cadastrar(cliente);
        return true;
    }

    public Cliente buscarClientePorTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode ser nulo ou vazio");
        }
        return repositorioCliente.buscarPorTelefone(telefone);
    }

    public boolean atualizarCliente(Cliente clienteAtualizado) {
        if (clienteAtualizado == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }

        Cliente clienteExistente = repositorioCliente.buscarPorTelefone(clienteAtualizado.getTelefone());
        if (clienteExistente == null) {
            return false;
        }

        repositorioCliente.atualizar(clienteAtualizado);
        return true;
    }

    public boolean removerCliente(String telefone) {
        Cliente cliente = repositorioCliente.buscarPorTelefone(telefone);
        if (cliente == null) {
            return false;
        }

        repositorioCliente.remover(cliente);
        return true;
    }

    public List<Cliente> listarTodosClientes() {
        return repositorioCliente.listarTodos();
    }
}
