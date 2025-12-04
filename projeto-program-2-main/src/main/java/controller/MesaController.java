package controller;

import java.util.List;

import modelo.Mesa;
import modelo.StatusMesa;
import repositorio.IRepositorioMesa;

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
    // NOVO MÉTODO PARA REDIMENSIONAR
    public void atualizarQuantidadeMesas(int novaQuantidade) throws Exception {
        List<Mesa> mesasAtuais = repositorioMesa.listarTodas();
        int qtdAtual = mesasAtuais.size();

        if (novaQuantidade == qtdAtual) return; // Nada a fazer

        if (novaQuantidade > qtdAtual) {
            // ADICIONAR MESAS
            for (int i = qtdAtual + 1; i <= novaQuantidade; i++) {
                int capacidade = (i % 2 == 0) ? 2 : 4; // Lógica par/impar
                Mesa novaMesa = new Mesa(i, capacidade, StatusMesa.LIVRE);
                repositorioMesa.cadastrar(novaMesa);
            }
        } else {
            // REMOVER MESAS (Do fim para o começo)
            // Só permite remover se a mesa estiver LIVRE
            for (int i = qtdAtual; i > novaQuantidade; i--) {
                Mesa mesaParaRemover = repositorioMesa.buscarPorNumero(i);
                
                if (mesaParaRemover.getStatus() != StatusMesa.LIVRE) {
                    throw new Exception("Não é possível reduzir para " + novaQuantidade + 
                        " mesas pois a Mesa " + i + " está ocupada ou reservada.");
                }
                repositorioMesa.remover(mesaParaRemover);
            }
        }
    }
}