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
        this.funcionarios.add(funcionario);
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
        for (int i = 0; i < funcionarios.size(); i++) {
            if (funcionarios.get(i).getNome().equalsIgnoreCase(funcionarioAtualizado.getNome())) {
                funcionarios.set(i, funcionarioAtualizado);
                break;
            }
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