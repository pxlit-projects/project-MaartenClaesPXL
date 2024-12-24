package be.pxl.services.service;

import be.pxl.services.api.controller.ReviewServiceClient;
import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreatePostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.hibernate.Hibernate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    @Autowired
    private final ReviewServiceClient reviewServiceClient;

    public List<PostDTO> getAllPosts(String userRole) {
        if (userRole.equals("Redacteur")) {
            return postRepository.findAll().stream().map(PostDTO::new).toList();
        } else if (userRole.equals("Gebruiker")) {
            return postRepository.findAll().stream().filter(p -> p.getStatus().equals(PostStatus.PUBLISHED)).map(PostDTO::new).toList();
        }
        throw new RuntimeException("Error: " + userRole);
    }

    @Override
    public void addPost(CreatePostRequest postRequest) {
        PostStatus status;
        if (postRequest.isConcept()) {
            status = PostStatus.CONCEPT;
        } else {
            status = PostStatus.UNREVIEWED;
        }
        Post post = new Post(
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getAuthor(),
                status);
        postRepository.save(post);
    }

    @Override
    public void updatePost(Long id, CreatePostRequest postRequest) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            throw new NotFoundException("Post not found");
        }
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setAuthor(postRequest.getAuthor());
        if (postRequest.isConcept()) {
            post.setStatus(PostStatus.CONCEPT);
        } else {
            post.setStatus(PostStatus.UNREVIEWED);
        }
        postRepository.save(post);
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            throw new NotFoundException("Post not found");
        }

        Hibernate.initialize(post.getReviews());

        List<ReviewDTO> reviews = new ArrayList<>();

        for (Long reviewId : post.getReviews()) {
            LinkedHashMap linkedHashMap = reviewServiceClient.getReviewById(reviewId);
            ObjectMapper objectMapper = new ObjectMapper();
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setId(Long.parseLong(linkedHashMap.get("id").toString()));
            reviewDTO.setDescription(linkedHashMap.get("description").toString());
            reviewDTO.setPostId(Long.parseLong(linkedHashMap.get("postId").toString()));
            reviewDTO.setApproved((boolean) linkedHashMap.get("approved"));
            reviewDTO.setReviewer(linkedHashMap.get("reviewer").toString());
            reviews.add(reviewDTO);
        }

        PostDTO postDTO = new PostDTO(post);
        postDTO.setReviews(reviews);

        return postDTO;
    }

    @RabbitListener(queues = "reviewQueue")
    public void listen(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Review review = objectMapper.readValue(message, Review.class);

        Post post = postRepository.findById(review.getPostId()).orElse(null);
        if (post == null) {
            throw new NotFoundException("Post not found");
        }
        if (review.isApproved()) {
            post.setStatus(PostStatus.APPROVED);
        } else {
            post.setStatus(PostStatus.REJECTED);
        }
        post.addReview(review.getId());
        postRepository.save(post);
    }

}
