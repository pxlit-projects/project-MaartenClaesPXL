package be.pxl.services.service;


import be.pxl.services.domain.CommentDetail;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.ReviewDetail;
import be.pxl.services.exceptions.PostNotApprovedException;
import be.pxl.services.exceptions.PostNotFoundException;
import be.pxl.services.api.controller.CommentServiceClient;
import be.pxl.services.api.controller.ReviewServiceClient;
import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.request.CreatePostRequest;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
        } else  {
            return postRepository.findAll().stream().filter(p -> p.getStatus().equals(PostStatus.PUBLISHED)).map(PostDTO::new).toList();
        }
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
                .postDate(new Date())
                .build();
        postRepository.save(post);
        log.info("Added post: " + post);
    }

    @Override
    public void updatePost(Long id, CreatePostRequest postRequest) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.error("updatePost - post not found");
            throw new PostNotFoundException("Post not found");
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
        post.setPostDate(new Date());
        postRepository.save(post);
        log.info("Updated post: " + post);
    }

    @Override
    public PostDTO publishPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.error("publishPost - post not found");
            throw new PostNotFoundException("Post not found");
        }
        if (post.getStatus() != PostStatus.APPROVED) {
            log.error("publishPost - post not approved");
            throw new PostNotApprovedException("Your post has not been approved yet");
        }
        post.setPostDate(new Date());
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
            throw new PostNotFoundException("Post not found");
        }

        List<ReviewDetail> reviews = new ArrayList<>();
        for (Long reviewId : post.getReviews()) {
            try {
                LinkedHashMap linkedHashMap = reviewServiceClient.getReviewById(reviewId);
                ReviewDetail review = new ReviewDetail();
                review.setId(Long.parseLong(linkedHashMap.get("id").toString()));
                review.setDescription(linkedHashMap.get("description").toString());
                review.setPostId(Long.parseLong(linkedHashMap.get("postId").toString()));
                review.setApproved((boolean) linkedHashMap.get("approved"));
                review.setReviewer(linkedHashMap.get("reviewer").toString());
                reviews.add(review);
            } catch(Exception e) {
                log.error("getPostById - review not found");
                System.out.println("something went wrong: " + e.getMessage());
            }
        }

        List<CommentDetail> comments = new ArrayList<>();
        for (Long commentId : post.getComments()) {
            try {
                LinkedHashMap linkedHashMap = commentServiceClient.getCommentById(commentId);
                CommentDetail comment = new CommentDetail();
                comment.setId(Long.parseLong(linkedHashMap.get("id").toString()));
                comment.setText(linkedHashMap.get("text").toString());
                comment.setPostId(Long.parseLong(linkedHashMap.get("postId").toString()));
                comment.setCommenter(linkedHashMap.get("commenter").toString());
                comments.add(comment);
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
    public void addReview(ReviewDetail message) {
        log.info("Adding review from queue: " + message);
        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if (post == null) {
            log.error("addReview - post not found");
            throw new PostNotFoundException("Post not found");
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
    public void addComment(CommentDetail message) {
        log.info("Adding comment from queue: " + message);
        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if (post == null) {
            log.error("addComment - post not found");
            throw new PostNotFoundException("Post not found");
        }
        post.addComment(message.getId());
        postRepository.save(post);
        log.info("Added comment to post: " + post);
    }

    @RabbitListener(queues = "deleteCommentQueue")
    public void deleteComment(CommentDetail message) {
        log.info("Deleting comment from queue: " + message);
        Post post = postRepository.findById(message.getPostId()).orElse(null);
        if (post == null) {
            log.error("deleteComment - post not found");
            throw new PostNotFoundException("Post not found");
        }
        post.deleteComment(message.getId());
        postRepository.save(post);
        log.info("Deleted comment to post: " + post);
    }
}
