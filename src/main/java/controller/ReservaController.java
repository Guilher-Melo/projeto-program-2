package controller;

import modelo.Reserva;
import modelo.Mesa;
import modelo.StatusMesa;
import repositorio.IRepositorioReserva;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

public class ReservaController {

    private IRepositorioReserva repositorioReserva;

    public ReservaController(IRepositorioReserva repositorioReserva) {
        this.repositorioReserva = repositorioReserva;
    }

    public boolean fazerReserva(Reserva reserva, Mesa mesa) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }

        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não pode ser nula");
        }

        // Valida número de pessoas vs capacidade da mesa
        if (reserva.getNumeroPessoas() > mesa.getCapacidade()) {
            return false;
        }

        // Valida status da mesa
        if (mesa.getStatus() != StatusMesa.LIVRE) {
            return false;
        }

        repositorioReserva.cadastrar(reserva);
        return true;
    }

    public boolean cancelarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }

        LocalDateTime agora = LocalDateTime.now();
        Duration duracao = Duration.between(agora, reserva.getDataHora());

        // Verifica se o cancelamento é com pelo menos 1 hora de antecedência
        if (duracao.toHours() < 1) {
            return false;
        }

        repositorioReserva.remover(reserva);
        return true;
    }

    public boolean removerReserva(Reserva reserva) {
        if (reserva == null) {
            return false;
        }

        repositorioReserva.remover(reserva);
        return true;
    }

    public List<Reserva> listarTodasReservas() {
        return repositorioReserva.listarTodas();
    }
}
