import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Specialty, SpecialtyRequest } from '../models/specialty.model';

@Injectable({
  providedIn: 'root'
})
export class SpecialtyService {
  private readonly apiUrl = `${environment.apiUrl}/specialties`;

  constructor(private readonly http: HttpClient) {}

  findAll(): Observable<Specialty[]> {
    return this.http.get<Specialty[]>(this.apiUrl);
  }

  findById(id: number): Observable<Specialty> {
    return this.http.get<Specialty>(`${this.apiUrl}/${id}`);
  }

  create(payload: SpecialtyRequest): Observable<Specialty> {
    return this.http.post<Specialty>(this.apiUrl, payload);
  }

  update(id: number, payload: SpecialtyRequest): Observable<Specialty> {
    return this.http.put<Specialty>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
