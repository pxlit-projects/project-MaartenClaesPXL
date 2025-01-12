package be.pxl.services;

import be.pxl.services.api.request.CreateCommentRequest;
import be.pxl.services.api.request.UpdateCommentRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.repository.CommentRepository;
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
import org.springframework.transaction.annotation.Transactional;
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
public class CommentTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Container
    private static MySQLContainer sqlContainer =
            new MySQLContainer("mysql:8.3");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    public void testCreateComment() throws Exception {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setCommenter("commenter");
        createCommentRequest.setText("test text");

        String commentString = objectMapper.writeValueAsString(createCommentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/1/addComment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentString))
                .andExpect(status().isCreated());

        assertEquals(1, commentRepository.findAll().size());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("commentQueue"), any(Comment.class));
    }

    @Test
    public void testGetComment() throws Exception {
        Comment comment = Comment.builder()
                .text("original text")
                .postId(1L)
                .commenter("commenter")
                .build();

        commentRepository.save(comment);
        Long commentId = commentRepository.findAll().getFirst().getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/" + commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("text").value("original text"))
                .andExpect(jsonPath("postId").value(1L))
                .andExpect(jsonPath("id").value(commentId))
                .andExpect(jsonPath("commenter").value("commenter"));
    }

    @Test
    public void testGetCommentShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateComment() throws Exception {
        Comment comment = Comment.builder()
                .text("original text")
                .postId(1L)
                .commenter("commenter")
                .build();

        commentRepository.save(comment);
        Long commentId = commentRepository.findAll().getFirst().getId();

        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest();
        updateCommentRequest.setText("updated test text");

        String commentString = objectMapper.writeValueAsString(updateCommentRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentString))
                .andExpect(status().isOk());

        assertEquals(1, commentRepository.findAll().size());
        assertEquals("updated test text", commentRepository.findAll().getFirst().getText());
    }

    @Test
    public void testUpdateCommentCommentNotFound() throws Exception {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest();
        updateCommentRequest.setText("updated test text");

        String commentString = objectMapper.writeValueAsString(updateCommentRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/comments/30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentString))
                        .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteComment() throws Exception {
        Comment comment = Comment.builder()
                .text("original text")
                .postId(1L)
                .commenter("commenter")
                .build();

        commentRepository.save(comment);
        Long commentId = commentRepository.findAll().getFirst().getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/" + commentId))
                .andExpect(status().isOk());

        assertEquals(0, commentRepository.findAll().size());
    }

    @Test
    public void testDeleteCommentCommentNotFoundShouldThrowNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/30"))
                .andExpect(status().isNotFound());
    }
}
