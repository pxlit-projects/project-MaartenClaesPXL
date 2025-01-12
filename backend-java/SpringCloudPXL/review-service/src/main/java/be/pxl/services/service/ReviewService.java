package be.pxl.services.service;

import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreateReviewRequest;
import be.pxl.services.domain.Review;
import be.pxl.services.exceptions.ReviewNotFoundException;
import be.pxl.services.repository.ReviewRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    public void addReview(CreateReviewRequest createReviewRequest, Long postId) {
        log.info("addReview: " + createReviewRequest);
        Review review = Review.builder()
                .postId(postId)
                .description(createReviewRequest.getDescription())
                .isApproved(createReviewRequest.isApproved())
                .reviewer(createReviewRequest.getReviewer())
                .build();
        reviewRepository.save(review);
        log.info("Review added: " + review);

        rabbitTemplate.convertAndSend("reviewQueue", review);
        log.info("Review added to queue");
    }

    @Override
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            log.info("Review not found: " + id);
            throw new ReviewNotFoundException("Review not found");
        }
        return new ReviewDTO(review);
    }
}
