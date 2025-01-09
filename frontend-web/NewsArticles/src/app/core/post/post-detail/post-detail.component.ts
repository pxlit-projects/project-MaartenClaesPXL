import { Component, inject } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { Post } from '../../../models/post.model';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { AddReviewComponent } from '../../review/add-review/add-review.component';
import { ReviewListComponent } from '../../review/review-list/review-list.component';
import { AddCommentComponent } from "../../comment/add-comment/add-comment.component";
import { CommentListComponent } from "../../comment/comment-list/comment-list.component";
import { Comment } from '../../../models/comment.model';
import { Subject } from 'rxjs';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [MatDividerModule, MatCardModule, MatButtonModule, AddReviewComponent, ReviewListComponent, AddCommentComponent, CommentListComponent, MatIcon],
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
  commentsUpdated = new Subject<void>();

  ngOnInit() {
    
    this.postService.getPost(this.id).subscribe({
      next: (post: Post) => {
        this.post = post;
      }
    })
  };

  commentAdded($event: Comment) {
    this.post.comments.push($event);
  }

  onPublish(post: Post) {
    this.postService.publishPost(post.id).subscribe({
      next: () => {
        alert('Post published');
        this.post.status = 'PUBLISHED';
      }
    });
  }
}

