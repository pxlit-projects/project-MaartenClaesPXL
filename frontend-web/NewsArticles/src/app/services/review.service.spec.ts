import { TestBed } from '@angular/core/testing';
import { ReviewService } from './review.service';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { Review } from '../models/review.model';
import { environment } from '../../environments/environment';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReviewService],
    });

    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a review', () => {
    const dummyReview: Review = { description: 'Great article!', approved: true, reviewer: 'John Doe' };
    const postId = 123;

    service.addReview(dummyReview, postId).subscribe((review) => {
      expect(review).toEqual(dummyReview);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/review/api/reviews/${postId}/addReview`
    );
    expect(req.request.method).toBe('POST');
    req.flush(dummyReview);
  });

  it('should handle error', () => {
    const dummyReview: Review = { description: 'Great article!', approved: true, reviewer: 'John Doe' };
    const postId = 123;
    const errorMessage = 'Something bad happened; please try again later.';

    service.addReview(dummyReview, postId).subscribe(
      () => fail('expected an error, not a review'),
      (error) => expect(error).toBe(errorMessage)
    );

    const req = httpMock.expectOne(
      `${environment.apiUrl}/review/api/reviews/${postId}/addReview`
    );
    req.flush('error', { status: 500, statusText: 'Server Error' });
  });
});
