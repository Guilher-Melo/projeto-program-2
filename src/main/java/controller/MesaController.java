package controller;

import modelo.Mesa;
import modelo.StatusMesa;
import repositorio.IRepositorioMesa;
import java.util.List;

public class MesaController {

    private IRepositorioMesa repositorioMesa;

    public MesaController(IRepositorioMesa repositorioMesa) {
        this.repositorioMesa = repositorioMesa;
    }

    public boolean cadastrarMesa(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não pode ser nula");
        }

        if (mesa.getCapacidade() <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero");
        }

        // Verifica se já existe uma mesa com o mesmo número
        if (repositorioMesa.buscarPorNumero(mesa.getNumero()) != null) {
            return false;
        }

        repositorioMesa.cadastrar(mesa);
        return true;
    }

    public Mesa buscarMesaPorNumero(int numero) {
        return repositorioMesa.buscarPorNumero(numero);
    }

    public boolean atualizarMesa(Mesa mesaAtualizada) {
        if (mesaAtualizada == null) {
            throw new IllegalArgumentException("Mesa não pode ser nula");
        }

        Mesa mesaExistente = repositorioMesa.buscarPorNumero(mesaAtualizada.getNumero());
        if (mesaExistente == null) {
            return false;
        }

        repositorioMesa.atualizar(mesaAtualizada);
        return true;
    }

    public boolean alterarStatusMesa(int numeroMesa, StatusMesa novoStatus) {
        Mesa mesa = repositorioMesa.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            return false;
        }

        mesa.alterarStatus(novoStatus);
        repositorioMesa.atualizar(mesa);
        return true;
    }

    public boolean removerMesa(int numero) {
        Mesa mesa = repositorioMesa.buscarPorNumero(numero);
        if (mesa == null) {
            return false;
        }

        repositorioMesa.remover(mesa);
        return true;
    }

    public List<Mesa> listarTodasMesas() {
        return repositorioMesa.listarTodas();
    }
}
