package com.alita.agendador_horarios.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alita.agendador_horarios.factory.AgendamentoFactory;
import com.alita.agendador_horarios.factory.ClienteFactory;
import com.alita.agendador_horarios.factory.CriarAgendamentoRequestFactory;
import com.alita.agendador_horarios.factory.HorarioFuncionamentoFactory;
import com.alita.agendador_horarios.factory.ProfissionalFactory;
import com.alita.agendador_horarios.factory.ServicoFactory;
import com.alita.agendador_horarios.infrastructure.dto.CriarAgendamentoRequest;
import com.alita.agendador_horarios.infrastructure.entity.Agendamento;
import com.alita.agendador_horarios.infrastructure.entity.Cliente;
import com.alita.agendador_horarios.infrastructure.entity.Profissional;
import com.alita.agendador_horarios.infrastructure.entity.Servico;
import com.alita.agendador_horarios.infrastructure.entity.StatusAgendamento;
import com.alita.agendador_horarios.infrastructure.repository.AgendamentoRepository;
import com.alita.agendador_horarios.infrastructure.repository.ClienteRepository;
import com.alita.agendador_horarios.infrastructure.repository.HorarioFuncionamentoRepository;
import com.alita.agendador_horarios.infrastructure.repository.ProfissionalRepository;
import com.alita.agendador_horarios.infrastructure.repository.ServicoRepository;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {

        @Mock
        AgendamentoRepository agendamentoRepository;
        @Mock
        ServicoRepository servicoRepository;
        @Mock
        ProfissionalRepository profissionalRepository;
        @Mock
        ClienteRepository clienteRepository;
        @Mock
        HorarioFuncionamentoRepository horarioFuncionamentoRepository;

        @InjectMocks
        AgendamentoService agendamentoService;

        

        @Test
        @DisplayName("Deve salvar agendamento quando profissional estiver disponível e dentro do horário de funcionamento")
        void deveSalvarAgendamentoSemConflito() {

                CriarAgendamentoRequest request = CriarAgendamentoRequestFactory.valido();

                Servico servico = ServicoFactory.servicoPadrao();
                Profissional profissional = ProfissionalFactory.profissionalAtivo();
                Cliente cliente = ClienteFactory.clienteValido();

                LocalDateTime inicio = request.getDataHoraInicio();
                DayOfWeek day = inicio.getDayOfWeek();

                System.out.println("ServicoId: " + request.getServicoId());

                when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
                when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
                when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

                when(horarioFuncionamentoRepository.findByDiaSemana(day))
                                .thenReturn(Optional.of(HorarioFuncionamentoFactory.horarioPadrao(day)));

                when(agendamentoRepository
                                .existsByProfissionalAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                                                any(), any(), any()))
                                .thenReturn(false);

                when(agendamentoRepository.save(any()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Agendamento agendamento = agendamentoService.salvarAgendamento(request);

                assertNotNull(agendamento);
                assertEquals(StatusAgendamento.CRIADO, agendamento.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção quando existir conflito de horário para o profissional")
        void deveBloquearAgendamentoComConflito() {

                CriarAgendamentoRequest request = CriarAgendamentoRequestFactory.valido();

                LocalDateTime inicio = request.getDataHoraInicio();
                DayOfWeek day = inicio.getDayOfWeek();

                Servico servico = ServicoFactory.servicoPadrao();
                Profissional profissional = ProfissionalFactory.profissionalAtivo();
                Cliente cliente = ClienteFactory.clienteValido();

                when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
                when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
                when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

                // 🔥 ESSENCIAL
                when(horarioFuncionamentoRepository.findByDiaSemana(day))
                                .thenReturn(Optional.of(HorarioFuncionamentoFactory.horarioPadrao(day)));

                // 🔥 FORÇA CONFLITO
                when(agendamentoRepository
                                .existsByProfissionalAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                                                any(), any(), any()))
                                .thenReturn(true);

                RuntimeException ex = assertThrows(
                                RuntimeException.class,
                                () -> agendamentoService.salvarAgendamento(request));

                System.out.println("ERRO: " + ex.getMessage());

                assertTrue(ex.getMessage().contains("não está disponível"));
        }

        @Test
        @DisplayName("Deve alterar horário do agendamento quando status for CRIADO e não houver conflito")
        void deveAlterarHorarioDoAgendamento() {

                Servico servico = ServicoFactory.servicoPadrao();
                Profissional profissional = ProfissionalFactory.profissionalAtivo();
                Cliente cliente = ClienteFactory.clienteValido();

                LocalDateTime inicio = LocalDateTime.of(2026, 4, 10, 10, 0);

                Agendamento agendamento = AgendamentoFactory.agendamentoConfirmado(
                                profissional,
                                cliente,
                                servico,
                                inicio);

                agendamento.setStatus(StatusAgendamento.CRIADO);

                LocalDateTime novoInicio = inicio.plusHours(1);
                DayOfWeek day = novoInicio.getDayOfWeek();

                when(agendamentoRepository.findById(1L))
                                .thenReturn(Optional.of(agendamento));

                when(horarioFuncionamentoRepository.findByDiaSemana(day))
                                .thenReturn(Optional.of(HorarioFuncionamentoFactory.horarioPadrao(day)));

                when(agendamentoRepository
                                .existsByProfissionalAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                                                any(), any(), any()))
                                .thenReturn(false);

                when(agendamentoRepository.save(any()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Agendamento atualizado = agendamentoService.atualizarHorario(1L, novoInicio);

                assertEquals(novoInicio, atualizado.getDataHoraInicio());
        }
}
