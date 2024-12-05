export class Post {
    id: number;
    postDate: Date;
    title: string;
    content: string;
    author: string;
    concept: boolean;

    constructor(id: number, postDate: Date, title: string, content: string, author: string, concept: boolean) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.concept = concept;
        this.postDate = postDate;
    }
}