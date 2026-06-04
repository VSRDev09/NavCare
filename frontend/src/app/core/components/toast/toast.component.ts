import { Component } from '@angular/core';
import { ToastMessage, ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent {
  readonly toast$ = this.toastService.toast$;

  constructor(private readonly toastService: ToastService) {}

  close(): void {
    this.toastService.clear();
  }

  getClassName(toast: ToastMessage | null): string {
    if (!toast) {
      return '';
    }

    return toast.type === 'success' ? 'toast-success' : toast.type === 'error' ? 'toast-error' : 'toast-info';
  }
}
