import { Component, inject } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { Post } from '../../../models/post.model';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { AddReviewComponent } from '../../review/add-review/add-review.component';
import { ReviewListComponent } from '../../review/review-list/review-list.component';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [MatDividerModule, MatCardModule, MatButtonModule, AddReviewComponent, ReviewListComponent],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.css'
})
export class PostDetailComponent {
  router: Router = inject(Router);
onEdit(post: Post) {
  this.router.navigate(['/add-post'], {
    queryParams: {
      title: post.title,
      content: post.content,
      author: post.author,
      status: post.status,
      id: post.id
    },
  });
}
  route: ActivatedRoute = inject(ActivatedRoute);
  postService: PostService = inject(PostService);
  id: number = this.route.snapshot.params['id'];
  post!: Post;
  currentUsername: string = localStorage.getItem('username') || '';
  role: string = localStorage.getItem('role') || '';
  ngOnInit() {
    
    this.postService.getPost(this.id).subscribe({
      next: (post: Post) => {
        this.post = post;
      }
    })
  };
}

