package com.alita.agendador_horarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.alita.agendador_horarios.infrastructure.dto.CriarAgendamentoRequest;
import com.alita.agendador_horarios.infrastructure.entity.Agendamento;
import com.alita.agendador_horarios.infrastructure.entity.Cliente;
import com.alita.agendador_horarios.infrastructure.entity.Profissional;
import com.alita.agendador_horarios.infrastructure.entity.Servico;
import com.alita.agendador_horarios.infrastructure.entity.StatusAgendamento;
import com.alita.agendador_horarios.infrastructure.exceptions.AgendamentoConflictException;
import com.alita.agendador_horarios.infrastructure.exceptions.AgendamentoInvalidException;
import com.alita.agendador_horarios.infrastructure.exceptions.GlobalExceptionHandler;
import com.alita.agendador_horarios.services.AgendamentoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
public class AgendamentoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AgendamentoService agendamentoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CriarAgendamentoRequest request;

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = standaloneSetup(new AgendamentoController(agendamentoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();

        request = new CriarAgendamentoRequest();
        agendamento = new Agendamento();
        request.setServicoId(1L);
        request.setProfissionalId(1L);
        request.setClienteId(1L);
        request.setDataHoraInicio(LocalDateTime.now().plusDays(1));

        Servico servico = new Servico();
        servico.setNome("Corte");

        Profissional profissional = new Profissional();
        profissional.setNome("João");

        Cliente cliente = new Cliente();
        cliente.setNome("Maria");

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setStatus(StatusAgendamento.CRIADO);
        agendamento.setServico(servico);
        agendamento.setProfissional(profissional);
        agendamento.setCliente(cliente);

    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void deveCriarAgendamentoComSucesso() throws Exception {
        when(agendamentoService.salvarAgendamento(any()))
                .thenReturn(agendamento);

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CRIADO"));
    }

    @Test
    @DisplayName("Deve testar o retorno de dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {

        request.setServicoId(null);

        when(agendamentoService.salvarAgendamento(any()))
                .thenThrow(new AgendamentoInvalidException("Dados inválidos"));

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve testar conflito de horários")
    void deveRetornarErroQuandoHorarioIndisponivel() throws Exception {

        when(agendamentoService.salvarAgendamento(any()))
                .thenThrow(new AgendamentoConflictException("Profissional não está disponível nesse horário"));

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("não está disponível")));
    }

    @Test
    @DisplayName("Buscar agendamentos")
    void deveBuscarAgendamentosPorDia() throws Exception {

        when(agendamentoService.buscarAgendamentosDia(any()))
                .thenReturn(List.of(agendamento));

        mockMvc.perform(get("/agendamentos/buscar-agendamentos-dia")
                .param("data", "2026-04-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CRIADO"));
    }

}
