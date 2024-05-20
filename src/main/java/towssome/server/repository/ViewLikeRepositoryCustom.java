package towssome.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import towssome.server.entity.ReviewPost;

@Repository
public interface ViewLikeRepositoryCustom {
    Page<ReviewPost> findLikeByMemberIdOrderByIdDesc(Long memberId, String sort, Pageable page);

    Page<ReviewPost> findLikeByIdAndMemberIdLessThanOrderByIdDesc(Long cursorId, Long memberId, String sort, Pageable page);

    Page<ReviewPost> findRecentByMemberIdOrderByIdDesc(Long memberId, String sort, Pageable page);

    Page<ReviewPost> findRecentByMemberIdLessThanOrderByIdDesc(Long cursorId, Long memberId, String sort, Pageable page);
}
