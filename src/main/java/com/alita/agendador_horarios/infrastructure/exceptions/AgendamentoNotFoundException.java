package com.alita.agendador_horarios.infrastructure.exceptions;

public class AgendamentoNotFoundException extends RuntimeException {
  public AgendamentoNotFoundException(String msg) { super(msg); }
}
