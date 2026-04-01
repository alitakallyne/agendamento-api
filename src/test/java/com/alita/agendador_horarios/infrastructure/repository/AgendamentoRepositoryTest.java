package com.alita.agendador_horarios.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.alita.agendador_horarios.factory.AgendamentoFactory;
import com.alita.agendador_horarios.factory.ClienteFactory;
import com.alita.agendador_horarios.factory.ProfissionalFactory;
import com.alita.agendador_horarios.factory.ServicoFactory;
import com.alita.agendador_horarios.infrastructure.entity.Agendamento;
import com.alita.agendador_horarios.infrastructure.entity.Cliente;
import com.alita.agendador_horarios.infrastructure.entity.Profissional;
import com.alita.agendador_horarios.infrastructure.entity.Servico;

import jakarta.persistence.EntityManager;


@ActiveProfiles("test")
public class AgendamentoRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @Test
    @DisplayName("Deve retornar true quando existir conflito de horário para o profissional")
    void deveRetornarTrueQuandoExistirConflitoDeHorario()  {

        Profissional profissional = ProfissionalFactory.profissionalAtivo();
        entityManager.persist(profissional);

        Cliente cliente = ClienteFactory.clienteValido();
        entityManager.persist(cliente);

        Servico servico = ServicoFactory.servicoPadrao();
        entityManager.persist(servico);

        LocalDateTime inicio = LocalDate.now().atTime(10, 0);

        Agendamento agendamento = AgendamentoFactory.agendamentoConfirmado(
                profissional,
                cliente,
                servico,
                inicio);

        entityManager.persist(agendamento);

        boolean existeConflito = agendamentoRepository
                .existsByProfissionalAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                        profissional,
                        inicio.plusMinutes(45),
                        inicio.plusMinutes(15));

        assertTrue(existeConflito);
    }

    @Test
    @DisplayName("Deve listar agendamentos dentro de um intervalo de datas")
    void deveListarAgendamentosPorIntervaloDeDatas()  {

        Profissional profissional = ProfissionalFactory.profissionalAtivo();
        entityManager.persist(profissional);

        Cliente cliente = ClienteFactory.clienteValido();
        entityManager.persist(cliente);

        Servico servico = ServicoFactory.servicoPadrao();
        entityManager.persist(servico);

        LocalDateTime inicio = LocalDate.now().atTime(14, 0);

        Agendamento agendamento = AgendamentoFactory.agendamentoConfirmado(
                profissional,
                cliente,
                servico,
                inicio);

        entityManager.persist(agendamento);

        List<Agendamento> resultado = agendamentoRepository.findByDataHoraInicioBetween(
                inicio.minusHours(1),
                inicio.plusHours(1));

        assertEquals(1, resultado.size());

    }

    @Test
    @DisplayName("Deve listar agendamentos de um profissional em um intervalo")
    void deveListarAgendamentosPorProfissionalEPeriodo() {

        Profissional profissional1 = ProfissionalFactory.profissionalAtivo();
        entityManager.persist(profissional1);

        Profissional profissional2 = ProfissionalFactory.profissionalAtivo();
        entityManager.persist(profissional2);

        Cliente cliente = ClienteFactory.clienteValido();
        entityManager.persist(cliente);

        Servico servico = ServicoFactory.servicoPadrao();
        entityManager.persist(servico);

        LocalDateTime inicio = LocalDate.now().atTime(9, 0);

        entityManager.persist(
                AgendamentoFactory.agendamentoConfirmado(
                        profissional1,
                        cliente,
                        servico,
                        inicio));

        entityManager.persist(
                AgendamentoFactory.agendamentoConfirmado(
                        profissional2,
                        cliente,
                        servico,
                        inicio.plusHours(1)));

        List<Agendamento> resultado = agendamentoRepository
                .findByProfissionalAndDataHoraInicioBetween(
                        profissional1,
                        inicio.minusHours(1),
                        inicio.plusHours(1));

        assertEquals(1, resultado.size());
        assertEquals(profissional1.getId(),
                resultado.get(0).getProfissional().getId());
    }
}
