import { CanDeactivateFn } from '@angular/router';
import { AddPostComponent } from './core/post/add-post/add-post.component';

export const confirmLeaveGuard: CanDeactivateFn<AddPostComponent> = (component, currentRoute, currentState, nextState) => {
  if(component.postForm.dirty) {
    return window.confirm('Are you sure you want to leave?');
  } else {
    return true;
  }
};
