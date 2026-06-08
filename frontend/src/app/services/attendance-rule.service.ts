import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AttendanceRule, AttendanceRuleRequest } from '../models/attendance-rule.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceRuleService {
  // Aqui eu concentro o CRUD de regras para manter a tela administrativa simples de revisar.
  private readonly apiUrl = `${environment.apiUrl}/attendance-rules`;

  constructor(private readonly http: HttpClient) {}

  findAll(): Observable<AttendanceRule[]> {
    // Aqui eu consumo as regras que depois aparecem na resposta enriquecida da triagem.
    return this.http.get<AttendanceRule[]>(this.apiUrl);
  }

  findById(id: number): Observable<AttendanceRule> {
    return this.http.get<AttendanceRule>(`${this.apiUrl}/${id}`);
  }

  create(payload: AttendanceRuleRequest): Observable<AttendanceRule> {
    // Aqui eu mantenho a operacao de cadastro isolada para nao duplicar logica no componente.
    return this.http.post<AttendanceRule>(this.apiUrl, payload);
  }

  update(id: number, payload: AttendanceRuleRequest): Observable<AttendanceRule> {
    return this.http.put<AttendanceRule>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
