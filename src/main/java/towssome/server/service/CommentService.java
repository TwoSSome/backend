package towssome.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import towssome.server.dto.*;
import towssome.server.entity.Comment;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundCommentException;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.exception.NotFoundReviewPostException;
import towssome.server.repository.CommentRepository;
import towssome.server.repository.CommentRepositoryImpl;
import towssome.server.repository.ReviewPostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final MemberService memberService;

    public void createComment(CommentReq commentReq, String username) {
        Long reviewId = commentReq.reviewPostId();
        Member member = memberService.getMember(username);
        // reviewId로 ReviewPost 조회
        ReviewPost reviewPost = reviewPostRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewPostException("ReviewPost not found with id: " + reviewId));
        Comment comment = new Comment(
                commentReq.body(),
                member,
                reviewPost
        );
        commentRepository.save(comment);
    }

    public Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundMemberException("Comment not found with id: " + commentId));
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundCommentException("해당 코멘트가 존재하지 않습니다."));
    }


    @Transactional
    public void updateComment(Long commentId, CommentUpdateReq req) {
        commentRepository.updateComment(commentId, req.body());
    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    public CursorResult<CommentRes> getCommentPageByReviewId(Long reviewId, Long cursorId, String sort, Pageable page){
        List<CommentRes> commentRes = new ArrayList<>();
        final Page<Comment> comments = getCommentsByReviewId(reviewId, cursorId, sort, page);
        for(Comment comment:comments){
            commentRes.add(new CommentRes(
                    comment.getId(),
                    comment.getBody(),
                    comment.getCreateDate(),
                    comment.getLastModifiedDate(),
                    comment.getMember().getId(),
                    comment.getReviewPost().getId()
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
}
