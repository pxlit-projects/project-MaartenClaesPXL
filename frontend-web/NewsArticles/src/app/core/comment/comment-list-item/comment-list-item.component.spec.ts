import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentListItemComponent } from './comment-list-item.component';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { CommentService } from '../../../services/comment.service';
import { Comment } from '../../../models/comment.model';

describe('CommentListItemComponent', () => {
  let component: CommentListItemComponent;
  let fixture: ComponentFixture<CommentListItemComponent>;
  let commentServiceStub: Partial<CommentService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    commentServiceStub = {
      updateComment: jasmine
        .createSpy('updateComment')
        .and.returnValue(of({ id: 1, text: 'Updated comment' })),
      deleteComment: jasmine.createSpy('deleteComment').and.returnValue(of({})),
    };

    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [CommentListItemComponent, MatDialogModule],
      providers: [
        { provide: CommentService, useValue: commentServiceStub },
        { provide: MatDialog, useValue: dialogSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentListItemComponent);
    component = fixture.componentInstance;
    component.comment = { id: 1, text: 'Test comment' } as Comment;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize editComment with comment text on ngOnInit', () => {
    component.ngOnInit();
    expect(component.editComment).toBe('Test comment');
  });

  it('should toggle edit mode', () => {
    component.toggleEditMode();
    expect(component.isEditing).toBeTrue();
    component.toggleEditMode();
    expect(component.isEditing).toBeFalse();
  });

  it('should save edited comment', () => {
    component.editComment = 'Updated comment';
    component.saveEdit(component.comment);
    expect(commentServiceStub.updateComment).toHaveBeenCalledWith(
      'Updated comment',
      1
    );
    expect(component.isEditing).toBeFalse();
  });

  it('should open delete confirmation dialog', () => {
    dialogSpy.open.and.returnValue({
      afterClosed: () => of(true),
    } as any);
    component.openDeleteConfirmation(1);
    expect(dialogSpy.open).toHaveBeenCalled();
  });

  it('should delete comment on confirmation', () => {
    spyOn(component, 'onDelete');
    dialogSpy.open.and.returnValue({
      afterClosed: () => of(true),
    } as any);
    component.openDeleteConfirmation(1);
    expect(component.onDelete).toHaveBeenCalledWith(1);
  });
});
