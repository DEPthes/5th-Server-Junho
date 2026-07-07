package org.example.board_project.domain.post.dto;

import java.time.LocalDateTime;
import org.example.board_project.domain.post.entity.Post;

public record PostListResponse(
        Long id,
        String title,
        String authorName,
        LocalDateTime createdAt
) {

    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getAuthorName(),
                post.getCreatedAt()
        );
    }
}
