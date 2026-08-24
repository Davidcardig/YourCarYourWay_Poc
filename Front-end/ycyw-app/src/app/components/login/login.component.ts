import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  isLogin = signal(true);
  isLoading = signal(false);
  errorMessage = signal('');

  loginForm = {
    email: '',
    password: ''
  };

  registerForm = {
    email: '',
    password: '',
    nom: '',
    prenom: '',
    role: 'CLIENT' as 'AGENT' | 'CLIENT'
  };

  constructor(private authService: AuthService, private router: Router) {}

  toggleMode() {
    this.isLogin.update(val => !val);
    this.errorMessage.set('');
  }

  login() {
    if (!this.loginForm.email || !this.loginForm.password) {
      this.errorMessage.set('Veuillez remplir tous les champs');
      return;
    }

    this.isLoading.set(true);
    this.authService.login(this.loginForm.email, this.loginForm.password).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.router.navigate(['/chat']);
      },
      error: (error) => {
        this.isLoading.set(false);
        this.errorMessage.set('Email ou mot de passe incorrect');
      }
    });
  }

  register() {
    if (!this.registerForm.email || !this.registerForm.password || 
        !this.registerForm.nom || !this.registerForm.prenom) {
      this.errorMessage.set('Veuillez remplir tous les champs');
      return;
    }

    this.isLoading.set(true);
    this.authService.register(
      this.registerForm.email,
      this.registerForm.password,
      this.registerForm.nom,
      this.registerForm.prenom,
      this.registerForm.role
    ).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.router.navigate(['/chat']);
      },
      error: (error) => {
        this.isLoading.set(false);
        this.errorMessage.set('Erreur lors de l\'inscription');
      }
    });
  }
}
