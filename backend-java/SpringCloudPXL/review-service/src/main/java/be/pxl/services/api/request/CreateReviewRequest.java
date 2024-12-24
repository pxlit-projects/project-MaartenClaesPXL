package be.pxl.services.api.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReviewRequest {
    private boolean isApproved;
    private String description;
    private String reviewer;
}
