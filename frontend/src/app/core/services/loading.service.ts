import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map } from 'rxjs';

interface LoadingState {
  visible: boolean;
  message: string;
}

@Injectable()
export class LoadingService {
  // Eu uso contador porque mais de uma operacao pode estar em andamento ao mesmo tempo,
  // e eu nao quero esconder o loading antes da hora.
  private counter = 0;
  private readonly loadingSubject = new BehaviorSubject<LoadingState>({
    visible: false,
    message: 'Processando...'
  });

  readonly loading$: Observable<boolean> = this.loadingSubject.asObservable().pipe(map(state => state.visible));
  readonly message$: Observable<string> = this.loadingSubject.asObservable().pipe(map(state => state.message));

  show(message = 'Processando...'): void {
    // Eu incremento este contador para nao esconder o loading enquanto ainda existir outra operacao pendente.
    this.counter += 1;
    this.loadingSubject.next({
      visible: true,
      message
    });
  }

  hide(): void {
    // Eu reduzo o contador para manter o loading visivel ate a ultima requisicao terminar.
    this.counter = Math.max(0, this.counter - 1);
    if (this.counter === 0) {
      this.loadingSubject.next({
        visible: false,
        message: 'Processando...'
      });
      return;
    }

    this.loadingSubject.next({
      visible: true,
      message: this.loadingSubject.value.message
    });
  }

  reset(): void {
    // Eu uso reset quando preciso limpar qualquer estado visual remanescente de uma operacao anterior.
    this.counter = 0;
    this.loadingSubject.next({
      visible: false,
      message: 'Processando...'
    });
  }
}
