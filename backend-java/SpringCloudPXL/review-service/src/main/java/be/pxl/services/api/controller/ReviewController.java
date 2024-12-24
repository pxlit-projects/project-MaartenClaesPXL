package be.pxl.services.api.controller;

import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreateReviewRequest;
import be.pxl.services.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{postId}/addReview")
    public ResponseEntity<HttpStatus> addPost(@RequestBody CreateReviewRequest createReviewRequest, @PathVariable Long postId) {
        reviewService.addReview(createReviewRequest, postId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ReviewDTO getReviewById(@PathVariable Long id) {
        ReviewDTO reviewDTO = reviewService.getReviewById(id);
        return reviewDTO;
    }
}
