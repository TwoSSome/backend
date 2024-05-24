package towssome.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import towssome.server.dto.ReviewPostUpdateDto;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;

import java.util.List;

public interface ReviewPostRepository extends JpaRepository<ReviewPost,Long>, ReviewPostRepositoryCustom {
    @Modifying
    @Query(value = "update ReviewPost r set r.body = :#{#dto.body}, r.price = :#{#dto.price} where r.id = :#{#dto.id}")
    void updateReview(@Param("dto") ReviewPostUpdateDto dto);

//    List<ReviewPost> findAllByOrderByIdDesc(Pageable page);
//
//    List<ReviewPost> findByIdLessThanOrderByIdDesc(Long id, Pageable page);
//
//    Boolean existsByIdLessThan(Long id);

    List<ReviewPost> findAllByMember(Member member);

//    @Query("select r from ReviewPost r where r.member.id = :memberId order by r.id desc")
//    List<ReviewPost> findMyPostAllByMemberId(@Param("memberId") Long memberId, Pageable page);

//    @Query("select r from ReviewPost r where r.member.id = :memberId and r.id < :cursorId order by r.id desc")
//    List<ReviewPost> findByMemberIdLessThanOrderByIdDesc(@Param("memberId") Long memberId, @Param("cursorId") Long cursorId, Pageable page);

}
