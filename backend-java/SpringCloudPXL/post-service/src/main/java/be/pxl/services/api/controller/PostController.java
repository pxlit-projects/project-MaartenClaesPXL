package be.pxl.services.api.controller;
import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.request.CreatePostRequest;
import be.pxl.services.service.PostService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping()
    public ResponseEntity<List<PostDTO>> getAllPosts(@RequestHeader(value = "role", defaultValue = "Gebruiker") String userRole) {
        return new ResponseEntity<List<PostDTO>>(postService.getAllPosts(userRole), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> addPost(@RequestBody CreatePostRequest postRequest) {
        postService.addPost(postRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        return new ResponseEntity<PostDTO>(postService.getPostById(id), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<HttpStatus> updatePost(@PathVariable Long id, @RequestBody CreatePostRequest postRequest) {
        postService.updatePost(id, postRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
