-- Tabela: cliente
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255),
    telefone VARCHAR(20),
    email VARCHAR(255),
    cpf VARCHAR(14)
);

-- Tabela: profissional
CREATE TABLE profissional (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255),
    especialidade VARCHAR(255),
    telefone VARCHAR(20),
    email VARCHAR(255),
    cpf VARCHAR(14),
    ativo BOOLEAN DEFAULT TRUE
);

-- Tabela: servico
CREATE TABLE servico (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255),
    descricao TEXT,
    preco NUMERIC(10,2),
    duracao_em_minutos INTEGER
);

-- Tabela: horario_funcionamento
CREATE TABLE horario_funcionamento (
    id SERIAL PRIMARY KEY,
    dia_semana VARCHAR(20),
    hora_abertura TIME,
    hora_fechamento TIME
);

-- Tabela: agendamento
CREATE TABLE agendamento (
    id SERIAL PRIMARY KEY,

    servico_id BIGINT,
    profissional_id BIGINT,
    cliente_id BIGINT,

    data_hora_inicio TIMESTAMP,
    data_hora_fim TIMESTAMP,
    data_insercao TIMESTAMP,

    status VARCHAR(50),

    CONSTRAINT fk_agendamento_servico
        FOREIGN KEY (servico_id) REFERENCES servico(id),

    CONSTRAINT fk_agendamento_profissional
        FOREIGN KEY (profissional_id) REFERENCES profissional(id),

    CONSTRAINT fk_agendamento_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);