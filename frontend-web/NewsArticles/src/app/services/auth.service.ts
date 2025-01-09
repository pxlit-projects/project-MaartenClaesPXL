import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  login(value: any) {
    localStorage.setItem('username', value.username);
    localStorage.setItem('role', value.role);
    localStorage.setItem('email', value.email);
  }

  constructor() { }
}
