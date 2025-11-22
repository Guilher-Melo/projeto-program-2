package controller;

import modelo.Funcionario;
import repositorio.IRepositorioFuncionario;
import java.util.List;

public class FuncionarioController {

    private IRepositorioFuncionario repositorioFuncionario;

    public FuncionarioController(IRepositorioFuncionario repositorioFuncionario) {
        this.repositorioFuncionario = repositorioFuncionario;
    }

    public boolean cadastrarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo");
        }

        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário não pode ser nulo ou vazio");
        }

        // Verifica se já existe um funcionário com o mesmo nome
        if (repositorioFuncionario.buscarPorNome(funcionario.getNome()) != null) {
            return false;
        }

        repositorioFuncionario.cadastrar(funcionario);
        return true;
    }

    public Funcionario buscarFuncionarioPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        return repositorioFuncionario.buscarPorNome(nome);
    }

    public boolean atualizarFuncionario(Funcionario funcionarioAtualizado) {
        if (funcionarioAtualizado == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo");
        }

        Funcionario funcionarioExistente = repositorioFuncionario.buscarPorNome(funcionarioAtualizado.getNome());
        if (funcionarioExistente == null) {
            return false;
        }

        repositorioFuncionario.atualizar(funcionarioAtualizado);
        return true;
    }

    public boolean removerFuncionario(String nome) {
        Funcionario funcionario = repositorioFuncionario.buscarPorNome(nome);
        if (funcionario == null) {
            return false;
        }

        repositorioFuncionario.remover(funcionario);
        return true;
    }

    public List<Funcionario> listarTodosFuncionarios() {
        return repositorioFuncionario.listarTodos();
    }
}
