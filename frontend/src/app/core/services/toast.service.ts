import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastMessage {
  message: string;
  type: ToastType;
}

@Injectable()
export class ToastService {
  private readonly toastSubject = new BehaviorSubject<ToastMessage | null>(null);
  private timeoutId: number | null = null;

  readonly toast$: Observable<ToastMessage | null> = this.toastSubject.asObservable();

  show(message: string, type: ToastType = 'success'): void {
    this.toastSubject.next({ message, type });
    if (this.timeoutId !== null) {
      window.clearTimeout(this.timeoutId);
    }
    this.timeoutId = window.setTimeout(() => this.clear(), 3500);
  }

  clear(): void {
    this.toastSubject.next(null);
    this.timeoutId = null;
  }
}
