package org.example.board_project.member.controller;

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
    @DisplayName("Shows member registration form")
    void createForm() throws Exception {
        mockMvc.perform(get("/members/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/new"))
                .andExpect(content().string(containsString("회원가입")));
    }

    @Test
    @DisplayName("Registers a member")
    void create() throws Exception {
        mockMvc.perform(post("/members")
                        .param("username", "junho")
                        .param("password", "password123")
                        .param("email", "junho@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/new"));
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
}
