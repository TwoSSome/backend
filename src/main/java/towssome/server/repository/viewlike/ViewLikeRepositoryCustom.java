package towssome.server.repository.viewlike;

import com.querydsl.core.Tuple;
import org.springframework.stereotype.Repository;
import towssome.server.dto.CursorResult;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;

import java.util.List;

@Repository
public interface ViewLikeRepositoryCustom {
    CursorResult<ReviewPost> findLikeByMemberIdOrderByIdDesc(Long memberId, Long pageId, String sort, int size);

    CursorResult<ReviewPost> findRecentByMemberIdOrderByIdDesc(Long memberId, Long pageId, String sort, int size);

    Long findLikeAmountByReviewPost(Long reviewId);
    Long findViewAmountByReviewPost(Long reviewId);

    List<Tuple> findViewLikesWithScores(Member member, List<Member> clusterMembers);
}
