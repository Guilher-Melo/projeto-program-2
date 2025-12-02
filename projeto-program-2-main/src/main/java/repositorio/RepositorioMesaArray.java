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
        for (int i = 0; i < mesas.size(); i++) {
            if (mesas.get(i).getNumero() == mesaAtualizada.getNumero()) {
                mesas.set(i, mesaAtualizada);
                break;
            }
        }
    }

    @Override
    public void cadastrar(Mesa mesa) {
        this.mesas.add(mesa);
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