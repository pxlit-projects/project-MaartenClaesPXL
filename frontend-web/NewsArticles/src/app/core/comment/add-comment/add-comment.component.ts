import { Component, EventEmitter, inject, Input, input, Output } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { Comment } from '../../../models/comment.model';
import { CommentService } from '../../../services/comment.service';

@Component({
  selector: 'app-add-comment',
  standalone: true,
  imports: [MatInputModule, MatButtonModule, MatCardModule, FormsModule],
  templateUrl: './add-comment.component.html',
  styleUrl: './add-comment.component.css'
})
export class AddCommentComponent {
  commentService: CommentService = inject(CommentService);
  model: Comment = { text: '', commenter: '', id: 0 };
  @Input() postId!: number;
  @Output() commentAdded: EventEmitter<Comment> = new EventEmitter<Comment>();

  ngOnInit() {
    this.model.commenter = localStorage.getItem('username') || '';
  }

  onSubmit(form: any) {
    if (form.valid) {
      this.commentService.addComment(this.model, this.postId).subscribe(
        (comment: Comment) => {
          this.commentAdded.emit(comment);
        }
      );
      form.reset();
    }
  }
}
