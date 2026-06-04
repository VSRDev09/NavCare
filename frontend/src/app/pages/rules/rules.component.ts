import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { LoadingService } from '../../core/services/loading.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../shared/utils/error-message';
import { AttendanceRule } from '../../models/attendance-rule.model';
import { Specialty } from '../../models/specialty.model';
import { AttendanceRuleService } from '../../services/attendance-rule.service';
import { SpecialtyService } from '../../services/specialty.service';

@Component({
  selector: 'app-rules',
  templateUrl: './rules.component.html',
  styleUrls: ['./rules.component.css']
})
export class RulesComponent implements OnInit {
  form!: FormGroup;
  rules: AttendanceRule[] = [];
  specialties: Specialty[] = [];
  editingId: number | null = null;
  submitted = false;

  readonly loading$ = this.loadingService.loading$;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly attendanceRuleService: AttendanceRuleService,
    private readonly specialtyService: SpecialtyService,
    private readonly loadingService: LoadingService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      specialtyId: [null, [Validators.required]],
      averageWaitTime: [30, [Validators.required, Validators.min(1)]],
      acceptsEmergency: [true, [Validators.required]],
      notes: ['', [Validators.required, Validators.maxLength(500)]]
    });

    this.loadSpecialties();
    this.loadRules();
  }

  loadSpecialties(): void {
    this.specialtyService.findAll().subscribe({
      next: specialties => {
        this.specialties = specialties;
        if (!this.form.get('specialtyId')?.value && specialties.length > 0) {
          this.form.patchValue({ specialtyId: specialties[0].id });
        }
      },
      error: error => this.toastService.show(extractErrorMessage(error), 'error')
    });
  }

  loadRules(): void {
    this.attendanceRuleService.findAll().subscribe({
      next: rules => this.rules = rules,
      error: error => this.toastService.show(extractErrorMessage(error), 'error')
    });
  }

  submit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      specialtyId: Number(this.form.value.specialtyId),
      averageWaitTime: Number(this.form.value.averageWaitTime),
      acceptsEmergency: Boolean(this.form.value.acceptsEmergency),
      notes: this.form.value.notes.trim()
    };

    this.loadingService.show(this.editingId ? 'Atualizando regra...' : 'Cadastrando regra...');
    const request$ = this.editingId
      ? this.attendanceRuleService.update(this.editingId, payload)
      : this.attendanceRuleService.create(payload);

    request$.pipe(finalize(() => this.loadingService.hide())).subscribe({
      next: () => {
        this.toastService.show(this.editingId ? 'Regra atualizada com sucesso.' : 'Regra criada com sucesso.', 'success');
        this.resetForm();
        this.loadRules();
      },
      error: error => this.toastService.show(extractErrorMessage(error), 'error')
    });
  }

  edit(rule: AttendanceRule): void {
    this.editingId = rule.id;
    const specialty = this.specialties.find(item => item.name === rule.specialtyName);
    this.form.patchValue({
      specialtyId: specialty?.id ?? null,
      averageWaitTime: rule.averageWaitTime,
      acceptsEmergency: rule.acceptsEmergency,
      notes: rule.notes
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  remove(rule: AttendanceRule): void {
    const confirmed = window.confirm(`Deseja excluir a regra da especialidade ${rule.specialtyName}?`);
    if (!confirmed) {
      return;
    }

    this.loadingService.show('Excluindo regra...');
    this.attendanceRuleService.delete(rule.id).pipe(finalize(() => this.loadingService.hide())).subscribe({
      next: () => {
        this.toastService.show('Regra excluída com sucesso.', 'success');
        this.loadRules();
        if (this.editingId === rule.id) {
          this.resetForm();
        }
      },
      error: error => this.toastService.show(extractErrorMessage(error), 'error')
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.submitted = false;
    this.form.reset({
      specialtyId: this.specialties.length > 0 ? this.specialties[0].id : null,
      averageWaitTime: 30,
      acceptsEmergency: true,
      notes: ''
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || this.submitted);
  }
}
