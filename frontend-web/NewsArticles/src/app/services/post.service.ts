import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Post } from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  updatePost(newPost: Post, id: number) {
    return this.http.put('http://localhost:8083/post/api/posts/' + id, newPost);
  }
  
  http: HttpClient = inject(HttpClient);

  addPost(post: Post) {
    return this.http.post('http://localhost:8083/post/api/posts', post);
  }

  getAllPosts() {
    const headers = new HttpHeaders({
      'role': localStorage.getItem('role')|| '', // Example: setting 'admin' role in header
      'Content-Type': 'application/json'
    });
    return this.http.get<Post[]>('http://localhost:8083/post/api/posts', { headers: headers });
  }

  getPost(postId: number): any {
    return this.http.get<Post>('http://localhost:8083/post/api/posts/' + postId);
  }
  
  constructor() { }
}
