package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.entity.Comment;

public interface CommentRepositoryCustom {
    Page<Comment> findFirstCommentPage(Long reviewId, String sort, Pageable page);
    Page<Comment> findCommentPageByCursorId(Long reviewId, Long cursorId, String sort, Pageable page);
    Comment findFixedCommentByReviewId(Long reviewId);
}
