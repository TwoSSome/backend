package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import towssome.server.entity.ReviewPost;

import java.util.List;

@Repository
public interface ReviewPostRepositoryCustom {

    Page<ReviewPost> findAllByOrderByReviewIdDesc(Pageable page);

    Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, Pageable page);
}
