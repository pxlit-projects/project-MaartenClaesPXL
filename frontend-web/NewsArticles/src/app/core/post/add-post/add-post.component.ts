import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators } from "@angular/forms";
import { Post } from '../../../models/post.model';
import { PostService } from '../../../services/post.service';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.css'
})
export class AddPostComponent {
  id: number = 0;
  postService: PostService = inject(PostService);
  fb: FormBuilder = inject(FormBuilder);
  route: ActivatedRoute = inject(ActivatedRoute);
  router: Router = inject(Router);
  postForm: FormGroup = this.fb.group({
    title: ['', Validators.required],
    content: ['', [Validators.required,]],
    author: [localStorage.getItem("username"),[Validators.required]],
    isConcept: [false]
  });

  setIsConcept(value: boolean) {
    this.postForm.patchValue({ isConcept: value });
  }

  onSubmit() {
    const newPost : Post = {
      ...this.postForm.value
    }
    if (this.id != 0) {
      this.postService.updatePost(newPost, this.id).subscribe(post => {
        this.postForm.reset();
        this.router.navigate(['/posts-list']);
      });
    } else {
      this.postService.addPost(newPost).subscribe(post => {
        this.postForm.reset();
        this.router.navigate(['/posts-list']);
      });
    }
  }

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.postForm.patchValue({
        title: params['title'] || '',
        content: params['content'] || '',
        author: params['author'] || localStorage.getItem("username"),
        isConcept: params['isConcept'] || false,
      });
      this.id = params['id'] || 0;
    });
  }

}
