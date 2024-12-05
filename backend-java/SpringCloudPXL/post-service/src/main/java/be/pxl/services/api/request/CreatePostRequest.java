package be.pxl.services.api.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePostRequest {
    private String title;
    private String content;
    private String author;
    private boolean isConcept;
}
