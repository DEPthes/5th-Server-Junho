package org.example.board_project.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Saves a member")
    void saveMember() {
        Member member = new Member("junho", "password123", "junho@example.com");

        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo("junho");
        assertThat(savedMember.getEmail()).isEqualTo("junho@example.com");
    }

    @Test
    @DisplayName("Checks username and email existence")
    void existsByUsernameAndEmail() {
        Member member = new Member("junho", "password123", "junho@example.com");
        memberRepository.save(member);

        assertThat(memberRepository.existsByUsername("junho")).isTrue();
        assertThat(memberRepository.existsByUsername("unknown")).isFalse();
        assertThat(memberRepository.existsByEmail("junho@example.com")).isTrue();
        assertThat(memberRepository.existsByEmail("unknown@example.com")).isFalse();
    }
}
