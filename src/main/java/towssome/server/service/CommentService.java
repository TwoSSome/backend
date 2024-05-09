package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.CommentReq;
import towssome.server.dto.CommentUpdateDto;
import towssome.server.dto.CommentUpdateReq;
import towssome.server.dto.ReviewPostUpdateDto;
import towssome.server.entity.Comment;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundCommunityPostException;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.exception.NotFoundReviewPostException;
import towssome.server.repository.CommentRepository;
import towssome.server.repository.MemberRepository;
import towssome.server.repository.ReviewPostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReviewPostRepository reviewPostRepository;

    public void createComment(CommentReq commentReq) {
        Long reviewId = commentReq.reviewPostId();
        Long memberId = commentReq.memberId();
        // reviewId로 ReviewPost 조회
        ReviewPost reviewPost = reviewPostRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewPostException("ReviewPost not found with id: " + reviewId));;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("Member not found with id: " + memberId));

        Comment comment = new Comment(
                commentReq.body(),
                member,
                reviewPost
        );
        commentRepository.save(comment);
    }

    public Comment findComment(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundMemberException("Comment not found with id: " + commentId));
    }

    public Page<Comment> getCommentsByReviewId(Long reviewId, Pageable pageable) {
        return commentRepository.findAllByReviewPostId(reviewId, pageable);
    }


    @Transactional
    public void updateComment(CommentUpdateReq req) {
        commentRepository.updateComment(req.id(), req.body());
    }

    public void deleteComment(Comment comment) { commentRepository.delete(comment); }

}
