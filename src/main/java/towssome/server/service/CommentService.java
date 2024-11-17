package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Comment;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundCommentException;
import towssome.server.exception.NotFoundReviewPostException;
import towssome.server.repository.comment.CommentRepository;
import towssome.server.repository.reviewpost.ReviewPostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final MemberService memberService;
    private final CommentLikeService commentLikeService;
    private final MemberAdvice memberAdvice;

    @Transactional
    public Comment createComment(Long reviewId, String body, String username) {
        Member member = memberService.getMember(username);
        // reviewId로 ReviewPost 조회
        ReviewPost reviewPost = reviewPostRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewPostException("ReviewPost not found with id: " + reviewId));
        Comment comment = new Comment(
                body,
                member,
                reviewPost
        );
        Comment savedComment = commentRepository.save(comment);
        reviewPost.getMember().addRankPoint(5);
        reviewPost.addComments(comment); // 일대다 연관관계 추가
        return savedComment;
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundCommentException("해당 코멘트가 존재하지 않습니다."));
    }


    public void updateComment(Long commentId, CommentUpdateReq req) {
        commentRepository.updateComment(commentId, req.body());
    }

    @Transactional
    public void deleteComment(Comment comment) {
        comment.getMember().addRankPoint(-3);
        comment.getReviewPost().removeComments(comment); //일대다 연관관계 삭제
        commentRepository.delete(comment);
    }


    /* 댓글 조회 */
    public CursorResult<CommentRes> getCommentPageByReviewId(Long reviewId, Long cursorId, String sort, Pageable page){
        List<CommentRes> commentRes = new ArrayList<>();
        final Page<Comment> comments = getCommentsByReviewId(reviewId, cursorId, sort, page);
        Member member = memberAdvice.findJwtMember();
        ProfileSimpleRes profileSimpleRes;
        Boolean isLiked;
        for(Comment comment:comments){
            Member commentedMember = comment.getMember();
            profileSimpleRes = new ProfileSimpleRes(
                    commentedMember.getNickName(),
                    commentedMember.getProfilePhoto() == null ? null : commentedMember.getProfilePhoto().getS3Path(),
                    commentedMember.getId()
            );
            isLiked = commentLikeService.isLikedComment(member, comment);
            commentRes.add(new CommentRes(
                    comment.getId(),
                    comment.getBody(),
                    comment.getCreateDate(),
                    comment.getLastModifiedDate(),
                    comment.getReviewPost().getId(),
                    isLiked,
                    commentLikeService.countLike(comment),
                    comment.getFixFlag(),
                    profileSimpleRes
            ));
        }
        cursorId = comments.isEmpty()?
                null:comments.getContent().get(comments.getContent().size()-1).getId();
        return new CursorResult<>(commentRes, cursorId, comments.hasNext());
    }

    public Page<Comment> getCommentsByReviewId(Long reviewId, Long cursorId, String sort, Pageable page){
        return cursorId == null?
                commentRepository.findFirstCommentPage(reviewId, sort, page):
                commentRepository.findCommentPageByCursorId(reviewId, cursorId, sort, page);
    }

    /* 댓글 고정 */
    public Comment getFixedCommentByReviewId(Long reviewId){
        return commentRepository.findFixedCommentByReviewId(reviewId);
    }

    public void changeFixedComment(Long reviewId, Long commentId){
        Comment fixedComment = getFixedCommentByReviewId(reviewId);
        if(fixedComment != null){
            commentRepository.updateFixFlag(fixedComment.getId(), false);
        }
        commentRepository.updateFixFlag(commentId, true);
    }

    public void unpinComment(Long reviewId) {
        Comment fixedComment = getFixedCommentByReviewId(reviewId);
        if(fixedComment != null) commentRepository.updateFixFlag(fixedComment.getId(), false);
    }
}
