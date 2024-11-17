package towssome.server.repository.reviewpost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.dto.CursorResult;
import towssome.server.entity.Calendar;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;

import java.util.List;

public interface ReviewPostRepositoryCustom {

    Page<ReviewPost> findFirstPageByOrderByReviewIdDesc(Boolean recommend, Pageable page);

    Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, Boolean recommend, Pageable page);

    Page<ReviewPost> findMyPostFirstPageByMemberId(Long memberId, String sort, Pageable page);

    Page<ReviewPost> findByMemberIdLessThanOrderByIdDesc(Long memberId, Long cursorId, String sort, Pageable page);

    CursorResult<ReviewPost> findSubscribeReviewList(Member subscriber, Pageable pageable);

    List<Integer> findReviewInCalendarOfMonth(int year, int month, Calendar calendar);

    CursorResult<ReviewPost> getRecommendedReviewsPage(Member member, Pageable pageable);

}
