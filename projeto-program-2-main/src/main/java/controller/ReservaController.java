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
        if (novaReserva == null || mesa == null) {
            throw new IllegalArgumentException("Dados inválidos para reserva.");
        }

        // 1. Validação de Capacidade
        if (novaReserva.getNumeroPessoas() > mesa.getCapacidade()) {
            return false;
        }

        // 2. Validação de Conflito de Horário (Permite reservar várias vezes se horários forem diferentes)
        // Regra: Não pode haver outra reserva para a mesma mesa num intervalo de +/- 2 horas (exemplo seguro)
        // ou usando a lógica de 30 min do usuário. Vamos usar um intervalo seguro de 1h30 para garantir.
        List<Reserva> reservasExistentes = repositorioReserva.listarTodas();
        for (Reserva r : reservasExistentes) {
            if (r.getMesa().getNumero() == mesa.getNumero()) {
                LocalDateTime inicioR = r.getDataHora();
                LocalDateTime fimR = r.getDataHora().plusMinutes(90); // Supomos que dura 1h30

                LocalDateTime inicioNova = novaReserva.getDataHora();
                
                // Verifica sobreposição
                if (inicioNova.isBefore(fimR) && inicioNova.isAfter(inicioR.minusMinutes(90))) {
                    // Horário conflitante
                    return false; 
                }
            }
        }

        // 3. Salva a reserva
        repositorioReserva.cadastrar(novaReserva);

        // 4. ATUALIZAÇÃO: NÃO muda o status da mesa imediatamente para RESERVADA.
        // O status só mudará 1 hora antes (verificado no método verificarStatusDasReservas).
        
        return true;
    }

    /**
     * Método Mágico: Verifica todas as reservas e atualiza o status das mesas
     * conforme a regra de 1 hora antes e expiração de 30 min.
     */
    public void verificarStatusDasReservas(MesaController mesaController) {
        LocalDateTime agora = LocalDateTime.now();
        List<Reserva> todasReservas = repositorioReserva.listarTodas();
        List<Reserva> reservasVencidas = new ArrayList<>();

        for (Reserva r : todasReservas) {
            Mesa mesa = r.getMesa();
            LocalDateTime horaReserva = r.getDataHora();
            
            // Definição dos Limites
            LocalDateTime horaInicioAlerta = horaReserva.minusHours(1); // 1 hora antes
            LocalDateTime horaExpiracao = horaReserva.plusMinutes(30);  // 30 min tolerância

            // REGRA 1: Exclusão automática após 30 min (Tolerância)
            if (agora.isAfter(horaExpiracao)) {
                reservasVencidas.add(r);
                
                // Se a mesa estava como RESERVADA, libera ela
                if (mesa.getStatus() == StatusMesa.RESERVADA) {
                    mesaController.alterarStatusMesa(mesa.getNumero(), StatusMesa.LIVRE);
                }
            }
            // REGRA 2: Aparecer como RESERVADA 1 hora antes
            else if (agora.isAfter(horaInicioAlerta) && agora.isBefore(horaExpiracao)) {
                // Só muda se estiver LIVRE (se estiver OCUPADA com gente comendo, não mexe)
                if (mesa.getStatus() == StatusMesa.LIVRE) {
                    mesaController.alterarStatusMesa(mesa.getNumero(), StatusMesa.RESERVADA);
                }
            }
        }

        // Remove as reservas vencidas do banco/lista
        for (Reserva r : reservasVencidas) {
            repositorioReserva.remover(r);
            System.out.println("Reserva expirada removida: Mesa " + r.getMesa().getNumero());
        }
    }

    public boolean cancelarReserva(Reserva reserva, MesaController mesaController) {
        if (reserva == null) return false;

        repositorioReserva.remover(reserva);

        // Se a mesa estava marcada como RESERVADA por causa desta reserva, libera
        if (reserva.getMesa().getStatus() == StatusMesa.RESERVADA) {
            mesaController.alterarStatusMesa(reserva.getMesa().getNumero(), StatusMesa.LIVRE);
        }

        return true;
    }

    public List<Reserva> listarTodasReservas() {
        return repositorioReserva.listarTodas();
    }
}