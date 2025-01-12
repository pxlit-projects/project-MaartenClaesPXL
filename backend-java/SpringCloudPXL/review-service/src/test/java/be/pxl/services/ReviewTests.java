package be.pxl.services;

import be.pxl.services.api.request.CreateReviewRequest;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.ReviewRepository;
import be.pxl.services.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

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
        reviewRepository.deleteAll();
    }

    @Test
    public void testAddReview() throws Exception {
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .reviewer("testReviewer")
                .description("testDescription")
                .isApproved(true)
                .build();
        String reviewString = objectMapper.writeValueAsString(createReviewRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews/1/addReview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewString))
                .andExpect(status().isCreated());

        assertEquals(1, reviewRepository.findAll().size());

        Review review = reviewRepository.findAll().getFirst();
        assertEquals(createReviewRequest.getReviewer(), review.getReviewer());
        assertEquals(createReviewRequest.getDescription(), review.getDescription());
        assertEquals(createReviewRequest.isApproved(), review.isApproved());
        assertEquals(1L, review.getPostId());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("reviewQueue"), any(Review.class));
    }

    @Test
    public void testGetReview() throws Exception {
        Review review = Review.builder()
                .reviewer("reviewer1")
                .isApproved(true)
                .description("goed")
                .postId(1L)
                .build();
        reviewRepository.save(review);

        Long reviewId = reviewRepository.findAll().getFirst().getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/" + reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(reviewId))
                .andExpect(jsonPath("postId").value(review.getPostId()))
                .andExpect(jsonPath("reviewer").value(review.getReviewer()))
                .andExpect(jsonPath("approved").value(review.isApproved()))
                .andExpect(jsonPath("description").value(review.getDescription()));
    }

    @Test
    public void testGetReviewShouldThrowNotFoundExceptionAndReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/999"))
                .andExpect(status().isNotFound());
    }
}
