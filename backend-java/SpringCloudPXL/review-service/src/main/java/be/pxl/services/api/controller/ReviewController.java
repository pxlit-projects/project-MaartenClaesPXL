package be.pxl.services.api.controller;

import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreateReviewRequest;
import be.pxl.services.exceptions.ReviewNotFoundException;
import be.pxl.services.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);


    @PostMapping("/{postId}/addReview")
    public ResponseEntity<HttpStatus> addPost(@RequestBody CreateReviewRequest createReviewRequest, @PathVariable Long postId) {
        log.info("POST review: " + createReviewRequest);
        reviewService.addReview(createReviewRequest, postId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        log.info("GET review: " + id);
        ReviewDTO reviewDTO = reviewService.getReviewById(id);
        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }
}
