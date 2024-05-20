package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import towssome.server.entity.ReviewPost;

@Repository
public interface ReviewPostRepositoryCustom {

    Page<ReviewPost> findFirstPageByOrderByReviewIdDesc(Boolean recommend, Pageable page);

    Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, Boolean recommend, Pageable page);

    Page<ReviewPost> findMyPostFirstPageByMemberId(Long memberId, String sort, Pageable page);

    Page<ReviewPost> findByMemberIdLessThanOrderByIdDesc(Long memberId, Long cursorId, String sort, Pageable page);
}