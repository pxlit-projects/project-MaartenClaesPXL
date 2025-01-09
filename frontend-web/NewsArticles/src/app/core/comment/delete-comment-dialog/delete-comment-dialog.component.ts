import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-delete-comment-dialog',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './delete-comment-dialog.component.html',
  styleUrl: './delete-comment-dialog.component.css'
})
export class DeleteCommentDialogComponent {
  constructor(private dialogRef: MatDialogRef<DeleteCommentDialogComponent>) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
