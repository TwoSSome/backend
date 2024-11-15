package towssome.server.repository.viewlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;

import java.util.List;
import java.util.Set;


public interface ViewLikeRepository extends JpaRepository<ViewLike,Long> {
    ViewLike findByReviewPostIdAndMemberId(Long reviewId, Long memberId);
    ViewLike findByReviewPostAndMember(ReviewPost reviewPost, Member member);
    List<ViewLike> findAllByReviewPost(ReviewPost reviewPost);
    List<ViewLike> findAllByMember(Member member);

    @Query("SELECT v.reviewPost FROM ViewLike v WHERE v.member = :member AND v.viewFlag = true")
    Set<ReviewPost> findViewedPostsByMember(@Param("member") Member member);

    @Query("SELECT v.reviewPost FROM ViewLike v WHERE v.member = :member AND v.likeFlag = true")
    Set<ReviewPost> findLikedPostsByMember(@Param("member") Member member);
}
