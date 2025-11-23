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

        if (funcionario.getSenha() == null || funcionario.getSenha().trim().isEmpty()) {
             throw new IllegalArgumentException("Senha é obrigatória");
        }

        if (repositorioFuncionario.buscarPorNome(funcionario.getNome()) != null) {
            return false;
        }

        repositorioFuncionario.cadastrar(funcionario);
        return true;
    }

    public Funcionario login(String nome, String senha) {
        if (nome == null || senha == null) {
            return null;
        }

        Funcionario f = repositorioFuncionario.buscarPorNome(nome);

        if (f != null && f.getSenha().equals(senha)) {
            return f;
        }

        return null;
    }

    public Funcionario buscarFuncionarioPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        return repositorioFuncionario.buscarPorNome(nome);
    }

    public boolean atualizarFuncionario(Funcionario funcionarioAtualizado) {
        if (funcionarioAtualizado == null) return false;
        Funcionario f = repositorioFuncionario.buscarPorNome(funcionarioAtualizado.getNome());
        if (f == null) return false;
        repositorioFuncionario.atualizar(funcionarioAtualizado);
        return true;
    }

    public boolean removerFuncionario(String nome) {
        Funcionario f = repositorioFuncionario.buscarPorNome(nome);
        if (f == null) return false;
        repositorioFuncionario.remover(f);
        return true;
    }

    public List<Funcionario> listarTodosFuncionarios() {
        return repositorioFuncionario.listarTodos();
    }
}