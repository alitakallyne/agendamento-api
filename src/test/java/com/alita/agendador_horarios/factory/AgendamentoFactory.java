package com.alita.agendador_horarios.factory;

import java.time.LocalDateTime;

import com.alita.agendador_horarios.infrastructure.entity.Agendamento;
import com.alita.agendador_horarios.infrastructure.entity.Cliente;
import com.alita.agendador_horarios.infrastructure.entity.Profissional;
import com.alita.agendador_horarios.infrastructure.entity.Servico;
import com.alita.agendador_horarios.infrastructure.entity.StatusAgendamento;

public class AgendamentoFactory {
public static Agendamento agendamentoConfirmado(
            Profissional profissional,
            Cliente cliente,
            Servico servico,
            LocalDateTime inicio
    ) {
        Agendamento agendamento = new Agendamento();
        agendamento.setProfissional(profissional);
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(
            inicio.plusMinutes(servico.getDuracaoEmMinutos())
        );
        agendamento.setStatus(StatusAgendamento.CRIADO);
        agendamento.setId(1L);
        return agendamento;
    }
}
