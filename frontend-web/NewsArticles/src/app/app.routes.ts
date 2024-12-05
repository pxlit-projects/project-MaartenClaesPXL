import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AddPostComponent } from './core/post/add-post/add-post.component';
import { PostListComponent } from './core/post/post-list/post-list.component';
import { PostDetailComponent } from './core/post/post-detail/post-detail.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'add-post', component: AddPostComponent },
    { path: 'posts-list', component: PostListComponent },
    { path: 'post/:id', component: PostDetailComponent },
    { path: '', redirectTo: 'login', pathMatch: 'full' }, 
];
