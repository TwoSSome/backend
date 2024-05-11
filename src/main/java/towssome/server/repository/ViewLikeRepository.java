package towssome.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;

import java.util.List;


public interface ViewLikeRepository extends JpaRepository<ViewLike,Long> {
    ViewLike findByReviewPostIdAndMemberId(Long reviewId, Long memberId);

    @Query("select v.reviewPost.id from ViewLike v where v.member.id = :member_id and v.likeFlag = true")
    List<Long> findByMemberIdAndLikeFlag(Long member_id);

    @Query("select rp from ViewLike v join v.reviewPost rp where v.member.id = :memberId and v.likeFlag = true order by v.id desc")
    List<ReviewPost> findLikeByMemberIdOrderByIdDesc(@Param("memberId") Long memberId, Pageable page);

    @Query("select rp from ViewLike v join v.reviewPost rp where v.member.id = :memberId and v.reviewPost.id < :cursorId and v.likeFlag = true order by v.id desc")
    List<ReviewPost> findLikeByIdAndMemberIdLessThanOrderByIdDesc(@Param("cursorId") Long cursorId, @Param("memberId") Long memberId, Pageable page);

    @Query("select rp from ViewLike v join v.reviewPost rp where v.member.id = :memberId and v.viewFlag = true order by v.id desc")
    List<ReviewPost> findAllByMemberIdOrderByIdDesc(@Param("memberId") Long memberId, Pageable page);

    @Query("select rp from ViewLike v join v.reviewPost rp where v.member.id = :memberId and v.reviewPost.id < :cursorId and v.viewFlag = true order by v.id desc")
    List<ReviewPost> findByMemberIdLessThanOrderByIdDesc(Long cursorId, Long memberId, Pageable page);

    ViewLike findByReviewPostAndMember(ReviewPost reviewPost, Member member);

}
