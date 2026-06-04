# Nav.Care

**Nav.Care** é um sistema de triagem e navegação inteligente para orientar pacientes ao atendimento médico mais adequado.

## Estrutura do projeto

- `backend`: API Spring Boot com PostgreSQL, Flyway e integração com IA
- `frontend`: interface Angular responsiva para triagem e área administrativa
- `docker-compose.yml`: sobe banco, backend e frontend

## Fluxo principal

1. O administrador cadastra especialidades e regras de atendimento.
2. O paciente envia um relato em texto livre.
3. O backend chama a IA com as especialidades cadastradas no banco.
4. A IA devolve especialidade, urgência e resumo.
5. O backend busca as regras associadas e devolve a resposta enriquecida.
6. O frontend exibe o resultado de forma clara, bonita e responsiva.

## Identidade visual

- Nome exibido: **🏥 Nav.Care**
- Subtítulo: **Triagem Inteligente para Direcionamento Médico**
- Paleta: vermelho hospitalar, branco e cinza claro

## Execução local

1. Copie `/.env.example` para `/.env` e ajuste `OPENAI_API_KEY`.
2. Suba o PostgreSQL ou use `docker compose`.
3. Inicie o backend em `backend`.
4. Inicie o frontend em `frontend`.

## Arquivo `.env`

O projeto usa um `.env` na raiz para centralizar a configuração sensível do backend e do Docker.

### Variáveis principais

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `OPENAI_API_KEY`
- `OPENAI_BASE_URL`
- `OPENAI_MODEL`
- `OPENAI_TIMEOUT_SECONDS`

### Como usar

1. Copie `/.env.example` para `/.env`.
2. Substitua `OPENAI_API_KEY` pela chave real.
3. Se quiser, ajuste banco, porta e modelo.
4. Rode `docker compose up --build` ou inicie os apps localmente.

### Frontend

O frontend usa `/api` como base, então normalmente não precisa de variável própria. Em desenvolvimento, o proxy do Angular redireciona as chamadas para o backend local.

## Docker

```bash
docker compose up --build
```

## Swagger

- `http://localhost:8081/swagger`
