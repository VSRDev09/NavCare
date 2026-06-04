# Nav.Care Frontend

Interface Angular da aplicação **Nav.Care**, criada para triagem inteligente e administração de dados clínicos.

## Tecnologias

- Angular
- TypeScript
- CSS
- Angular Router
- Reactive Forms
- HttpClient

## Estrutura

- `core`: componentes visuais compartilhados e serviços de estado da interface
- `services`: comunicação com o backend
- `models`: contratos de dados
- `pages`: telas da aplicação
- `shared`: utilitários reutilizáveis

## Como executar

```bash
npm install
npm start
```

Depois, acesse:

- `http://localhost:4200`

## Comunicação com o backend

O frontend consome a API do backend por rota relativa:

- `/api`

No desenvolvimento local, o Angular usa `proxy.conf.json` para encaminhar `/api` para `http://localhost:8081`.

## Arquivo `.env`

O frontend não precisa de segredo próprio. Se você quiser padronizar a configuração do projeto inteiro, use o `.env` da raiz para o backend e para o Docker Compose.
