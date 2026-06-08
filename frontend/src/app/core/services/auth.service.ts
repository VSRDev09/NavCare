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
  // Aqui eu guardo a sessao no proprio frontend para que o guard e o interceptor
  // consigam reaproveitar o estado sem depender de backend extra.
  private readonly sessionSubject = new BehaviorSubject<AuthSession | null>(this.loadSession());
  readonly session$ = this.sessionSubject.asObservable();

  private readonly apiUrl = `${environment.apiUrl}/auth`;

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    // Aqui eu salvo a sessao somente depois que o backend confirmou as credenciais.
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
    // Aqui eu limpo a sessao local para garantir que o token desapareca do navegador
    // assim que o admin sair.
    localStorage.removeItem(AUTH_STORAGE_KEY);
    this.sessionSubject.next(null);
  }

  hasValidToken(): boolean {
    // Eu valido a expiração no proprio frontend para nao manter uma sessao morta em memoria.
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
    // Eu persisto a sessao no browser para que o reload da pagina nao derrube o acesso do admin.
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
    this.sessionSubject.next(session);
  }

  private loadSession(): AuthSession | null {
    // Eu tento restaurar a sessao ao iniciar a aplicacao para manter a experiencia
    // de admin continua quando o token ainda estiver valido.
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
