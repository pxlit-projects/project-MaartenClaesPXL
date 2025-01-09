package be.pxl.services.api.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;

@FeignClient(name="comment-service")
public interface CommentServiceClient {
    @GetMapping("/api/comments/{id}")
    LinkedHashMap getCommentById(@PathVariable("id") Long id);
}
