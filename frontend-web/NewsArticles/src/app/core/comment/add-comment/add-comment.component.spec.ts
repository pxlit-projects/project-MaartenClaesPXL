import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCommentComponent } from './add-comment.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AddCommentComponent', () => {
  let component: AddCommentComponent;
  let fixture: ComponentFixture<AddCommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddCommentComponent, HttpClientTestingModule, BrowserAnimationsModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
