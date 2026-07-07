package org.example.board_project.member.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Shows home page")
    void home() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Shows member registration form")
    void createForm() throws Exception {
        mockMvc.perform(get("/members/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/new"))
                .andExpect(content().string(containsString("togglePasswordVisibility")))
                .andExpect(content().string(containsString("type=\"text\"")));
    }

    @Test
    @DisplayName("Registers a member")
    void create() throws Exception {
        mockMvc.perform(post("/members")
                        .param("username", "junho")
                        .param("password", "password123")
                        .param("email", "junho@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Returns form when request is invalid")
    void createWithInvalidRequest() throws Exception {
        mockMvc.perform(post("/members")
                        .param("username", "")
                        .param("password", "short")
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/new"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Returns form when username is duplicated")
    void createWithDuplicateUsername() throws Exception {
        memberRepository.save(new Member("junho", "password123", "junho@example.com"));

        mockMvc.perform(post("/members")
                        .param("username", "junho")
                        .param("password", "password456")
                        .param("email", "other@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/new"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Shows member login form")
    void loginForm() throws Exception {
        mockMvc.perform(get("/members/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/login"))
                .andExpect(content().string(containsString("togglePasswordVisibility")))
                .andExpect(content().string(containsString("type=\"text\"")));
    }

    @Test
    @DisplayName("Logs in a member")
    void login() throws Exception {
        Member member = memberRepository.save(new Member("junho", "password123", "junho@example.com"));

        mockMvc.perform(post("/members/login")
                        .param("username", "junho")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"))
                .andExpect(request().sessionAttribute("loginMemberId", member.getId()));
    }

    @Test
    @DisplayName("Returns login form when login request is invalid")
    void loginWithInvalidRequest() throws Exception {
        mockMvc.perform(post("/members/login")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("members/login"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Returns login form when password is wrong")
    void loginWithWrongPassword() throws Exception {
        memberRepository.save(new Member("junho", "password123", "junho@example.com"));

        mockMvc.perform(post("/members/login")
                        .param("username", "junho")
                        .param("password", "wrong-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/login"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Logs out a member")
    void logout() throws Exception {
        mockMvc.perform(post("/members/logout")
                        .sessionAttr("loginMemberId", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login"));
    }
}
