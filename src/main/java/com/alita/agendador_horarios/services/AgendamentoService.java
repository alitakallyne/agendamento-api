package com.alita.agendador_horarios.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.alita.agendador_horarios.AgendadorHorariosApplication;
import com.alita.agendador_horarios.infrastructure.dto.CriarAgendamentoRequest;
import com.alita.agendador_horarios.infrastructure.entity.Agendamento;
import com.alita.agendador_horarios.infrastructure.entity.Cliente;
import com.alita.agendador_horarios.infrastructure.entity.HorarioFuncionamento;
import com.alita.agendador_horarios.infrastructure.entity.Profissional;
import com.alita.agendador_horarios.infrastructure.entity.Servico;
import com.alita.agendador_horarios.infrastructure.entity.StatusAgendamento;
import com.alita.agendador_horarios.infrastructure.repository.AgendamentoRepository;
import com.alita.agendador_horarios.infrastructure.repository.ClienteRepository;
import com.alita.agendador_horarios.infrastructure.repository.HorarioFuncionamentoRepository;
import com.alita.agendador_horarios.infrastructure.repository.ProfissionalRepository;
import com.alita.agendador_horarios.infrastructure.repository.ServicoRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {


    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ClienteRepository clienteRepository;
    private final HorarioFuncionamentoRepository horarioFuncionamentoRepository;

 
    public Agendamento salvarAgendamento(CriarAgendamentoRequest request) {

        Servico servico = servicoRepository.findById(request.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Profissional profissional = profissionalRepository.findById(request.getProfissionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        if (!profissional.getAtivo()) {
            throw new RuntimeException(
                    "Profissional não está ativo para agendamentos");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        LocalDateTime inicio = request.getDataHoraInicio();
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoEmMinutos());

        if (inicio.isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Não é possível agendar para um horário no passado");
        }

        validarHorarioFuncionamento(inicio, fim);

        boolean profissionalDisponivel = agendamentoRepository
                .existsByProfissionalAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                        profissional, fim, inicio);
        if (profissionalDisponivel) {
            throw new RuntimeException("Profissional não está disponível nesse horário");
        }

        Agendamento agendamento = new Agendamento();

        agendamento.setServico(servico);
        agendamento.setProfissional(profissional);
        agendamento.setCliente(cliente);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(fim);
        agendamento.setStatus(StatusAgendamento.CRIADO);
        agendamento.setDataInsercao(LocalDateTime.now());

        return agendamentoRepository.save(agendamento);

    }

    private void validarHorarioFuncionamento(LocalDateTime inicio, LocalDateTime fim) {

        if (!inicio.toLocalDate().equals(fim.toLocalDate())) {
            throw new RuntimeException(
                    "O agendamento não pode ultrapassar o dia atual");
        }

        DayOfWeek diaSemana = inicio.getDayOfWeek();

        HorarioFuncionamento horario = horarioFuncionamentoRepository.findByDiaSemana(diaSemana)
                .orElseThrow(
                        () -> new RuntimeException("Horário de funcionamento não encontrado para o dia: " + diaSemana));

        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFim = fim.toLocalTime();

        if (horaInicio.isBefore(horario.getHoraAbertura())
                || horaFim.isAfter(horario.getHoraFechamento())) {

            throw new RuntimeException(
                    "Agendamento fora do horário de funcionamento");
        }

    }

    public void deletarAgendamento(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (agendamento.getStatus() == StatusAgendamento.CONFIRMADO) {
            throw new RuntimeException("gendamento concluído não pode ser excluído");
        }

        agendamentoRepository.delete(agendamento);
    }

    public List<Agendamento> buscarAgendamentosDia(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);

        return agendamentoRepository.findByDataHoraInicioBetween(inicio, fim);
    }

    public List<Agendamento> listarPorProfissionalEDia(Long profissionalId, LocalDate data) {

        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);

        return agendamentoRepository
                .findByProfissionalAndDataHoraInicioBetween(profissional, inicio, fim);

    }

    public void atualizarStatus(Long id, StatusAgendamento novoStatus) {

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (agendamento.getStatus() == StatusAgendamento.CONFIRMADO) {
            throw new RuntimeException("Agendamento já foi concluído");
        }

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO
                && novoStatus == StatusAgendamento.CANCELADO) {
            throw new RuntimeException("Agendamento cancelado não pode ser concluído");
        }

        agendamento.setStatus(novoStatus);
        agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizarHorario(Long id, LocalDateTime data) {
    Agendamento agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

    if (agendamento.getStatus() != StatusAgendamento.CRIADO) {
        throw new RuntimeException(
                "Só é possível alterar o horário de agendamentos criados");
    }

    if (data.isBefore(LocalDateTime.now())) {
        throw new RuntimeException(
                "Não é possível reagendar para um horário no passado");
    }

    Servico servico = agendamento.getServico();
    Profissional profissional = agendamento.getProfissional();

    LocalDateTime novoFim = data.plusMinutes(servico.getDuracaoEmMinutos());

    validarHorarioFuncionamento(data, novoFim);

    boolean conflito = agendamentoRepository
            .existsByProfissionalAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    profissional,
                    novoFim,
                    data
            );

    if (conflito) {
        throw new RuntimeException(
                "Profissional não está disponível nesse novo horário");
    }

    agendamento.setDataHoraInicio(data);
    agendamento.setDataHoraFim(novoFim);

    return agendamentoRepository.save(agendamento);
}
}
