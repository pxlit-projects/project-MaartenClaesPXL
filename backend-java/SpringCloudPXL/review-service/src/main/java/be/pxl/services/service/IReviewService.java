package be.pxl.services.service;

import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreateReviewRequest;


public interface IReviewService {
    void addReview(CreateReviewRequest request, Long postId);

    ReviewDTO getReviewById(Long id);
}
