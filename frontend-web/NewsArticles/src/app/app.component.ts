import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AddPostComponent } from "./core/post/add-post/add-post.component";
import { LoginComponent } from "./login/login.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AddPostComponent, LoginComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'NewsArticles';
}
