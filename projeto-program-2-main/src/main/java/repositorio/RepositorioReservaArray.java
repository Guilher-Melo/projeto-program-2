package repositorio;

import modelo.Reserva;
import java.util.ArrayList;
import java.util.List;

public class RepositorioReservaArray implements IRepositorioReserva {

    private List<Reserva> reservas;

    public RepositorioReservaArray() {
        this.reservas = new ArrayList<>();
    }

    @Override
    public void cadastrar(Reserva reserva) {
        this.reservas.add(reserva);
    }

    @Override
    public void remover(Reserva reserva) {
        this.reservas.remove(reserva);
    }

    @Override
    public List<Reserva> listarTodas() {
        return new ArrayList<>(this.reservas);
    }
}