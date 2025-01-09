package be.pxl.services.service;

import be.pxl.services.api.dto.PostDTO;
import be.pxl.services.api.request.CreatePostRequest;

import java.util.List;

public interface IPostService {
    public List<PostDTO> getAllPosts(String userRole);

    void addPost(CreatePostRequest postRequest);

    PostDTO getPostById(Long id);

    void updatePost(Long id, CreatePostRequest postRequest);

    PostDTO publishPost(Long id);
}
