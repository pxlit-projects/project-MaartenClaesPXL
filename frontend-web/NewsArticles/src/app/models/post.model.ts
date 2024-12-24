import { Review } from "./review.model";

export class Post {
    id: number;
    postDate: Date;
    title: string;
    content: string;
    author: string;
    status: string;
    reviews: Review[];

    constructor(id: number, postDate: Date, title: string, content: string, author: string, status: string, reviews: Review[]) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.postDate = postDate;
        this.status = status;
        this.reviews = reviews;
    }
}