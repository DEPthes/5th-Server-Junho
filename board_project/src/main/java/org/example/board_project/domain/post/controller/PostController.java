package org.example.board_project.domain.post.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.board_project.domain.post.dto.PostCreateRequest;
import org.example.board_project.domain.post.dto.PostResponse;
import org.example.board_project.domain.post.dto.PostUpdateRequest;
import org.example.board_project.domain.post.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PostController {

    private static final String LOGIN_MEMBER_ID = "loginMemberId";
    private static final int PAGE_SIZE = 5;

    private final PostService postService;

    @GetMapping("/main")
    public String main(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageNumber = Math.max(page, 0);
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);

        model.addAttribute("postPage", postService.findPosts(pageable));
        return "main";
    }

    @GetMapping("/posts/new")
    public String createForm(Model model, HttpSession session) {
        if (session.getAttribute(LOGIN_MEMBER_ID) == null) {
            return "redirect:/members/login";
        }

        model.addAttribute("postCreateRequest", new PostCreateRequest());
        return "posts/new";
    }

    @PostMapping("/posts")
    public String create(
            @Valid @ModelAttribute PostCreateRequest postCreateRequest,
            BindingResult bindingResult,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute(LOGIN_MEMBER_ID);

        if (memberId == null) {
            return "redirect:/members/login";
        }

        if (bindingResult.hasErrors()) {
            return "posts/new";
        }

        postService.create(postCreateRequest, memberId);
        return "redirect:/main";
    }

    @GetMapping("/posts/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        model.addAttribute("post", postService.findPost(postId));
        return "posts/detail";
    }

    @GetMapping("/posts/{postId}/edit")
    public String updateForm(@PathVariable Long postId, Model model, HttpSession session) {
        if (session.getAttribute(LOGIN_MEMBER_ID) == null) {
            return "redirect:/members/login";
        }

        PostResponse post = postService.findPost(postId);
        model.addAttribute("postId", post.id());
        model.addAttribute("postUpdateRequest", new PostUpdateRequest(post.title(), post.content()));
        return "posts/edit";
    }

    @PostMapping("/posts/{postId}/edit")
    public String update(
            @PathVariable Long postId,
            @Valid @ModelAttribute PostUpdateRequest postUpdateRequest,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (session.getAttribute(LOGIN_MEMBER_ID) == null) {
            return "redirect:/members/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", postId);
            return "posts/edit";
        }

        postService.update(postId, postUpdateRequest);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/delete")
    public String delete(@PathVariable Long postId, HttpSession session) {
        if (session.getAttribute(LOGIN_MEMBER_ID) == null) {
            return "redirect:/members/login";
        }

        postService.delete(postId);
        return "redirect:/main";
    }
}
