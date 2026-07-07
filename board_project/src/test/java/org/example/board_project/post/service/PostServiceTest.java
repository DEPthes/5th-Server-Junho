package org.example.board_project.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
import org.example.board_project.domain.post.dto.PostCreateRequest;
import org.example.board_project.domain.post.dto.PostListResponse;
import org.example.board_project.domain.post.dto.PostUpdateRequest;
import org.example.board_project.domain.post.entity.Post;
import org.example.board_project.domain.post.repository.PostRepository;
import org.example.board_project.domain.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("Creates a post")
    void create() {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        PostCreateRequest request = new PostCreateRequest("First post", "Hello board");

        Long postId = postService.create(request, member.getId());

        assertThat(postRepository.findById(postId)).isPresent();
    }

    @Test
    @DisplayName("Cannot create a post with an unknown member")
    void createWithUnknownMember() {
        PostCreateRequest request = new PostCreateRequest("First post", "Hello board");

        assertThatThrownBy(() -> postService.create(request, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Member not found.");
    }

    @Test
    @DisplayName("Finds posts")
    void findPosts() {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        postService.create(new PostCreateRequest("First post", "Hello board"), member.getId());

        Page<PostListResponse> posts = postService.findPosts(PageRequest.of(0, 5));

        assertThat(posts.getContent()).hasSize(1);
        assertThat(posts.getContent().get(0).title()).isEqualTo("First post");
        assertThat(posts.getContent().get(0).authorName()).isEqualTo("junho");
    }

    @Test
    @DisplayName("Finds a post")
    void findPost() {
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        assertThat(postService.findPost(post.getId()).title()).isEqualTo("First post");
        assertThat(postService.findPost(post.getId()).content()).isEqualTo("Hello board");
    }

    @Test
    @DisplayName("Updates a post")
    void update() {
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        postService.update(post.getId(), new PostUpdateRequest("Updated post", "Updated content"));

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getTitle()).isEqualTo("Updated post");
        assertThat(updatedPost.getContent()).isEqualTo("Updated content");
    }

    @Test
    @DisplayName("Deletes a post")
    void delete() {
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        postService.delete(post.getId());

        assertThat(postRepository.findById(post.getId())).isEmpty();
    }
}
