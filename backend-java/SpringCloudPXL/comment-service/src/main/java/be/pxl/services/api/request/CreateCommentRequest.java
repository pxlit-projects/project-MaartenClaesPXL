package be.pxl.services.api.request;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String text;
    private String commenter;
}
