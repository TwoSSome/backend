package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.HashtagClassification;
import towssome.server.entity.ReviewPost;

import java.util.List;

public interface HashtagClassificationRepository extends JpaRepository<HashtagClassification,Long>,HashtagClassificationRepositoryCustom {
    List<HashtagClassification> findAllByReviewPost(ReviewPost reviewPost);
}