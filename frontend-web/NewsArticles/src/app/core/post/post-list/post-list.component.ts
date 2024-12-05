import { Component, inject } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { Post } from '../../../models/post.model';
import { PostListItemComponent } from "../post-list-item/post-list-item.component";
import { MatCard } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatNativeDateModule, MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [PostListItemComponent, MatCard, MatButtonModule, MatDatepickerModule, MatFormFieldModule, MatInputModule, FormsModule, MatNativeDateModule],
  providers : [
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' }, // Use British locale for DD/MM/YYYY
    { provide: MAT_DATE_FORMATS, useValue: {
      parse: {
        dateInput: 'DD/MM/YYYY',
      },
      display: {
        dateInput: 'DD/MM/YYYY',
        monthYearLabel: 'MMM YYYY',
        dateA11yLabel: 'LL',
        monthYearA11yLabel: 'MMMM YYYY',
      },
    } },
  ],
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css'
})
export class PostListComponent {
  postService: PostService = inject(PostService);
  router: Router = inject(Router);
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  filters = {
    content: '',
    author: '',
    date: ''
  };
  
  addPost() {
      this.router.navigate(['/add-post']);
  }
  
  viewPost(postId: number) {
    this.router.navigate(['/post', postId]);
  }

  ngOnInit() {
    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.filteredPosts = posts;
        console.log(this.posts);
      }
    })
  }

  filterPosts() {
    this.filteredPosts = this.posts.filter(post => {
      const matchesContent = post.content.toLowerCase().includes(this.filters.content.toLowerCase());
      const matchesAuthor = post.author.toLowerCase().includes(this.filters.author.toLowerCase());
  
      // Date filtering
      let matchesDate = true; // Default to true if no filter date is set
      if (this.filters.date) {
        const filterDate = new Date(this.filters.date); // Parse the selected filter date
        const postDate = new Date(post.postDate); // Parse the post's date
  
        // Compare year, month, and day explicitly to handle time differences
        matchesDate =
          filterDate.getDate() === postDate.getDate() &&
          filterDate.getMonth() === postDate.getMonth() &&
          filterDate.getFullYear() === postDate.getFullYear();
      }
  
      return matchesContent && matchesAuthor && matchesDate;
    });
  }
  
}

