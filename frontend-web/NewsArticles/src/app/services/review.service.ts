import { inject, Injectable } from '@angular/core';
import { Review } from '../models/review.model';
import { catchError, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  http: HttpClient = inject(HttpClient);
  api: string = environment.apiUrl;
  
  addReview(newReview: Review, postId: number) {
    return this.http
      .post<Review>(`${this.api}/review/api/reviews/${postId}/addReview`, newReview)
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
