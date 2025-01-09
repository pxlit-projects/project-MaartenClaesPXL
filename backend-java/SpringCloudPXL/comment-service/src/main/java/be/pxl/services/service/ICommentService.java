package be.pxl.services.service;

import be.pxl.services.api.dto.CommentDTO;
import be.pxl.services.api.request.CreateCommentRequest;
import be.pxl.services.api.request.UpdateCommentRequest;

public interface ICommentService {
    CommentDTO addComment(CreateCommentRequest createCommentRequest, Long postId);

    CommentDTO getCommentById(Long id);

    CommentDTO updateComment(Long id, UpdateCommentRequest updateCommentRequest);

    void deleteComment(Long id);
}
