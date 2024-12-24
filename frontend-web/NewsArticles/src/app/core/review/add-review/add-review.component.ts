import { Component, inject, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReviewService } from '../../../services/review.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Review } from '../../../models/review.model';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common'; // Import CommonModule


@Component({
  selector: 'app-add-review',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './add-review.component.html',
  styleUrl: './add-review.component.css',
})
export class AddReviewComponent {
  @Input() postId!: number;

  reviewService: ReviewService = inject(ReviewService);
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);

  reviewForm: FormGroup = this.fb.group({
    isApproved: [false],
    description: ['', Validators.required],
    reviewer: [localStorage.getItem('username'), Validators.required],
  });

  async onSubmit() {
    const newReview: Review = {
      ...this.reviewForm.value,
    };

    await this.reviewService.addReview(newReview, this.postId).subscribe(() => {
      this.reviewForm.reset({
        reviewer: localStorage.getItem('username'),
      });
    });

    location.reload();
  }
}
