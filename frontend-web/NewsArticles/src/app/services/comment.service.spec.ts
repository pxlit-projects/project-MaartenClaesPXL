import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CommentService } from './comment.service';
import { Comment } from '../models/comment.model';
import { environment } from '../../environments/environment';



describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CommentService]
    });
    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a comment', () => {
    const newComment: Comment = { id: 1, text: 'Test comment', commenter: "test commenter" };
    const postId = 1;

    service.addComment(newComment, postId).subscribe(comment => {
      expect(comment).toEqual(newComment);
    });

    const req = httpMock.expectOne(`${apiUrl}/comment/api/comments/${postId}/addComment`);
    expect(req.request.method).toBe('POST');
    req.flush(newComment);
  });

  it('should delete a comment', () => {
    const commentId = 1;

    service.deleteComment(commentId).subscribe(comment => {
      expect(comment).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/comment/api/comments/${commentId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should update a comment', () => {
    const updatedComment: Comment = { id: 1, text: 'Test comment', commenter: "test commenter" };
    const commentId = 1;
    const newText = 'Updated comment';

    service.updateComment(newText, commentId).subscribe(comment => {
      expect(comment).toEqual(updatedComment);
    });

    const req = httpMock.expectOne(`${apiUrl}/comment/api/comments/${commentId}`);
    expect(req.request.method).toBe('PUT');
    req.flush(updatedComment);
  });

  it('should handle error', () => {
    const errorMessage = 'Something bad happened; please try again later.';
    const commentId = 1;

    service.deleteComment(commentId).subscribe(
      () => fail('expected an error, not comments'),
      error => expect(error).toBe(errorMessage)
    );

    const req = httpMock.expectOne(`${apiUrl}/comment/api/comments/${commentId}`);
    req.flush('error', { status: 500, statusText: 'Server Error' });
  });
});
