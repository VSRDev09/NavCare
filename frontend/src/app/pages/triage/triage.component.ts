import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { LoadingService } from '../../core/services/loading.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../shared/utils/error-message';
import { TriageResponse } from '../../models/triage.model';
import { TriageService } from '../../services/triage.service';

@Component({
  selector: 'app-triage',
  templateUrl: './triage.component.html',
  styleUrls: ['./triage.component.css']
})
export class TriageComponent implements OnInit {
  form!: FormGroup;
  result: TriageResponse | null = null;
  submitted = false;

  readonly loading$ = this.loadingService.loading$;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly triageService: TriageService,
    private readonly loadingService: LoadingService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    // Eu exijo um minimo de texto porque um relato muito curto gera pouca base
    // para a IA e costuma produzir orientacao fraca.
    this.form = this.formBuilder.group({
      report: ['', [Validators.required, Validators.minLength(15), Validators.maxLength(4000)]]
    });
  }

  submit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // Eu mostro este loading porque a triagem depende de uma chamada externa e o usuario
    // precisa perceber que o sistema esta processando o relato.
    this.loadingService.show('Analisando sintomas...');
    this.triageService.analyze({ report: this.form.value.report }).pipe(
      finalize(() => this.loadingService.hide())
    ).subscribe({
      next: result => {
        this.result = result;
        this.toastService.show('Triagem concluída com sucesso.', 'success');
      },
      error: error => {
        this.toastService.show(extractErrorMessage(error), 'error');
      }
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || this.submitted);
  }

  getUrgencyClass(): string {
    // Eu traduzo a urgencia em classe visual para deixar o resultado rapido de ler.
    const urgency = this.result?.urgency?.toLowerCase();
    if (urgency === 'alta') {
      return 'urgency-high';
    }
    if (urgency === 'média' || urgency === 'media') {
      return 'urgency-medium';
    }
    return 'urgency-low';
  }

  getUrgencyMessage(): string {
    // Eu transformo a prioridade em uma mensagem humana para reforcar a orientacao clinica.
    const urgency = this.result?.urgency?.toLowerCase();
    if (urgency === 'alta') {
      return 'Procure atendimento médico com prioridade.';
    }
    if (urgency === 'média' || urgency === 'media') {
      return 'Recomenda-se procurar atendimento assim que possível.';
    }
    return 'Recomenda-se descanso. Procure atendimento se os sintomas persistirem.';
  }
}
