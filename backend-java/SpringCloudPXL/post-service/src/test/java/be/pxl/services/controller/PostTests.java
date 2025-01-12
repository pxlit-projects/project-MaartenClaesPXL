package be.pxl.services.controller;

import be.pxl.services.PostServiceApplication;
import be.pxl.services.api.controller.ReviewServiceClient;
import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.request.CreatePostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.service.CommentService;
import be.pxl.services.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PostServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class PostTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void testAddPostConcept() throws Exception {
        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .isConcept(true)
                .authorEmail("12202385@student.pxl.com")
                .author("student")
                .title("Test Post")
                .build();

        String postString = objectMapper.writeValueAsString(createPostRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(postString))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().getFirst();
        assertNotNull(post);
        assertEquals(createPostRequest.getTitle(), post.getTitle());
        assertEquals(createPostRequest.getAuthorEmail(), post.getAuthorEmail());
        assertEquals(createPostRequest.getAuthor(), post.getAuthor());
        assertEquals(PostStatus.CONCEPT, post.getStatus());
    }

    @Test
    public void testAddPostUnreviewed() throws Exception {
        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .isConcept(false)
                .authorEmail("12202385@student.pxl.com")
                .author("student")
                .title("Test Post")
                .build();

        String postString = objectMapper.writeValueAsString(createPostRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postString))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().getFirst();
        assertNotNull(post);
        assertEquals(createPostRequest.getTitle(), post.getTitle());
        assertEquals(createPostRequest.getAuthorEmail(), post.getAuthorEmail());
        assertEquals(createPostRequest.getAuthor(), post.getAuthor());
        assertEquals(PostStatus.UNREVIEWED, post.getStatus());
    }

    @Test
    public void testGetAllPostsForGebruiker() throws Exception {
        Post publishedPost = Post.builder()
                .status(PostStatus.PUBLISHED)
                .build();

        Post unreviewedPost = new Post();
        unreviewedPost.setStatus(PostStatus.UNREVIEWED);

        PostDTO postDTO = new PostDTO(publishedPost);

        postRepository.save(publishedPost);
        postRepository.save(unreviewedPost);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("role", "Gebruiker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(PostStatus.PUBLISHED.toString()))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    public void testGetAllPostsForRedacteur() throws Exception {
        Post publishedPost = Post.builder()
                .status(PostStatus.PUBLISHED)
                .build();

        Post unreviewedPost = Post.builder()
                .status(PostStatus.UNREVIEWED)
                .build();

        PostDTO postDTO = new PostDTO(publishedPost);

        postRepository.save(publishedPost);
        postRepository.save(unreviewedPost);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("role", "Redacteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(PostStatus.PUBLISHED.toString()))
                .andExpect(jsonPath("$[1].status").value(PostStatus.UNREVIEWED.toString()));
    }

    @Test
    public void testUpdatePost() throws Exception {
        Post post = Post.builder().build();

        postRepository.save(post);
        Long postId = postRepository.findAll().getFirst().getId();

        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .title("updated").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findAll().getFirst();

        assertNotNull(updatedPost);
        assertEquals(createPostRequest.getTitle(), updatedPost.getTitle());
    }

    @Test
    public void testUpdatePostShouldReturnNotFound() throws Exception {
        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .title("updated").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateConceptPost() throws Exception {
        Post post = Post.builder().build();

        postRepository.save(post);
        Long postId = postRepository.findAll().getFirst().getId();

        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .isConcept(true)
                .title("updated").build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findAll().getFirst();

        assertNotNull(updatedPost);
        assertEquals(createPostRequest.getTitle(), updatedPost.getTitle());
        assertEquals(PostStatus.CONCEPT, updatedPost.getStatus());
    }

    @Test
    public void testGetPostById() throws Exception {
        Post post = Post.builder()
                .title("test")
                .build();

        postRepository.save(post);

        Long postId = postRepository.findAll().getFirst().getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(post.getTitle()));
    }

    @Test
    public void testGetPostByIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPublishPost() throws Exception {
        Post post = Post.builder()
                .title("test")
                .status(PostStatus.APPROVED)
                .build();

        postRepository.save(post);

        Long postId = postRepository.findAll().getFirst().getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + postId + "/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(post.getTitle()))
                .andExpect(jsonPath("status").value(PostStatus.PUBLISHED.toString()));

    }
}

