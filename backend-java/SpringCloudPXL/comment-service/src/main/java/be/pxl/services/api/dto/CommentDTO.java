package be.pxl.services.api.dto;

import be.pxl.services.domain.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private Long postId;
    private String text;
    private String commenter;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.text = comment.getText();
        this.commenter = comment.getCommenter();
    }
}
