import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Comment } from '../models/comment.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  http: HttpClient = inject(HttpClient);
  api: string = environment.apiUrl;
  
  addComment(newComment: Comment, postId: number) {
    return this.http
      .post<Comment>(`${this.api}/comment/api/comments/${postId}/addComment`, newComment)
      .pipe(catchError(this.handleError));
  }

  deleteComment(commentId: number) {
    return this.http
      .delete<Comment>(`${this.api}/comment/api/comments/` + commentId)
      .pipe(catchError(this.handleError));
      
  }

  updateComment(text: String, commentId: number) {
    return this.http
      .put<Comment>(`${this.api}/comment/api/comments/${commentId}`, {text: text})
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      console.error('An error occurred:', error.error.message);
    } else {
      console.error(
        `Backend returned code ${error.status}, ` + `body was: ${error.error}`
      );
    }
    return throwError('Something bad happened; please try again later.');
  }

  constructor() {}
}
