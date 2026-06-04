import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AttendanceRule, AttendanceRuleRequest } from '../models/attendance-rule.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceRuleService {
  private readonly apiUrl = `${environment.apiUrl}/attendance-rules`;

  constructor(private readonly http: HttpClient) {}

  findAll(): Observable<AttendanceRule[]> {
    return this.http.get<AttendanceRule[]>(this.apiUrl);
  }

  findById(id: number): Observable<AttendanceRule> {
    return this.http.get<AttendanceRule>(`${this.apiUrl}/${id}`);
  }

  create(payload: AttendanceRuleRequest): Observable<AttendanceRule> {
    return this.http.post<AttendanceRule>(this.apiUrl, payload);
  }

  update(id: number, payload: AttendanceRuleRequest): Observable<AttendanceRule> {
    return this.http.put<AttendanceRule>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
