import { Component, Input } from '@angular/core';
import { Review } from '../../../models/review.model';
import { MatIconModule } from '@angular/material/icon';


@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './review-list.component.html',
  styleUrl: './review-list.component.css'
})
export class ReviewListComponent {
  constructor() { }
  @Input() reviews!: Review[];
}
