import { Component, Input, input, OnChanges, SimpleChanges } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { Comment } from '../../../models/comment.model';
import { CommentListItemComponent } from "../comment-list-item/comment-list-item.component";

@Component({
  selector: 'app-comment-list',
  standalone: true,
  imports: [MatCardModule, MatListModule, MatIconModule, CommentListItemComponent],
  templateUrl: './comment-list.component.html',
  styleUrl: './comment-list.component.css'
})
export class CommentListComponent {
  constructor() { }
  @Input() comments!: Comment[];

  commentDeleted($event: number) {
    this.comments = this.comments.filter(c => c.id !== $event);
  }
}
