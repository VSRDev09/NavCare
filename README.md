# Nav.Care

**Nav.Care** é um sistema de triagem e navegação inteligente para orientar pacientes ao atendimento médico mais adequado.

## Estrutura do projeto

- `backend`: API Spring Boot com PostgreSQL, Flyway e integração com Gemini
- `frontend`: interface Angular responsiva para triagem e área administrativa
- `docker-compose.yml`: sobe banco, backend e frontend

## Fluxo principal

1. O administrador faz login com credenciais persistidas no PostgreSQL.
2. O administrador cadastra especialidades e regras de atendimento.
3. O paciente envia um relato em texto livre.
4. O backend chama a Gemini API com as especialidades cadastradas no banco.
5. A IA devolve especialidade, urgência e resumo.
6. O backend busca as regras associadas e devolve a resposta enriquecida.
7. O frontend exibe o resultado de forma clara, bonita e responsiva.

## Identidade visual

- Nome exibido: **🏥 Nav.Care**
- Subtítulo: **Triagem Inteligente para Direcionamento Médico**
- Paleta: vermelho hospitalar, branco e cinza claro

## Execução local

1. Copie `/.env.example` para `/.env` e ajuste `GEMINI_API_KEY`.
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
- `GEMINI_API_KEY`
- `GEMINI_BASE_URL`
- `GEMINI_MODEL`
- `GEMINI_TIMEOUT_SECONDS`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

### Como usar

1. Copie `/.env.example` para `/.env`.
2. Substitua `GEMINI_API_KEY` pela chave real.
3. Se quiser, ajuste banco, porta e modelo.
4. Rode `docker compose up --build` ou inicie os apps localmente.

### Frontend

O frontend usa `/api` como base, então normalmente não precisa de variável própria. Em desenvolvimento, o proxy do Angular redireciona as chamadas para o backend local.

## Como gerar e testar a chave Gemini

1. Acesse o [Google AI Studio](https://aistudio.google.com/app/apikey).
2. Clique para criar uma nova API key.
3. Copie a chave para o seu arquivo `/.env`.
4. Rode o backend e faça uma requisição `POST /api/triage`.
5. Se a chave estiver correta, os logs não devem mais mostrar fallback por ausência de credencial.

## Docker

```bash
docker compose up --build
```

## Swagger

- `http://localhost:8081/swagger`
