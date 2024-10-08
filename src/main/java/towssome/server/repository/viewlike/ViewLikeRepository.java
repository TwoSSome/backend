package towssome.server.repository.viewlike;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;

import java.util.List;


public interface ViewLikeRepository extends JpaRepository<ViewLike,Long> {
    ViewLike findByReviewPostIdAndMemberId(Long reviewId, Long memberId);
    ViewLike findByReviewPostAndMember(ReviewPost reviewPost, Member member);
    List<ViewLike> findAllByReviewPost(ReviewPost reviewPost);
}
