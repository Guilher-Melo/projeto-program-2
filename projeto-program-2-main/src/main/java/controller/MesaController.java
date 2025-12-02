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

    // =========================================================
    // MÉTODO CORRIGIDO (O return false bloqueador foi removido)
    // =========================================================
    public boolean alterarStatusMesa(int numeroMesa, StatusMesa novoStatus) {
        Mesa mesa = repositorioMesa.buscarPorNumero(numeroMesa);
        
        // 1. Verifica se a mesa existe
        if (mesa == null) {
            return false; 
        }

        StatusMesa statusAtual = mesa.getStatus();

        // 2. Se o status já for o desejado, apenas retorna true sem fazer nada no banco
        if (statusAtual == novoStatus) {
            return true;
        }

        // 3. Atualiza o status
        // Removemos qualquer bloqueio aqui para permitir que o PedidoController libere a mesa.
        mesa.setStatus(novoStatus);
        
        // 4. Salva a alteração no repositório
        repositorioMesa.atualizar(mesa);
        
        return true;
    }
}