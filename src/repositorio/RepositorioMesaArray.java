package repositorio;

import java.util.ArrayList;
import java.util.List;
import modelo.Mesa;

public class RepositorioMesaArray implements IRepositorioMesa {

    private List<Mesa> mesas;

    public RepositorioMesaArray() {
        this.mesas = new ArrayList<>();
    }

    @Override
    public Mesa buscarPorNumero(int numero) {
        for (Mesa mesa : mesas) {
            if (mesa.getNumero() == numero) {
                return mesa;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Mesa mesaAtualizada) {
        Mesa mesaAntiga = buscarPorNumero(mesaAtualizada.getNumero());
        if (mesaAntiga != null) {
            mesas.remove(mesaAntiga);
            mesas.add(mesaAtualizada);
        }
    }

    @Override
    public void cadastrar(Mesa mesa) {
        if (buscarPorNumero(mesa.getNumero()) == null) {
            this.mesas.add(mesa);
        }
        // (Idealmente, lançaria uma exceção se já existisse)
    }

    @Override
    public void remover(Mesa mesa) {
        this.mesas.remove(mesa);
    }

    @Override
    public List<Mesa> listarTodas() {
        return new ArrayList<>(this.mesas);
    }
}