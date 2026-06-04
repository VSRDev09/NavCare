import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  constructor(
    public readonly authService: AuthService,
    private readonly router: Router
  ) {}

  navigateToAdministration(): void {
    const targetRoute = this.authService.hasValidToken() ? '/admin' : '/admin/login';
    this.router.navigate([targetRoute]);
  }
}
