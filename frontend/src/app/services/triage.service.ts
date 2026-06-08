import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TriageRequest, TriageResponse } from '../models/triage.model';

@Injectable({
  providedIn: 'root'
})
export class TriageService {
  // Aqui eu concentro a chamada da triagem para que a tela nao precise conhecer a URL do backend.
  private readonly apiUrl = `${environment.apiUrl}/triage`;

  constructor(private readonly http: HttpClient) {}

  analyze(payload: TriageRequest): Observable<TriageResponse> {
    // Aqui eu mantenho a chamada de triagem isolada para que a tela fique simples de ler.
    return this.http.post<TriageResponse>(this.apiUrl, payload);
  }
}
