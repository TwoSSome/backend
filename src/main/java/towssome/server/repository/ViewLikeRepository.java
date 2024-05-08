package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.ViewLike;


public interface ViewLikeRepository extends JpaRepository<ViewLike,Long> {
    ViewLike findByReviewPostIdAndMemberId(Long reviewId, Long memberId);
}
