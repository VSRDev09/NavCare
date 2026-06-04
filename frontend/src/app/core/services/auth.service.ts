import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthSession, LoginRequest, LoginResponse } from '../../models/auth.model';

const AUTH_STORAGE_KEY = 'navcare-auth-session';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly sessionSubject = new BehaviorSubject<AuthSession | null>(this.loadSession());
  readonly session$ = this.sessionSubject.asObservable();

  private readonly apiUrl = `${environment.apiUrl}/auth`;

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        const expiresAt = Date.now() + response.expiresInSeconds * 1000;
        const session: AuthSession = {
          token: response.token,
          username: response.username,
          expiresAt
        };
        this.saveSession(session);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    this.sessionSubject.next(null);
  }

  hasValidToken(): boolean {
    const session = this.sessionSubject.value;
    if (!session) {
      return false;
    }
    if (Date.now() >= session.expiresAt) {
      this.logout();
      return false;
    }
    return true;
  }

  getToken(): string | null {
    return this.hasValidToken() ? this.sessionSubject.value?.token ?? null : null;
  }

  getUsername(): string | null {
    return this.hasValidToken() ? this.sessionSubject.value?.username ?? null : null;
  }

  isAuthenticated$(): Observable<boolean> {
    return this.session$.pipe(map(() => this.hasValidToken()));
  }

  private saveSession(session: AuthSession): void {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
    this.sessionSubject.next(session);
  }

  private loadSession(): AuthSession | null {
    const rawSession = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!rawSession) {
      return null;
    }

    try {
      const session = JSON.parse(rawSession) as AuthSession;
      if (!session.token || !session.username || !session.expiresAt) {
        return null;
      }
      if (Date.now() >= session.expiresAt) {
        localStorage.removeItem(AUTH_STORAGE_KEY);
        return null;
      }
      return session;
    } catch {
      localStorage.removeItem(AUTH_STORAGE_KEY);
      return null;
    }
  }
}
