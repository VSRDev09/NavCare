import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { LoadingService } from '../../core/services/loading.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../shared/utils/error-message';
import { Specialty } from '../../models/specialty.model';
import { SpecialtyService } from '../../services/specialty.service';

@Component({
  selector: 'app-specialties',
  templateUrl: './specialties.component.html',
  styleUrls: ['./specialties.component.css']
})
export class SpecialtiesComponent implements OnInit {
  form!: FormGroup;
  specialties: Specialty[] = [];
  editingId: number | null = null;
  submitted = false;

  readonly loading$ = this.loadingService.loading$;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly specialtyService: SpecialtyService,
    private readonly loadingService: LoadingService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(120)]],
      description: ['', [Validators.required, Validators.maxLength(500)]]
    });
    this.loadSpecialties();
  }

  loadSpecialties(): void {
    this.specialtyService.findAll().subscribe({
      next: specialties => {
        this.specialties = specialties;
      },
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
      name: this.form.value.name.trim(),
      description: this.form.value.description.trim()
    };

    this.loadingService.show(this.editingId ? 'Atualizando especialidade...' : 'Cadastrando especialidade...');
    const request$ = this.editingId
      ? this.specialtyService.update(this.editingId, payload)
      : this.specialtyService.create(payload);

    request$.pipe(finalize(() => this.loadingService.hide())).subscribe({
      next: () => {
        this.toastService.show(this.editingId ? 'Especialidade atualizada com sucesso.' : 'Especialidade criada com sucesso.', 'success');
        this.resetForm();
        this.loadSpecialties();
      },
      error: error => this.toastService.show(extractErrorMessage(error), 'error')
    });
  }

  edit(specialty: Specialty): void {
    this.editingId = specialty.id;
    this.form.patchValue({
      name: specialty.name,
      description: specialty.description
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  remove(specialty: Specialty): void {
    const confirmed = window.confirm(`Deseja excluir a especialidade ${specialty.name}?`);
    if (!confirmed) {
      return;
    }

    this.loadingService.show('Excluindo especialidade...');
    this.specialtyService.delete(specialty.id).pipe(finalize(() => this.loadingService.hide())).subscribe({
      next: () => {
        this.toastService.show('Especialidade excluída com sucesso.', 'success');
        this.loadSpecialties();
        if (this.editingId === specialty.id) {
          this.resetForm();
        }
      },
      error: error => this.toastService.show(extractErrorMessage(error), 'error')
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.submitted = false;
    this.form.reset();
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || this.submitted);
  }
}
