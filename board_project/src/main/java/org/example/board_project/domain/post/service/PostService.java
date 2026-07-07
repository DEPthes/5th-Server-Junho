package org.example.board_project.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.board_project.domain.member.entity.Member;
import org.example.board_project.domain.member.repository.MemberRepository;
import org.example.board_project.domain.post.dto.PostCreateRequest;
import org.example.board_project.domain.post.dto.PostListResponse;
import org.example.board_project.domain.post.dto.PostResponse;
import org.example.board_project.domain.post.dto.PostUpdateRequest;
import org.example.board_project.domain.post.entity.Post;
import org.example.board_project.domain.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public Page<PostListResponse> findPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(PostListResponse::from);
    }

    public PostResponse findPost(Long postId) {
        Post post = findPostEntity(postId);
        return PostResponse.from(post);
    }

    @Transactional
    public Long create(PostCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                member.getUsername()
        );

        return postRepository.save(post).getId();
    }

    @Transactional
    public void update(Long postId, PostUpdateRequest request) {
        Post post = findPostEntity(postId);
        post.update(request.getTitle(), request.getContent());
    }

    @Transactional
    public void delete(Long postId) {
        Post post = findPostEntity(postId);
        postRepository.delete(post);
    }

    private Post findPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
    }
}
