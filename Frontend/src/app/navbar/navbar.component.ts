import { Component } from '@angular/core';
import { ModalService } from '../modal.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  imports: [CommonModule],
})
export class NavbarComponent {
  get isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  constructor(private modalService: ModalService) {}

  openLoginModal() {
    this.modalService.openLoginModal();
  }

  logout() {
    localStorage.removeItem('token');
    window.location.reload();
    this.openLoginModal();
  }
}
