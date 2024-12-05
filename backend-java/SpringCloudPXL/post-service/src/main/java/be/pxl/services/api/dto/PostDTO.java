package be.pxl.services.api.dto;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Date postDate;
    private boolean isConcept;
    private List<Review> reviews;
    private List<Comment> comments;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
        this.isConcept = post.isConcept();
        this.reviews = post.getReviews();
        this.comments = post.getComments();
        this.postDate = post.getPostDate();
    }
}
