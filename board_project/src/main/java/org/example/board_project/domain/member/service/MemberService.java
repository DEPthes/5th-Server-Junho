package org.example.board_project.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.board_project.domain.member.dto.MemberCreateRequest;
import org.example.board_project.domain.member.dto.MemberLoginRequest;
import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(MemberCreateRequest request) {
        validateDuplicateMember(request);

        Member member = new Member(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );

        return memberRepository.save(member).getId();
    }

    public Long login(MemberLoginRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!member.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        return member.getId();
    }

    private void validateDuplicateMember(MemberCreateRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
    }
}
