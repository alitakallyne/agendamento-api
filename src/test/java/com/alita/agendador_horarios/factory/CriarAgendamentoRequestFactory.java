package com.alita.agendador_horarios.factory;

import java.time.LocalDateTime;

import com.alita.agendador_horarios.infrastructure.dto.CriarAgendamentoRequest;

public class CriarAgendamentoRequestFactory {
    public static CriarAgendamentoRequest valido() {
        CriarAgendamentoRequest request = new CriarAgendamentoRequest();

        request.setClienteId(1L);
        request.setProfissionalId(1L);
        request.setServicoId(1L);
        request.setDataHoraInicio(LocalDateTime.of(2026, 4, 10, 10, 0));

        return request;
    }
}
