package org.example.board_project.domain.member.repository;

import java.util.Optional;
import org.example.board_project.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
