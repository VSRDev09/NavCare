import { HttpErrorResponse } from '@angular/common/http';

export function extractErrorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (typeof error.error?.message === 'string' && error.error.message.trim()) {
      return error.error.message;
    }

    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    if (error.status === 0) {
      return 'Não foi possível conectar ao backend. Verifique se a API está rodando.';
    }

    return 'Ocorreu um erro ao processar a solicitação.';
  }

  return 'Ocorreu um erro inesperado.';
}
