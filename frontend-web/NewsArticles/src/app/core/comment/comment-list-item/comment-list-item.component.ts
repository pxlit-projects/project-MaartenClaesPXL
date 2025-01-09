import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Comment } from '../../../models/comment.model';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { DeleteCommentDialogComponent } from '../delete-comment-dialog/delete-comment-dialog.component';
import { FormsModule } from '@angular/forms';
import { CommentService } from '../../../services/comment.service';


@Component({
  selector: 'app-comment-list-item',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, FormsModule],
  templateUrl: './comment-list-item.component.html',
  styleUrl: './comment-list-item.component.css'
})
export class CommentListItemComponent {
  constructor(public dialog: MatDialog) { }
  @Input() comment!: Comment;
  @Output() deleteComment = new EventEmitter<number>();
  editComment: string = '';
  commentService: CommentService = inject(CommentService);
  loggedInUser: string = localStorage.getItem('username') || '';
  isEditing: boolean = false;

  ngOnInit() {
    this.editComment = this.comment.text
  }

  openDeleteConfirmation(commentId: number): void {
    const dialogRef = this.dialog.open(DeleteCommentDialogComponent, {
      data: { id: commentId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.onDelete(commentId);  // Proceed with deletion if confirmed
      }
    });
  }

  toggleEditMode() {
    if (this.isEditing) {
      this.editComment = this.comment.text;
    } 
    this.isEditing = !this.isEditing;
  }

  saveEdit(comment: any) {
    // Logic to save the edited comment, e.g., API call
    console.log('Comment saved:', comment);
    this.isEditing = false;
    this.commentService.updateComment(this.editComment, this.comment.id).subscribe(
      (comment: Comment) => {
        if (!comment) {
          
        this.editComment = this.comment.text;
          return;
        }
        this.comment = comment;
        console.log('Comment updated:', comment);
      }
    );
  }

  onDelete(commentId: number): void {
    console.log(`Deleting comment with id ${commentId}`);
    this.commentService.deleteComment(commentId).subscribe(
      () => {
        this.deleteComment.emit(commentId);
      }
    );
  }
}
