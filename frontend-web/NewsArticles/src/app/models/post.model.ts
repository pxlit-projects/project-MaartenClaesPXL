import { Review } from "./review.model";
import { Comment } from "./comment.model";

export class Post {
    id: number;
    postDate: Date;
    title: string;
    content: string;
    author: string;
    authorEmail: string;
    status: string;
    reviews: Review[];
    comments: Comment[];

    constructor(id: number, postDate: Date, title: string, content: string, author: string, status: string, reviews: Review[], comments: Comment[], authorEmail: string) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.postDate = postDate;
        this.status = status;
        this.reviews = reviews;
        this.comments = comments;
        this.authorEmail = authorEmail;
    }
}