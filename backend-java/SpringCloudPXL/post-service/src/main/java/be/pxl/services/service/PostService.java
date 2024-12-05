package be.pxl.services.service;

import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.request.CreatePostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.repository.PostRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;

    public List<PostDTO> getAllPosts(String userRole) {
        if (userRole.equals("Redacteur")) {
            return postRepository.findAll().stream().map(PostDTO::new).toList();
        } else if (userRole.equals("Gebruiker")) {
            return postRepository.findAll().stream().filter(post -> !post.isConcept()).map(PostDTO::new).toList();
        }
        throw new RuntimeException("Error: " + userRole);
    }

    @Override
    public void addPost(CreatePostRequest postRequest) {
        Post post = new Post(
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getAuthor(),
                postRequest.isConcept());
        postRepository.save(post);
    }

    @Override
    public PostDTO getPostById(Long id) {
        return postRepository.findById(id).map(PostDTO::new).orElse(null);
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
        post.setConcept(postRequest.isConcept());
        postRepository.save(post);
    }
}
