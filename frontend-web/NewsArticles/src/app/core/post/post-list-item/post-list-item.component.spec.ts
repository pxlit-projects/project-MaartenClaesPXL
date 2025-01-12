import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostListItemComponent } from './post-list-item.component';
import { Post } from '../../../models/post.model';
import { By } from '@angular/platform-browser';

describe('PostListItemComponent', () => {
  let component: PostListItemComponent;
  let fixture: ComponentFixture<PostListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostListItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostListItemComponent);
    component = fixture.componentInstance;
    component.post = {
      id: 1,
      title: 'Test Post',
      content: 'Test Content',
      postDate: new Date(),
      author: 'Test Author',
      authorEmail: 'author@test.com',
      status: 'UNREVIEWED',
      reviews: [],
      comments: []
    }
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display post title', () => {
    const titleElement = fixture.debugElement.query(By.css('mat-card-title')).nativeElement;
    expect(titleElement.textContent).toContain(component.post.title);
  });
});
