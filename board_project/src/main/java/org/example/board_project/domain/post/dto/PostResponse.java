package org.example.board_project.domain.post.dto;

import java.time.LocalDateTime;
import org.example.board_project.domain.post.entity.Post;

public record PostResponse(
        Long id,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorName(),
                post.getCreatedAt()
        );
    }
}
