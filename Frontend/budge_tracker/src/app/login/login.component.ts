import { Component, OnInit } from '@angular/core';
import { ModalService } from '../modal.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  isVisible = false;
  isRegistering = false;

  email = '';
  password = '';

  constructor(
    private modalService: ModalService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.modalService.showLoginModal$.subscribe((visible) => {
      this.isVisible = visible;
    });
  }

  toggleMode() {
    this.isRegistering = !this.isRegistering;
  }

  close() {
    this.modalService.closeLoginModal();
    this.isRegistering = false;
    this.email = '';
    this.password = '';
    window.location.reload();
  }

  onSubmit() {
    if (this.isRegistering) {
      this.authService.register(this.email, this.password).subscribe({
        next: (res) => {
          console.log('Registrierung erfolgreich:', res);
          this.close();
        },
        error: (err) => {
          console.error('Registrierung fehlgeschlagen:', err);
        },
      });
    } else {
      this.authService.login(this.email, this.password).subscribe({
        next: (res) => {
          localStorage.setItem('token', res.token);

          this.close();
        },
        error: (err) => {
          console.error('Login fehlgeschlagen:', err);
        },
      });
    }
  }
}
