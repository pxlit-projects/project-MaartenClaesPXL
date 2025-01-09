package be.pxl.services.api.dto;


import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String authorEmail;
    private Date postDate;
    private PostStatus status;
    private List<ReviewDTO> reviews;
    private List<CommentDTO> comments;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
        this.status = post.getStatus();
        this.postDate = post.getPostDate();
    }
}
