import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TriageRequest, TriageResponse } from '../models/triage.model';

@Injectable({
  providedIn: 'root'
})
export class TriageService {
  private readonly apiUrl = `${environment.apiUrl}/triage`;

  constructor(private readonly http: HttpClient) {}

  analyze(payload: TriageRequest): Observable<TriageResponse> {
    return this.http.post<TriageResponse>(this.apiUrl, payload);
  }
}
