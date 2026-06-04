import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { LoadingService } from '../../../core/services/loading.service';
import { ToastService } from '../../../core/services/toast.service';
import { extractErrorMessage } from '../../../shared/utils/error-message';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent implements OnInit {
  form!: FormGroup;
  submitted = false;

  readonly loading$ = this.loadingService.loading$;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly loadingService: LoadingService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    if (this.authService.hasValidToken()) {
      this.router.navigate(['/admin']);
      return;
    }

    this.form = this.formBuilder.group({
      username: ['admin', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  submit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loadingService.show('Autenticando...');
    this.authService.login({
      username: this.form.value.username,
      password: this.form.value.password
    }).pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: () => {
          this.toastService.show('Acesso administrativo liberado.', 'success');
          this.router.navigate(['/admin']);
        },
        error: error => this.toastService.show(extractErrorMessage(error), 'error')
      });
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || this.submitted);
  }
}
