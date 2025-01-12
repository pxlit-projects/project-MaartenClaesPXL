import {
  HttpClientTestingModule,
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { Post } from '../models/post.model';
import { environment } from '../../environments/environment';
import { PostService } from './post.service';
import { TestBed } from '@angular/core/testing';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostService, provideHttpClientTesting()],
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a post', () => {
    const newPost: Post = {
      id: 1,
      title: 'Test Post',
      content: 'Test Content',
      postDate: new Date(),
      author: 'test author',
      authorEmail: 'test@author.com',
      status: 'UNREVIEWED',
      reviews: [],
      comments: []
    };

    service.addPost(newPost).subscribe((post) => {
      expect(post).toEqual(newPost);
    });

    const req = httpMock.expectOne(`${apiUrl}/post/api/posts`);
    expect(req.request.method).toBe('POST');
    req.flush(newPost);
  });

  it('should update a post', () => {
    const updatedPost: Post = {
      id: 1,
      title: 'Test Post',
      content: 'Test Content',
      postDate: new Date(),
      author: 'test author',
      authorEmail: 'test@author.com',
      status: 'UNREVIEWED',
      reviews: [],
      comments: []
    };

    service.updatePost(updatedPost, 1).subscribe((post) => {
      expect(post).toEqual(updatedPost);
    });

    const req = httpMock.expectOne(`${apiUrl}/post/api/posts/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(updatedPost);
  });

  it('should get all posts', () => {
    const posts: Post[] = [
      {
        id: 1,
        title: 'Test Post 1',
        content: 'Test Content',
        postDate: new Date(),
        author: 'test author',
        authorEmail: 'test@author.com',
        status: 'UNREVIEWED',
        reviews: [],
        comments: []
      },
      {
        id: 2,
        title: 'Test Post 2',
        content: 'Test Content',
        postDate: new Date(),
        author: 'test author',
        authorEmail: 'test@author.com',
        status: 'UNREVIEWED',
        reviews: [],
        comments: []
      }
    ];

    service.getAllPosts().subscribe((data) => {
      expect(data.length).toBe(2);
      expect(data).toEqual(posts);
    });

    const req = httpMock.expectOne(`${apiUrl}/post/api/posts`);
    expect(req.request.method).toBe('GET');
    req.flush(posts);
  });

  it('should get a single post', () => {
    const post: Post = {
      id: 1,
      title: 'Test Post',
      content: 'Test Content',
      postDate: new Date(),
      author: 'test author',
      authorEmail: 'test@author.com',
      status: 'UNREVIEWED',
      reviews: [],
      comments: []
    };

    service.getPost(1).subscribe((data) => {
      expect(data).toEqual(post);
    });

    const req = httpMock.expectOne(`${apiUrl}/post/api/posts/1`);
    expect(req.request.method).toBe('GET');
    req.flush(post);
  });

  it('should publish a post', () => {
    const post: Post = {
      id: 1,
      title: 'Test Post',
      content: 'Test Content',
      postDate: new Date(),
      author: 'test author',
      authorEmail: 'test@author.com',
      status: 'UNREVIEWED',
      reviews: [],
      comments: []
    };

    service.publishPost(1).subscribe((data) => {
      expect(data).toEqual(post);
    });

    const req = httpMock.expectOne(`${apiUrl}/post/api/posts/1/publish`);
    expect(req.request.method).toBe('PUT');
    req.flush(post);
  });

  it('should handle error', () => {
    const errorMessage = 'Something bad happened; please try again later.';

    service.getPost(1).subscribe(
      () => fail('expected an error, not posts'),
      (error) => expect(error).toBe(errorMessage)
    );

    const req = httpMock.expectOne(`${apiUrl}/post/api/posts/1`);
    req.flush('error', { status: 500, statusText: 'Server Error' });
  });
});
