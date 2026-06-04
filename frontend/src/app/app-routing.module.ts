import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard.component';
import { AdminLoginComponent } from './pages/admin/login/admin-login.component';
import { RulesComponent } from './pages/rules/rules.component';
import { SpecialtiesComponent } from './pages/specialties/specialties.component';
import { TriageComponent } from './pages/triage/triage.component';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'triage' },
  { path: 'triage', component: TriageComponent },
  { path: 'admin/login', component: AdminLoginComponent },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [AuthGuard] },
  { path: 'specialties', component: SpecialtiesComponent, canActivate: [AuthGuard] },
  { path: 'rules', component: RulesComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'triage' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
