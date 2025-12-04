package controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import modelo.Mesa;
import modelo.Reserva;
import modelo.StatusMesa;
import repositorio.IRepositorioReserva;

public class ReservaController {

    private IRepositorioReserva repositorioReserva;

    public ReservaController(IRepositorioReserva repositorioReserva) {
        this.repositorioReserva = repositorioReserva;
    }

    public boolean fazerReserva(Reserva novaReserva, Mesa mesa, MesaController mesaController) {
        // ... (código de fazerReserva permanece igual) ...
        // Mantive oculto para focar na alteração, mas o código original estava correto
        if (novaReserva == null || mesa == null) {
            throw new IllegalArgumentException("Dados inválidos para reserva.");
        }
        if (novaReserva.getNumeroPessoas() > mesa.getCapacidade()) {
            return false;
        }
        List<Reserva> reservasExistentes = repositorioReserva.listarTodas();
        for (Reserva r : reservasExistentes) {
            if (r.getMesa().getNumero() == mesa.getNumero()) {
                LocalDateTime inicioR = r.getDataHora();
                LocalDateTime fimR = r.getDataHora().plusMinutes(90);
                LocalDateTime inicioNova = novaReserva.getDataHora();
                if (inicioNova.isBefore(fimR) && inicioNova.isAfter(inicioR.minusMinutes(90))) {
                    return false; 
                }
            }
        }
        repositorioReserva.cadastrar(novaReserva);
        return true;
    }

    public void verificarStatusDasReservas(MesaController mesaController) {
        // ... (código de verificarStatus permanece igual) ...
        // Mantive oculto para focar na alteração
        LocalDateTime agora = LocalDateTime.now();
        List<Reserva> todasReservas = repositorioReserva.listarTodas();
        List<Reserva> reservasVencidas = new ArrayList<>();

        for (Reserva r : todasReservas) {
            Mesa mesa = r.getMesa();
            LocalDateTime horaReserva = r.getDataHora();
            LocalDateTime horaInicioAlerta = horaReserva.minusHours(1);
            LocalDateTime horaExpiracao = horaReserva.plusMinutes(30);

            if (agora.isAfter(horaExpiracao)) {
                reservasVencidas.add(r);
                if (mesa.getStatus() == StatusMesa.RESERVADA) {
                    mesaController.alterarStatusMesa(mesa.getNumero(), StatusMesa.LIVRE);
                }
            }
            else if (agora.isAfter(horaInicioAlerta) && agora.isBefore(horaExpiracao)) {
                if (mesa.getStatus() == StatusMesa.LIVRE) {
                    mesaController.alterarStatusMesa(mesa.getNumero(), StatusMesa.RESERVADA);
                }
            }
        }
        for (Reserva r : reservasVencidas) {
            repositorioReserva.remover(r);
        }
    }

    // --- MÉTODO ALTERADO PARA ATENDER AO REQ16 ---
    public boolean cancelarReserva(Reserva reserva, MesaController mesaController) {
        if (reserva == null) return false;

        // 1. Obtém hora atual e hora da reserva
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime horaReserva = reserva.getDataHora();

        // 2. Calcula a diferença em horas (ou verifica se 'agora' já passou do limite)
        // O limite é: hora da reserva menos 1 hora.
        // Se AGORA for DEPOIS desse limite, significa que falta menos de 1h (ou já passou).
        LocalDateTime limiteCancelamento = horaReserva.minusHours(1);

        if (agora.isAfter(limiteCancelamento)) {
            
            System.out.println("Erro: Tentativa de cancelamento com menos de 1h de antecedência.");
            return false; // Retorna falso para indicar que não foi possível cancelar
        }

        // 3. Se passou na validação, remove a reserva
        repositorioReserva.remover(reserva);

        // Se a mesa estava reservada, libera ela
        if (reserva.getMesa().getStatus() == StatusMesa.RESERVADA) {
            mesaController.alterarStatusMesa(reserva.getMesa().getNumero(), StatusMesa.LIVRE);
        }

        return true;
    }

    public List<Reserva> listarTodasReservas() {
        return repositorioReserva.listarTodas();
    }
}