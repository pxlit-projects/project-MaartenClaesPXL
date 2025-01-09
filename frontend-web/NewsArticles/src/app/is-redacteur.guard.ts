import { CanActivateFn } from '@angular/router';

export const isRedacteurGuard: CanActivateFn = (route, state) => {
  const canAvtivate: boolean = localStorage.getItem('role')?.toLocaleLowerCase() === 'redacteur';
  if (!canAvtivate) {
    alert('You are not authorized to access this page');
  }
  return canAvtivate
};
