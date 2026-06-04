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
- `integration/ai`: integração com OpenAI

## Variáveis de ambiente

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `OPENAI_API_KEY`
- `OPENAI_BASE_URL`
- `OPENAI_MODEL`
- `OPENAI_TIMEOUT_SECONDS`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

As credenciais administrativas agora são persistidas no PostgreSQL pela migration de seed. Não deixe senha fixa no código nem no repositório.

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
