package com.alita.agendador_horarios.factory;

import com.alita.agendador_horarios.infrastructure.entity.Cliente;

public class ClienteFactory {

    public static Cliente clienteValido() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Oliveira");
        cliente.setTelefone("85999999999");
        cliente.setEmail("maria@email.com");
        cliente.setCpf("12345678901");
        return cliente;
    }
}
