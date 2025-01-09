package be.pxl.services.api.controller;

import be.pxl.services.api.dto.CommentDTO;
import be.pxl.services.api.request.CreateCommentRequest;
import be.pxl.services.api.request.UpdateCommentRequest;
import be.pxl.services.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @PostMapping("/{postId}/addComment")
    public ResponseEntity<CommentDTO> addComment(@RequestBody CreateCommentRequest createCommentRequest, @PathVariable Long postId) {
        log.info("POST comment: " + createCommentRequest.toString());
        CommentDTO commentDTO = commentService.addComment(createCommentRequest, postId);
        return new ResponseEntity<CommentDTO>(commentDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public CommentDTO getComment(@PathVariable Long id) {
        log.info("GET comment: " + id);
        CommentDTO commentDTO = commentService.getCommentById(id);
        return commentDTO;
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("PUT comment: " + updateCommentRequest.toString());
        CommentDTO commentDTO = commentService.updateComment(id, updateCommentRequest);
        return new ResponseEntity<CommentDTO>(commentDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long id) {
        log.info("DELETE comment: " + id);
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
