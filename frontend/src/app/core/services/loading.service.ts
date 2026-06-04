import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map } from 'rxjs';

interface LoadingState {
  visible: boolean;
  message: string;
}

@Injectable()
export class LoadingService {
  private counter = 0;
  private readonly loadingSubject = new BehaviorSubject<LoadingState>({
    visible: false,
    message: 'Processando...'
  });

  readonly loading$: Observable<boolean> = this.loadingSubject.asObservable().pipe(map(state => state.visible));
  readonly message$: Observable<string> = this.loadingSubject.asObservable().pipe(map(state => state.message));

  show(message = 'Processando...'): void {
    this.counter += 1;
    this.loadingSubject.next({
      visible: true,
      message
    });
  }

  hide(): void {
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
    this.counter = 0;
    this.loadingSubject.next({
      visible: false,
      message: 'Processando...'
    });
  }
}
