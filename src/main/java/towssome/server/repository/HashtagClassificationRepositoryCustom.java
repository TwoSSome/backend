package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.dto.QuickRecommendReq;
import towssome.server.entity.ReviewPost;

import java.util.List;

public interface HashtagClassificationRepositoryCustom {
    Page<ReviewPost> findFirstReviewPageByHashtag(String keyword, String sort, Pageable pageable);
    Page<ReviewPost> findReviewPageByCursorIdAndHashTag(String keyword, Long cursorId, String sort, Pageable pageable);
    List<String> findHashtagsByReviewId(Long reviewId);
    Page<ReviewPost> findFirstRecommendPageByHashtag(QuickRecommendReq req, String sort, Pageable pageable);
    Page<ReviewPost> findRecommendPageByCursorIdAndHashTag(QuickRecommendReq req, Long cursorId, String sort, Pageable pageable);
}
