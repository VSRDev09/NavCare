import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Specialty, SpecialtyRequest } from '../models/specialty.model';

@Injectable({
  providedIn: 'root'
})
export class SpecialtyService {
  // Aqui eu mantenho o CRUD de especialidades isolado para nao espalhar URLs pelo app.
  private readonly apiUrl = `${environment.apiUrl}/specialties`;

  constructor(private readonly http: HttpClient) {}

  findAll(): Observable<Specialty[]> {
    // Aqui eu consumo a listagem publica para alimentar tanto a area administrativa quanto a triagem.
    return this.http.get<Specialty[]>(this.apiUrl);
  }

  findById(id: number): Observable<Specialty> {
    return this.http.get<Specialty>(`${this.apiUrl}/${id}`);
  }

  create(payload: SpecialtyRequest): Observable<Specialty> {
    // Aqui eu preservo o CRUD administrativo sem espalhar URLs pelo componente.
    return this.http.post<Specialty>(this.apiUrl, payload);
  }

  update(id: number, payload: SpecialtyRequest): Observable<Specialty> {
    return this.http.put<Specialty>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
