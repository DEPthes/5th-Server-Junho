package org.example.board_project.post.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
import org.example.board_project.domain.post.entity.Post;
import org.example.board_project.domain.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("Shows main board page")
    void main() throws Exception {
        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(content().string(containsString("Board")))
                .andExpect(content().string(containsString("/posts/new")));
    }

    @Test
    @DisplayName("Shows paginated posts")
    void mainWithPagination() throws Exception {
        for (int i = 1; i <= 6; i++) {
            postRepository.save(new Post("Post " + i, "Content " + i, "junho"));
        }

        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Post 6")))
                .andExpect(content().string(containsString("page=1")))
                .andExpect(content().string(containsString("다음")));

        mockMvc.perform(get("/main").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Post 1")))
                .andExpect(content().string(containsString("이전")));
    }

    @Test
    @DisplayName("Shows post creation form")
    void createForm() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));

        mockMvc.perform(get("/posts/new")
                        .sessionAttr("loginMemberId", member.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/new"))
                .andExpect(model().attributeExists("postCreateRequest"));
    }

    @Test
    @DisplayName("Redirects to login when creating a post form without login")
    void createFormWithoutLogin() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login"));
    }

    @Test
    @DisplayName("Creates a post")
    void create() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));

        mockMvc.perform(post("/posts")
                        .sessionAttr("loginMemberId", member.getId())
                        .param("title", "First post")
                        .param("content", "Hello board"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));

        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("First post")))
                .andExpect(content().string(containsString("junho")));
    }

    @Test
    @DisplayName("Returns post creation form when request is invalid")
    void createWithInvalidRequest() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));

        mockMvc.perform(post("/posts")
                        .sessionAttr("loginMemberId", member.getId())
                        .param("title", "")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/new"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Redirects to login when creating a post without login")
    void createWithoutLogin() throws Exception {
        mockMvc.perform(post("/posts")
                        .param("title", "First post")
                        .param("content", "Hello board"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login"));
    }

    @Test
    @DisplayName("Shows post detail page")
    void detail() throws Exception {
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(get("/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/detail"))
                .andExpect(content().string(containsString("First post")))
                .andExpect(content().string(containsString("Hello board")));
    }

    @Test
    @DisplayName("Shows post edit form")
    void updateForm() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(get("/posts/{postId}/edit", post.getId())
                        .sessionAttr("loginMemberId", member.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/edit"))
                .andExpect(model().attributeExists("postUpdateRequest"));
    }

    @Test
    @DisplayName("Redirects to login when editing a post without login")
    void updateFormWithoutLogin() throws Exception {
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(get("/posts/{postId}/edit", post.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login"));
    }

    @Test
    @DisplayName("Updates a post")
    void update() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(post("/posts/{postId}/edit", post.getId())
                        .sessionAttr("loginMemberId", member.getId())
                        .param("title", "Updated post")
                        .param("content", "Updated content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post.getId()));

        mockMvc.perform(get("/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Updated post")))
                .andExpect(content().string(containsString("Updated content")));
    }

    @Test
    @DisplayName("Returns edit form when update request is invalid")
    void updateWithInvalidRequest() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(post("/posts/{postId}/edit", post.getId())
                        .sessionAttr("loginMemberId", member.getId())
                        .param("title", "")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/edit"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Deletes a post")
    void delete() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(post("/posts/{postId}/delete", post.getId())
                        .sessionAttr("loginMemberId", member.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));

        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("아직 등록된 게시글이 없습니다.")));
    }

    @Test
    @DisplayName("Redirects to login when deleting a post without login")
    void deleteWithoutLogin() throws Exception {
        Post post = postRepository.save(new Post("First post", "Hello board", "junho"));

        mockMvc.perform(post("/posts/{postId}/delete", post.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login"));
    }
}
