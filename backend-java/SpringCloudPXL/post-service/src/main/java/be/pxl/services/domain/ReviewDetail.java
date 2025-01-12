package be.pxl.services.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetail {
    private Long id;
    private Long postId;
    private boolean isApproved;
    private String description;
    private String reviewer;
}
