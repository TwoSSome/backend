package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import towssome.server.entity.ReviewPost;

public interface HashtagClassificationRepositoryCustom {
    Page<ReviewPost> findReviewsByHashtagOrderBySort(String keyword, String sort, Pageable pageable);
}
