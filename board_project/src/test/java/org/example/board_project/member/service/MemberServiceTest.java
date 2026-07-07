package org.example.board_project.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.board_project.domain.member.dto.MemberCreateRequest;
import org.example.board_project.domain.member.dto.MemberLoginRequest;
import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
import org.example.board_project.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Joins a member")
    void join() {
        MemberCreateRequest request = new MemberCreateRequest(
                "junho",
                "password123",
                "junho@example.com"
        );

        Long memberId = memberService.join(request);

        Member member = memberRepository.findById(memberId).orElseThrow();
        assertThat(member.getUsername()).isEqualTo("junho");
        assertThat(member.getEmail()).isEqualTo("junho@example.com");
    }

    @Test
    @DisplayName("Cannot join with a duplicate username")
    void joinWithDuplicateUsername() {
        memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        MemberCreateRequest request = new MemberCreateRequest(
                "junho",
                "password456",
                "other@example.com"
        );

        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already in use.");
    }

    @Test
    @DisplayName("Cannot join with a duplicate email")
    void joinWithDuplicateEmail() {
        memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        MemberCreateRequest request = new MemberCreateRequest(
                "other",
                "password456",
                "junho@example.com"
        );

        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already in use.");
    }

    @Test
    @DisplayName("Logs in a member")
    void login() {
        Member savedMember = memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        MemberLoginRequest request = new MemberLoginRequest("junho", "password123");

        Long memberId = memberService.login(request);

        assertThat(memberId).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("Cannot login with a wrong password")
    void loginWithWrongPassword() {
        memberRepository.save(new Member("junho", "password123", "junho@example.com"));
        MemberLoginRequest request = new MemberLoginRequest("junho", "wrong-password");

        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password.");
    }

    @Test
    @DisplayName("Cannot login with an unknown username")
    void loginWithUnknownUsername() {
        MemberLoginRequest request = new MemberLoginRequest("unknown", "password123");

        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password.");
    }
}
