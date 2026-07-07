package org.example.board_project.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must be 100 characters or less.")
    private String title;

    @NotBlank(message = "Content is required.")
    private String content;
}
