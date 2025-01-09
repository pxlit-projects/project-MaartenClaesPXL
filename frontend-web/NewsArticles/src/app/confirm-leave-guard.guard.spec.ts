import { TestBed } from '@angular/core/testing';
import { CanDeactivateFn } from '@angular/router';

import { confirmLeaveGuard } from './confirm-leave.guard';
import { AddPostComponent } from './core/post/add-post/add-post.component';

describe('confirmLeaveGuardGuard', () => {
  const executeGuard: CanDeactivateFn<AddPostComponent> = (...guardParameters) => 
      TestBed.runInInjectionContext(() => confirmLeaveGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
