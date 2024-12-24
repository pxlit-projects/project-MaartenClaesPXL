package be.pxl.services.api.dto;

import be.pxl.services.domain.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long postId;
    private boolean isApproved;
    private String description;
    private String reviewer;

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.postId = review.getPostId();
        this.isApproved = review.isApproved();
        this.description = review.getDescription();
        this.reviewer = review.getReviewer();
    }
}
