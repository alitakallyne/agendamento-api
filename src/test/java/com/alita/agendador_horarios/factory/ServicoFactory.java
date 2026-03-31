package com.alita.agendador_horarios.factory;

import java.math.BigDecimal;

import com.alita.agendador_horarios.infrastructure.entity.Servico;

public class ServicoFactory {

    public static Servico servicoPadrao() {
        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("Corte de Cabelo");
        servico.setDescricao("Corte masculino tradicional");
        servico.setPreco(BigDecimal.valueOf(35.00));
        servico.setDuracaoEmMinutos(30);
        return servico;
    }
}
