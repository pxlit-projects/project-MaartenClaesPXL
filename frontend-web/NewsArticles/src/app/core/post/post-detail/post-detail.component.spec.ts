import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostDetailComponent } from './post-detail.component';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { PostService } from '../../../services/post.service';
import { ActivatedRoute } from '@angular/router';
import { Post } from '../../../models/post.model';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    postServiceMock = {
      getPost: jasmine.createSpy('getPost').and.returnValue(of({
        id: 1,
        title: 'Test Post',
        content: 'Test Content',
        author: 'Test Author',
        status: 'DRAFT',
        comments: [],
      })),
    };

    routerMock = {
      navigate: jasmine.createSpy('navigate'),
    };

    await TestBed.configureTestingModule({
      imports: [
        PostDetailComponent,
        HttpClientTestingModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: { snapshot: { params: { id: 1 } } } },
      ],
    }).compileComponents();
    
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch post on init', () => {
    component.ngOnInit();
    expect(postServiceMock.getPost).toHaveBeenCalledWith(1);
    expect(component.post.id).toEqual(1);
    expect(component.post.title).toEqual('Test Post');
    expect(component.post.content).toEqual('Test Content');
    expect(component.post.author).toEqual('Test Author');
    expect(component.post.status).toEqual('DRAFT');
    expect(component.post.comments).toEqual([]);
  });
});
