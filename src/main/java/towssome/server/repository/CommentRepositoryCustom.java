package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.entity.Comment;

public interface CommentRepositoryCustom {

    Page<Comment> findAllByReviewIdOrderBySort(String sort, Long reviewId, Pageable pageable);
}
