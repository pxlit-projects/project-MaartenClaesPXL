package be.pxl.services.service;

import be.pxl.services.api.controller.CommentController;
import be.pxl.services.api.dto.CommentDTO;
import be.pxl.services.api.request.CreateCommentRequest;
import be.pxl.services.api.request.UpdateCommentRequest;
import be.pxl.services.domain.Comment;
import be.pxl.services.repository.CommentRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    @Override
    public CommentDTO addComment(CreateCommentRequest createCommentRequest, Long postId) {
        log.info("addComment");
        Comment comment = Comment.builder()
                .commenter(createCommentRequest.getCommenter())
                .text(createCommentRequest.getText())
                .postId(postId)
                .build();
        commentRepository.save(comment);
        log.info("comment added");

        rabbitTemplate.convertAndSend("commentQueue", comment);
        log.info("comment sent to queue");
        return new CommentDTO(comment);
    }

    @Override
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            log.info("comment not found");
            throw new NotFoundException("Comment not found");
        }
        return new CommentDTO(comment);
    }

    @Override
    public CommentDTO updateComment(Long id, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            log.info("comment not found");
            throw new NotFoundException("Comment not found");
        }
        comment.setText(updateCommentRequest.getText());
        commentRepository.save(comment);
        log.info("comment updated");
        return new CommentDTO(comment);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            log.info("comment not found");
            throw new NotFoundException("Comment not found");
        }
        commentRepository.delete(comment);
        log.info("comment deleted");

        rabbitTemplate.convertAndSend("deleteCommentQueue", comment);
        log.info("delete comment sent to queue");
    }
}
