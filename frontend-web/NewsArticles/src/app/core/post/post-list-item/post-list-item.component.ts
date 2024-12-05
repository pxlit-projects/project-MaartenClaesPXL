import { Component, inject, Input } from '@angular/core';
import { Post } from '../../../models/post.model';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-post-list-item',
  standalone: true,
  imports: [MatCardModule],
  templateUrl: './post-list-item.component.html',
  styleUrl: './post-list-item.component.css'
})

export class PostListItemComponent {
  @Input() post!: Post;
}