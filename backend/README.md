# Nav.Care Backend

Backend da aplicação **Nav.Care**, responsável pelos CRUDs de Especialidades e Regras de Atendimento, além da triagem assistida por IA.

## Tecnologias

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- Validation
- Flyway
- PostgreSQL
- Lombok
- Springdoc OpenAPI
- RestClient

## Estrutura

- `config`: beans e configurações gerais
- `controller`: endpoints REST
- `dto`: objetos de requisição e resposta
- `entity`: entidades JPA
- `repository`: acesso a dados
- `service`: regras de negócio
- `exception`: tratamento global de erros
- `mapper`: conversão entre entidades e DTOs
- `integration/ai`: integração com Gemini

## Variáveis de ambiente

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `GEMINI_API_KEY`
- `GEMINI_BASE_URL`
- `GEMINI_MODEL`
- `GEMINI_TIMEOUT_SECONDS`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

As credenciais administrativas são persistidas no PostgreSQL pela migration de seed. Não deixe senha fixa no código nem no repositório.

## Execução

O projeto foi configurado para rodar com **Java 25**. Se o ambiente estiver em outra versão, o build e a aplicação vão falhar de propósito para evitar comportamento fora da base esperada.

O backend também tenta carregar automaticamente o arquivo `.env` da raiz do projeto quando você executa a aplicação localmente a partir da pasta `backend` ou da raiz do repositório.

```bash
./mvnw spring-boot:run
```

Se você estiver no Windows sem wrapper, use:

```bash
mvn spring-boot:run
```

## Swagger

- `http://localhost:8081/swagger`

## Configuração da Gemini API

1. Crie uma API key no [Google AI Studio](https://aistudio.google.com/app/apikey).
2. Copie `/.env.example` para `/.env`.
3. Preencha `GEMINI_API_KEY` com a chave gerada.
4. Se quiser, ajuste `GEMINI_MODEL` e `GEMINI_TIMEOUT_SECONDS`.
5. Reinicie o backend para carregar as variáveis.

## Teste rápido

Envie um relato para `POST /api/triage` e verifique se os logs mostram comunicação com a Gemini em vez do fallback local.

## Endpoints

- `GET /api/specialties`
- `GET /api/specialties/{id}`
- `POST /api/specialties`
- `PUT /api/specialties/{id}`
- `DELETE /api/specialties/{id}`
- `GET /api/attendance-rules`
- `GET /api/attendance-rules/{id}`
- `POST /api/attendance-rules`
- `PUT /api/attendance-rules/{id}`
- `DELETE /api/attendance-rules/{id}`
- `POST /api/triage`
