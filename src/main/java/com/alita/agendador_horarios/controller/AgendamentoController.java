package com.alita.agendador_horarios.controller;

import lombok.RequiredArgsConstructor;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alita.agendador_horarios.infrastructure.dto.AgendamentoResponse;
import com.alita.agendador_horarios.infrastructure.dto.CriarAgendamentoRequest;
import com.alita.agendador_horarios.infrastructure.entity.Agendamento;
import com.alita.agendador_horarios.infrastructure.entity.StatusAgendamento;
import com.alita.agendador_horarios.services.AgendamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos de horários")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @Operation(summary = "Criar agendamento", description = "Cria um novo agendamento respeitando regras de disponibilidade e horário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Agendamento criado com sucesso", content = @Content(schema = @Schema(implementation = AgendamentoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Horário indisponível"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping
    public ResponseEntity<AgendamentoResponse> salvarAgendamento(@RequestBody @Valid CriarAgendamentoRequest request) {
        AgendamentoResponse response = AgendamentoResponse.fromEntity(agendamentoService.salvarAgendamento(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Deletar agendamento", description = "Remove um agendamento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/{idAgendamento}")
    public ResponseEntity<Void> deletarAgendamento(
            @PathVariable Long idAgendamento) {

        agendamentoService.deletarAgendamento(idAgendamento);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar agendamentos por dia", description = "Lista todos os agendamentos de uma data específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping("buscar-agendamentos-dia")
    public ResponseEntity<List<Agendamento>> buscarAgendamentosDia(@RequestParam LocalDate data) {
        return ResponseEntity.ok().body(agendamentoService.buscarAgendamentosDia(data));
    }

    @Operation(summary = "Buscar agendamentos por profissional e dia", description = "Lista agendamentos de um profissional específico em determinada data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })

    @GetMapping("buscar-agendamentosProf-dia")
    public ResponseEntity<List<AgendamentoResponse>> buscarAgendamentoProfissionalDia(
            @RequestParam Long profissionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data

    ) {

        List<Agendamento> agendamentos = agendamentoService.listarPorProfissionalEDia(profissionalId, data);

        return ResponseEntity.ok(
                agendamentos.stream()
                        .map(AgendamentoResponse::fromEntity)
                        .toList());
    }

    @Operation(summary = "Atualizar status do agendamento", description = "Altera o status do agendamento (CONFIRMADO, CANCELADO, CONCLUIDO, etc)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Status inválido")
    })

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusAgendamento status) {

        agendamentoService.atualizarStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reagendar horário", description = "Atualiza a data e hora de um agendamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Horário indisponível")
    })

    @PatchMapping("/{id}/horario")
    public ResponseEntity<Agendamento> atualizarHorario(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {

        Agendamento atualizado = agendamentoService.atualizarHorario(id, data);
        return ResponseEntity.ok(atualizado);
    }
}


