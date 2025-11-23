package repositorio;

import modelo.Cliente;
import java.util.List;

// REQ01: Permitir o cadastro de clientes
public interface IRepositorioCliente {

    void cadastrar(Cliente cliente);
    Cliente buscarPorTelefone(String telefone); // Telefone é a chave única
    void atualizar(Cliente cliente);
    void remover(Cliente cliente);
    List<Cliente> listarTodos();
}