package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private String author;
    private String authorEmail;
    private Date postDate;
    private PostStatus status;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reviewIds", joinColumns = @JoinColumn(name = "post"))
    @Column(name = "reviewId")
    private List<Long> reviews = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "commentIds", joinColumns = @JoinColumn(name = "post"))
    @Column(name = "commentId")
    private List<Long> comments = new ArrayList<>();

    public Post(String title, String content, String author, PostStatus status) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.status = status;
        this.postDate = new Date();
        this.comments = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public void addReview(Long id) {
        this.reviews.add(id);
    }

    public void addComment(Long id) {
        this.comments.add(id);
    }

    public void deleteComment(Long id) {
        this.comments.remove(id);
    }
}
