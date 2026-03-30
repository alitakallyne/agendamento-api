# 📅 Sistema de Agendamento de Horários

API REST desenvolvida em **Java + Spring Boot** para gerenciamento de agendamentos profissionais (salões, clínicas, consultórios, etc), com foco em **regras de negócio realistas** e validações de disponibilidade.

---

# 📌 📖 Sobre o projeto

O sistema permite:

* ✅ Criar agendamentos
* 📅 Consultar agendamentos por data
* 👨‍⚕️ Consultar agenda por profissional
* 🔄 Reagendar horários
* ❌ Cancelar agendamentos
* ✔️ Atualizar status (CRIADO, CANCELADO, CONCLUÍDO, etc)

---

# 🧠 🛠️ Regras de Negócio Implementadas

Este projeto não é apenas CRUD — ele possui validações reais:

* 🚫 Não permite agendamento em horários já ocupados
* ⏰ Respeita horário de funcionamento por dia da semana
* 👨‍⚕️ Valida disponibilidade do profissional
* 🔄 Permite alteração de horário apenas em estados válidos
* 📌 Controle de status do agendamento

---

# 🧩 📦 Entidades Principais

* Cliente
* Profissional
* Serviço
* Agendamento
* Horário de Funcionamento

---

# 🚀 ⚙️ Tecnologias Utilizadas

* ☕ Java 17
* 🌱 Spring Boot
* 🛡️ Spring Validation
* 🧪 JUnit 5
* 🎭 Mockito
* 🗄️ H2 Database (ambiente local)
* 🛠 Maven

---

# 🧪 🔬 Testes Unitários

O projeto possui testes unitários focados na **camada de serviço**, validando as principais regras de negócio.

### ✔️ Cenários testados

* ✔️ Criação de agendamento sem conflito
* ❌ Bloqueio de agendamento com conflito de horário
* 🔄 Alteração de horário válida

### ⚠️ Observação

Os testes estão em evolução e fazem parte do processo de aprendizado.
Testes de controller serão implementados após estabilização dos testes de serviço.

---

# 📂 🏗️ Estrutura do Projeto

```text
src/
 └───com.alita.agendador_horarios
      │   AgendadorHorariosApplication.java
      │
      ├───controller
      │       AgendamentoController.java
      │
      ├───infrastructure
      │   ├───dto
      │   ├───entity
      │   └───repository
      │
      └───services
              AgendamentoService.java
```

---

# ▶️ 🚀 Como Executar o Projeto

### 1️⃣ Clonar o repositório

```bash
git clone https://github.com/alitakallyne/agendamento-api.git
```

### 2️⃣ Acessar o diretório

```bash
cd agendamento-api
```

### 3️⃣ Executar a aplicação

```bash
./mvnw spring-boot:run
```

---

# 🌐 🧪 Testando a API

Após subir a aplicação:

```text
http://localhost:8080/agendamentos
```

Você pode testar via:

* Postman
* Insomnia
* Swagger 

---

# 💡 📈 Melhorias Futuras

* 🔐 Autenticação e autorização (JWT)
* 📦 Dockerização
* ☁️ Deploy em nuvem

---

# 🎓 📌 Aprendizados

Este projeto demonstra na prática:

* Implementação de regras de negócio complexas
* Uso de testes unitários com Mockito
* Organização em camadas (Controller, Service, Repository)
* Boas práticas com Spring Boot

---

# 👩‍💻 Autora

Desenvolvido por **Alita Kallyne do Nascimento**

---

# 📜 Licença

Projeto de uso livre para fins de estudo e aprendizado.
