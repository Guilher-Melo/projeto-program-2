package repositorio;

import modelo.Funcionario;
import java.util.ArrayList;
import java.util.List;

public class RepositorioFuncionarioArray implements IRepositorioFuncionario {

    private List<Funcionario> funcionarios;

    public RepositorioFuncionarioArray() {
        this.funcionarios = new ArrayList<>();
    }

    @Override
    public void cadastrar(Funcionario funcionario) {
        if (buscarPorNome(funcionario.getNome()) == null) {
            this.funcionarios.add(funcionario);
        }
    }

    @Override
    public Funcionario buscarPorNome(String nome) {
        for (Funcionario f : funcionarios) {
            if (f.getNome().equalsIgnoreCase(nome)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Funcionario funcionarioAtualizado) {
        Funcionario funcionarioAntigo = buscarPorNome(funcionarioAtualizado.getNome());
        if (funcionarioAntigo != null) {
            funcionarios.remove(funcionarioAntigo);
            funcionarios.add(funcionarioAtualizado);
        }
    }

    @Override
    public void remover(Funcionario funcionario) {
        this.funcionarios.remove(funcionario);
    }

    @Override
    public List<Funcionario> listarTodos() {
        return new ArrayList<>(this.funcionarios);
    }
}