import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteCommentDialogComponent } from './delete-comment-dialog.component';
import { MatDialogRef } from '@angular/material/dialog';

describe('DeleteCommentDialogComponent', () => {
  let component: DeleteCommentDialogComponent;
  let fixture: ComponentFixture<DeleteCommentDialogComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<DeleteCommentDialogComponent>>;

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [DeleteCommentDialogComponent],
      providers: [{ provide: MatDialogRef, useValue: dialogRefSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteCommentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog with false when onCancel is called', () => {
    component.onCancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });

  it('should close dialog with true when onConfirm is called', () => {
    component.onConfirm();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });
});
