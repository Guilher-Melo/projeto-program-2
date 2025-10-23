package repositorio;

import modelo.Reserva;
import java.util.List;

public interface IRepositorioReserva {

    void cadastrar(Reserva reserva);
    void remover(Reserva reserva);
    List<Reserva> listarTodas();

    // (A classe Reserva não tem ID, então não podemos criar um "buscar" por ID ainda)
}