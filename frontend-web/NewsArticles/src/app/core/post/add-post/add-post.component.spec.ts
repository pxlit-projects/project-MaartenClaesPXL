import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPostComponent } from './add-post.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { PostService } from '../../../services/post.service';
import { ActivatedRoute } from '@angular/router';

describe('AddPostComponent', () => {
  let component: AddPostComponent;
  let fixture: ComponentFixture<AddPostComponent>;
  let postServiceStub: Partial<PostService>;

  beforeEach(async () => {
    postServiceStub = {
      addPost: jasmine.createSpy('addPost').and.returnValue(of({})),
      updatePost: jasmine.createSpy('updatePost').and.returnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [
        AddPostComponent,
        ReactiveFormsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: PostService, useValue: postServiceStub },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              title: 'Test Title',
              content: 'Test Content',
              author: 'Test Author',
              authorEmail: 'test@example.com',
              isConcept: false,
              id: 1,
            }),
          },
        },
      ]
    }).compileComponents();
    

    fixture = TestBed.createComponent(AddPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with query params', () => {
    expect(component.postForm.value).toEqual({
      title: 'Test Title',
      content: 'Test Content',
      author: 'Test Author',
      authorEmail: 'test@example.com',
      isConcept: false,
    });
    expect(component.id).toBe(1);
  });

  it('should call addPost on submit when id is 0', () => {
    component.id = 0;
    component.onSubmit();
    expect(postServiceStub.addPost).toHaveBeenCalled();
  });

  it('should call updatePost on submit when id is not 0', () => {
    component.id = 1;
    component.onSubmit();
    expect(postServiceStub.updatePost).toHaveBeenCalled();
  });

  it('should reset form and navigate after successful submit', () => {
    spyOn(component.postForm, 'reset');
    spyOn(component.router, 'navigate');
    component.onSubmit();
    expect(component.postForm.reset).toHaveBeenCalled();
    expect(component.router.navigate).toHaveBeenCalledWith(['/posts-list']);
  });

  it('should set isConcept value', () => {
    component.setIsConcept(true);
    expect(component.postForm.value.isConcept).toBe(true);
  });
});
