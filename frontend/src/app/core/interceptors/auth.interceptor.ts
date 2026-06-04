import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (!this.shouldAttachToken(request)) {
      return next.handle(request);
    }

    const token = this.authService.getToken();
    if (!token) {
      return next.handle(request);
    }

    return next.handle(
      request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    );
  }

  private shouldAttachToken(request: HttpRequest<unknown>): boolean {
    const path = this.getPath(request.url);
    const method = request.method.toUpperCase();

    if (path === '/api/auth/login' || path === '/api/triage') {
      return false;
    }

    if (method === 'GET') {
      return false;
    }

    const specialtiesMutation =
      path === '/api/specialties' && method === 'POST' ||
      path.startsWith('/api/specialties/') && (method === 'PUT' || method === 'DELETE');

    const rulesMutation =
      path === '/api/attendance-rules' && method === 'POST' ||
      path.startsWith('/api/attendance-rules/') && (method === 'PUT' || method === 'DELETE');

    return specialtiesMutation || rulesMutation;
  }

  private getPath(url: string): string {
    if (url.startsWith('http://') || url.startsWith('https://')) {
      try {
        return new URL(url).pathname;
      } catch {
        return url;
      }
    }

    return url;
  }
}
