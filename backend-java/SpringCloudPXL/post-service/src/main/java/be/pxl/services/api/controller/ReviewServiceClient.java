package be.pxl.services.api.controller;

import be.pxl.services.api.dto.ReviewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;

@FeignClient(name="review-service")
public interface ReviewServiceClient {

    @GetMapping("/api/reviews/{id}")
    LinkedHashMap getReviewById(@PathVariable("id") Long id);
}
