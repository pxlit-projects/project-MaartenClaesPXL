package be.pxl.services.service;

import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreateReviewRequest;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.ReviewRepository;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;



    public void addReview(CreateReviewRequest createReviewRequest, Long postId) {
        Review review = Review.builder()
                .postId(postId)
                .description(createReviewRequest.getDescription())
                .isApproved(createReviewRequest.isApproved())
                .reviewer(createReviewRequest.getReviewer())
                .build();
        reviewRepository.save(review);

        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("reviewQueue", review);
    }

    @Override
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            throw new NotFoundException("Review not found");
        }
        return new ReviewDTO(review);
    }
}
