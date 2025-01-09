export class Comment {
    id: number;
    text: string;
    commenter: string;

    constructor(id: number, text: string, commenter: string) {
        this.id = id;
        this.text = text;
        this.commenter = commenter;
    }
}