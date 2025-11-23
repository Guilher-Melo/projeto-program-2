package repositorio;

import modelo.Funcionario;
import java.util.List;

public interface IRepositorioFuncionario {
    void cadastrar(Funcionario funcionario);
    Funcionario buscarPorNome(String nome);
    void atualizar(Funcionario funcionario);
    void remover(Funcionario funcionario);
    List<Funcionario> listarTodos();
}