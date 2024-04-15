package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.CommentDTO;
import towssome.server.entity.Comment;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.exception.NotFoundReviewPostException;
import towssome.server.repository.CommentRepository;
import towssome.server.repository.MemberRepository;
import towssome.server.repository.ReviewPostRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReviewPostRepository reviewPostRepository;

    public void createComment(Long memberId, Long reviewId, CommentDTO commentDTO) {
        // reviewId로 ReviewPost 조회
        ReviewPost reviewPost = reviewPostRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewPostException("ReviewPost not found with id: " + reviewId));;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("Member not found with id: " + memberId));

        // CommentDTO에 ReviewPost 설정
        commentDTO = new CommentDTO(commentDTO.body(), member, reviewPost);

        // Comment 생성 및 저장
        Comment comment = new Comment(
                commentDTO.body(),
                commentDTO.member(),
                commentDTO.reviewPost()
        );
        commentRepository.save(comment);
    }

}
