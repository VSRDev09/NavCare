import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoadingComponent } from './core/components/loading/loading.component';
import { NavbarComponent } from './core/components/navbar/navbar.component';
import { ToastComponent } from './core/components/toast/toast.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { LoadingService } from './core/services/loading.service';
import { ToastService } from './core/services/toast.service';
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard.component';
import { AdminLoginComponent } from './pages/admin/login/admin-login.component';
import { RulesComponent } from './pages/rules/rules.component';
import { SpecialtiesComponent } from './pages/specialties/specialties.component';
import { TriageComponent } from './pages/triage/triage.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoadingComponent,
    ToastComponent,
    TriageComponent,
    AdminLoginComponent,
    AdminDashboardComponent,
    SpecialtiesComponent,
    RulesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    LoadingService,
    ToastService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
