import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { Post } from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  updatePost(newPost: Post, id: number) : Observable<Post> {
    return this.http.put<Post>('http://localhost:8083/post/api/posts/' + id, newPost).pipe(
      catchError(this.handleError)
    );
  }
  
  http: HttpClient = inject(HttpClient);

  addPost(post: Post) :Observable<Post> {
    return this.http.post<Post>('http://localhost:8083/post/api/posts', post).pipe(
      catchError(this.handleError)
    );
  }

  getAllPosts() : Observable<Post[]> {
    const headers = new HttpHeaders({
      'role': localStorage.getItem('role')|| '', // Example: setting 'admin' role in header
      'Content-Type': 'application/json'
    });
    return this.http.get<Post[]>('http://localhost:8083/post/api/posts', { headers: headers }).pipe(
      catchError(this.handleError)
    );
  }

  getPost(postId: number): Observable<Post> {
    return this.http.get<Post>('http://localhost:8083/post/api/posts/' + postId).pipe(
      catchError(this.handleError)
    );
  }
  
  constructor() { }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      console.error('An error occurred:', error.error.message);
    } else {
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    return throwError(
      'Something bad happened; please try again later.');
  }
}
