package be.pxl.services.service;

import be.pxl.services.PostServiceApplication;
import be.pxl.services.api.controller.ReviewServiceClient;
import be.pxl.services.domain.CommentDetail;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.ReviewDetail;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PostServiceApplication.class)
@Testcontainers
public class PostServiceTests {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private static RabbitTemplate rabbitTemplate;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ReviewServiceClient reviewServiceClient;

    @Container
    private static MySQLContainer mySQLContainer =
            new MySQLContainer("mysql:8.3");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
    }

    @Test
    public void testAddReview() {
        Post post = Post.builder().build();
        postRepository.save(post);

        Long postId = postRepository.findAll().getFirst().getId();

        ReviewDetail reviewDetail = ReviewDetail.builder()
                .postId(postId)
                .isApproved(true)
                .build();
        postService.addReview(reviewDetail);

        assertEquals(PostStatus.APPROVED, postRepository.findAll().getFirst().getStatus());

        reviewDetail = ReviewDetail.builder()
                .postId(postId)
                .isApproved(false)
                .build();
        postService.addReview(reviewDetail);

        assertEquals(PostStatus.REJECTED, postRepository.findAll().getFirst().getStatus());
    }

    @Test
    public void testAddComment() {
        Post post = Post.builder().build();
        postRepository.save(post);
        Long postId = postRepository.findAll().getFirst().getId();

        CommentDetail commentDetail = CommentDetail.builder()
                .id(1L)
                .postId(postId)
                .build();
        postService.addComment(commentDetail);

        post = postRepository.findAll().getFirst();

        assertEquals(1, postRepository.findAll().getFirst().getComments().size());
        assertEquals(commentDetail.getId(), postRepository.findAll().getFirst().getComments().getFirst());
    }

    @Test
    public void testDeleteComment() {
        Post post = new Post("test", "content", "author", PostStatus.APPROVED);
        post.addComment(1L);
        postRepository.save(post);
        Long postId = postRepository.findAll().getFirst().getId();

        CommentDetail commentDetail = CommentDetail.builder().postId(postId).id(1L).build();

        assertEquals(1, postRepository.findAll().getFirst().getComments().size());
        postService.deleteComment(commentDetail);
        assertEquals(0, postRepository.findAll().getFirst().getComments().size());


    }
}
