package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.entity.ReviewPost;

import java.util.List;

public interface HashtagClassificationRepositoryCustom {
    Page<ReviewPost> findReviewsByHashtagOrderBySort(String keyword, String sort, Pageable pageable);

    List<String> findHashtagsByReviewId(Long reviewId);
}
