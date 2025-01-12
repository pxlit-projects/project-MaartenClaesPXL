import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddReviewComponent } from './add-review.component';
import { ReviewService } from '../../../services/review.service';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AddReviewComponent', () => {
  let component: AddReviewComponent;
  let fixture: ComponentFixture<AddReviewComponent>;
  let reviewServiceSpy: jasmine.SpyObj<ReviewService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddReviewComponent, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        FormBuilder,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AddReviewComponent);
    component = fixture.componentInstance;
    reviewServiceSpy = TestBed.inject(
      ReviewService
    ) as jasmine.SpyObj<ReviewService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with default values', () => {
    expect(component.reviewForm.value).toEqual({
      isApproved: false,
      description: '',
      reviewer: localStorage.getItem('username'),
    });
  });
});
