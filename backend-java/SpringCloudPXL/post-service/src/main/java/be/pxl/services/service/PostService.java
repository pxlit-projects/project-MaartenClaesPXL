package be.pxl.services.service;

import be.pxl.services.api.controller.CommentController;
import be.pxl.services.api.controller.CommentServiceClient;
import be.pxl.services.api.controller.PostController;
import be.pxl.services.api.controller.ReviewServiceClient;
import be.pxl.services.api.dto.CommentDTO;
import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.dto.ReviewDTO;
import be.pxl.services.api.request.CreatePostRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final ReviewServiceClient reviewServiceClient;
    private final CommentServiceClient commentServiceClient;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    public List<PostDTO> getAllPosts(String userRole) {
        if (userRole.equals("Redacteur")) {
            return postRepository.findAll().stream().map(PostDTO::new).toList();
        } else if (userRole.equals("Gebruiker")) {
            return postRepository.findAll().stream().filter(p -> p.getStatus().equals(PostStatus.PUBLISHED)).map(PostDTO::new).toList();
        }
        log.error("getAllPosts");
        throw new RuntimeException("Error: " + userRole);
    }

    @Override
    public void addPost(CreatePostRequest postRequest) {
        PostStatus status;
        if (postRequest.isConcept()) {
            status = PostStatus.CONCEPT;
            log.info("Adding concept post");
        } else {
            status = PostStatus.UNREVIEWED;
            log.info("Adding unreviewed post");
        }
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .status(status)
                .author(postRequest.getAuthor())
                .authorEmail(postRequest.getAuthorEmail())
                .content(postRequest.getContent())
                .build();
        postRepository.save(post);
        log.info("Added post: " + post);
    }

    @Override
    public void updatePost(Long id, CreatePostRequest postRequest) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.error("updatePost - post not found");
            throw new NotFoundException("Post not found");
        }
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        if (postRequest.isConcept()) {
            log.info("Updating concept post");
            post.setStatus(PostStatus.CONCEPT);
        } else {
            log.info("Updating unreviewed post");
            post.setStatus(PostStatus.UNREVIEWED);
        }
        postRepository.save(post);
        log.info("Updated post: " + post);
    }

    @Override
    public PostDTO publishPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.error("publishPost - post not found");
            throw new NotFoundException("Post not found");
        }
        if (post.getStatus() != PostStatus.APPROVED) {
            log.error("publishPost - post not approved");
            throw new ForbiddenException("Your post has not been approved yet");
        }
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);
        log.info("Published post: " + post);
        return new PostDTO(post);
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.error("getPostById - post not found");
            throw new NotFoundException("Post not found");
        }

        List<ReviewDTO> reviews = new ArrayList<>();
        for (Long reviewId : post.getReviews()) {
            try {
                LinkedHashMap linkedHashMap = reviewServiceClient.getReviewById(reviewId);
                ObjectMapper objectMapper = new ObjectMapper();
                ReviewDTO reviewDTO = new ReviewDTO();
                reviewDTO.setId(Long.parseLong(linkedHashMap.get("id").toString()));
                reviewDTO.setDescription(linkedHashMap.get("description").toString());
                reviewDTO.setPostId(Long.parseLong(linkedHashMap.get("postId").toString()));
                reviewDTO.setApproved((boolean) linkedHashMap.get("approved"));
                reviewDTO.setReviewer(linkedHashMap.get("reviewer").toString());
                reviews.add(reviewDTO);
            } catch(Exception e) {
                log.error("getPostById - review not found");
                System.out.println("something went wrong: " + e.getMessage());
            }
        }

        List<CommentDTO> comments = new ArrayList<>();
        for (Long commentId : post.getComments()) {
            try {
                LinkedHashMap linkedHashMap = commentServiceClient.getCommentById(commentId);
                ObjectMapper objectMapper = new ObjectMapper();
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setId(Long.parseLong(linkedHashMap.get("id").toString()));
                commentDTO.setText(linkedHashMap.get("text").toString());
                commentDTO.setPostId(Long.parseLong(linkedHashMap.get("postId").toString()));
                commentDTO.setCommenter(linkedHashMap.get("commenter").toString());
                comments.add(commentDTO);
            }
            catch(Exception e) {
                log.error("getPostById - comment not found");
                System.out.println("something went wrong: " + e.getMessage());
            }
        }

        PostDTO postDTO = new PostDTO(post);
        postDTO.setReviews(reviews);
        postDTO.setComments(comments);

        return postDTO;
    }

    @RabbitListener(queues = "reviewQueue")
    public void addReview(Review message) {
        log.info("Adding review from queue: " + message);
        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if (post == null) {
            log.error("addReview - post not found");
            throw new NotFoundException("Post not found");
        }
        if (message.isApproved()) {
            post.setStatus(PostStatus.APPROVED);
        } else {
            post.setStatus(PostStatus.REJECTED);
        }
        post.addReview(message.getId());
        postRepository.save(post);
        emailService.sendMail(post, message);
        log.info("Added review to post: " + post);
    }

    @RabbitListener(queues = "commentQueue")
    public void addComment(Comment message) {
        log.info("Adding comment from queue: " + message);
        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if (post == null) {
            log.error("addComment - post not found");
            throw new NotFoundException("Post not found");
        }
        post.addComment(message.getId());
        postRepository.save(post);
        log.info("Added comment to post: " + post);
    }

    @RabbitListener(queues = "deleteCommentQueue")
    public void deleteComment(Comment message) {
        log.info("Deleting comment from queue: " + message);
        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if (post == null) {
            log.error("deleteComment - post not found");
            throw new NotFoundException("Post not found");
        }
        post.deleteComment(message.getId());
        postRepository.save(post);
        log.info("Deleted comment to post: " + post);
    }
}
