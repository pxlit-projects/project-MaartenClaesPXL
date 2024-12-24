export class Review {
    description: string;
    approved: boolean;
    reviewer: string;

    constructor(description: string, approved: boolean, reviewer: string) {
        this.description = description;
        this.approved = approved;
        this.reviewer = reviewer;
    }
}