package towssome.server.repository;

import org.springframework.stereotype.Repository;
import towssome.server.dto.CursorResult;
import towssome.server.entity.ReviewPost;

@Repository
public interface ViewLikeRepositoryCustom {
    CursorResult<ReviewPost> findLikeByMemberIdOrderByIdDesc(Long memberId, Long pageId, String sort, int size);

    CursorResult<ReviewPost> findRecentByMemberIdOrderByIdDesc(Long memberId, Long pageId, String sort, int size);

    Long findLikeAmountByReviewPost(Long reviewId);

}
