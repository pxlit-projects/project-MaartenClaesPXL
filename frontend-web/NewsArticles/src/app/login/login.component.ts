import {Component, inject} from '@angular/core';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatCardModule, MatSelectModule, MatButtonModule],  
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);
  
  onSubmit(loginForm: any) {
    this.authService.login(loginForm.value);
    this.router.navigate(['/posts-list']);
  }

  model: User = {} as User;
  username: any;
  email: any;
  role: any;
}
