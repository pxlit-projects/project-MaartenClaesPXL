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
    private Date postDate;
    private boolean isConcept;
    @Transient
    private List<Review> reviews;
    @Transient
    private List<Comment> comments;

    public Post(String title, String content, String author, boolean isConcept) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.isConcept = isConcept;
        this.postDate = new Date();
        this.reviews = new ArrayList<Review>();
        this.comments = new ArrayList<Comment>();
    }
}
